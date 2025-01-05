(ns jobs-dict-api.inceptors
  (:require [io.pedestal.interceptor :as interceptor]
            [jobs-dict-api.uuid :as gen]
            [clojure.data.json :as json]
            [io.pedestal.http.content-negotiation :as content-negotiation]
            [io.staticweb.rate-limit.middleware :refer [ip-rate-limit wrap-rate-limit]]
            [io.staticweb.rate-limit.storage :as storage]
            [io.staticweb.rate-limit.quota-state :as quota-state]
            ;;[buddy.auth :as auth]
            [buddy.auth.backends :as auth.backends]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.middleware :as auth.middleware]
            [jobs-dict-api.secapi :as sapi]
            ))

(def content-length-json-body
  (interceptor/interceptor
    {:name ::content-length-json-body
     :leave (fn [context]
              (let [response (:response context)
                    body (:body response)
                    ;; json-response-body (if body (chjson/generate-string body) "")
                    json-response-body (if body (json/write-str body) "")
                    
                    ;; Content-Length is the size of the response in bytes
                    ;; Let's count the bytes instead of the string, in case there are unicode characters
                    content-length (count (.getBytes ^String json-response-body))
                    headers (:headers response { "Content-Type" "application/json;charset=utf-8"
                                                 "Content-Length" (str content-length) 
                                                 ;;"X-Content-Type-Options" "nosniff"
                                                 ;;"X-Frame-Options" "deny"
                                                 ;;"X-XSS-Protection" "0"
                                                 ;;"Cache-Control" "no-store"
                                                 ;;"Content-Security-Policy" "script-src 'self'; default-src 'none'; frame-ancestors 'none'; sandbox"
                                                 ;;"Strict-Transport-Security" "max-age=15552001; includeSubDomains; preload; redirectHttpToHttps=true"
                                                })
                    newapikey (gen/guid)
                    ]
                (assoc context
                       :response {:status (:status response)
                                  :body json-response-body
                                  :headers (merge headers (if (= (:status response) 200) { "x-api-key" newapikey } {}))
                                                 ;; {;;"Content-Type" "application/json;charset=utf-8"
                                                   ;;"Content-Length" (str content-length)
                                                   ;;"x-api-key" newapikey})
                                                   })))}))

(def supported-types ["application/json"
                      "application/edn"]) 

(def content-negotiation-interceptor
  (content-negotiation/negotiate-content supported-types))

(defn accepted-type
  [context]
  (get-in context [:request :accept :field] "application/json"))

(defn transform-content
  [body content-type]
  (case content-type
    "application/json" (json/write-str body)
    "application/edn" (pr-str body)))

(defn coerce-to
  [response content-type]
  (-> response
      (update :body transform-content content-type)
      (assoc-in [:headers "Content-Type"] content-type)))

(def coerce-body-interceptor
  {:name ::coerce-body
   :leave
   (fn [context]
     (cond-> context
       (nil? (get-in context [:response :headers "Content-Type"])) 
       (update-in [:response] coerce-to (accepted-type context))))})

(def settings
    (clojure.edn/read-string (slurp "resources/settings.edn")))

(def ratelimit-settings
     (:ratelimit settings))

(def storage (storage/local-storage))

;; Define the rate limit: 1 req/s per IP address
(def limit (ip-rate-limit :limit-id (Long/parseLong (:limit-id ratelimit-settings)) (java.time.Duration/ofSeconds 60))) ;;(t/seconds 5)

;; Define the middleware configuration
(def rate-limit-config {:storage storage :limit limit})

(def rate-limit-interceptor
  "Adds rate limit info to context."
  (interceptor/interceptor
    {:name ::rate-limit
     :enter (fn [context]
              (let [quota-state (quota-state/read-quota-state storage limit (:request context))]
                (if (quota-state/quota-exhausted? quota-state)
                  (assoc context :response (quota-state/build-error-response quota-state (:response context)))
                  (do
                    (quota-state/increment-counter quota-state storage)
                    (assoc context :rate-limit-details (quota-state/rate-limit-response quota-state {}))))))}))

(defn keycloak-authfn
  [req token]
  (let [token (sapi/get-keycloak-token req)
        user_email (sapi/get-keycloak-user-by-token token) ]
      user_email))

;; Create an instance of auth backend.

(def auth-backend
  (token-backend {:authfn keycloak-authfn :token-name "Bearer"})) ;;


(def authentication-interceptor
  "Port of buddy-auth's wrap-authentication middleware."
  (interceptor/interceptor
   {:name ::authenticate
    :enter (fn [context]
             (update context :request auth.middleware/authentication-request auth-backend))}))

(def check-headers-interceptor
  "Check headers keys."
  (interceptor/interceptor
    {:name ::check-headers
     :enter (fn [context]
              (let [request (:request context)
                    headers (:headers (:request context))
                    is_user_agent (contains? headers "user-agent")
                    is_auth (contains? headers "authorization")
                    is_x_api_key (contains? headers "x-api-key")
                    is_x_api_secret (contains? headers "x-api-secret")
                    is_s_nonce (contains? headers "s-nonce")
                    is_valid (every? true? [is_user_agent is_auth is_x_api_key is_x_api_secret is_s_nonce])
                    req (assoc request :req_valid is_valid)
                    ]
                (assoc-in context [:request] req)))}))

(def validate-headers-interceptor
  "Validate headers values."
  (interceptor/interceptor
    {:name ::validate-headers
     :enter (fn [context]
              (let [request (:request context)
                    req_valid (:req_valid request)
                    is_req_valid (sapi/validate-headers-value request)
                    req (assoc request :req_valid (and req_valid is_req_valid))
                    ]
                (assoc-in context [:request] req)))}))
