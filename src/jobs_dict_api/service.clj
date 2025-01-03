(ns jobs-dict-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor :as interceptor]
            [ring.util.response :as ring-resp]
            [next.jdbc :as jdbc]
            [clojure.data.json :as json]
            ;;[cheshire.core :as chjson]
            ;;[io.pedestal.http.content-negotiation :as content-negotiation]
            [clojure-aes.core :as aes]
            ;;[jobs-dict-api.sqlite :as sqlite]
            [jobs-dict-api.uuid :as gen]
            [clojure.data.codec.base64 :as b64]
            [jobs-dict-api.nonce :as nonce]
            [jobs-dict-api.crypt :as crypt]
            [buddy.core.codecs :as codecs]
            [buddy.core.hash :as hash]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            ;;[clojure.pprint :as p]
            [jobs-dict-api.secapi :as sapi]
            [jobs-dict-api.inceptors :as incp]
            [clojure.core.cache.wrapped :as w]
            [clojure.edn :as edn]
            [clojure.core.async :as async
             :refer [>! <! >!! <!! go chan buffer close! alts!! take!]]
            ))

(def db-config
     (:postgresdb (edn/read-string (slurp "src/settings.edn"))))

(def db (jdbc/get-datasource db-config))

(defn get-headers-new
  [request]
  (let [ ua_valid (sapi/is-useragent-valid request)
         apk (sapi/is-apikey-valid request)
         skk_valid (sapi/is-secret-valid request)
         nonce_valid (sapi/is-nonce-valid request)
         token (sapi/get-keycloak-token request)
         ktoken_valid (sapi/is-keycloak-token-valid token)
       ] 
       ;;(prn request)
       (prn ua_valid)
       (prn apk)
       (prn skk_valid)
       (prn nonce_valid)
       (prn ktoken_valid)
       (every? true? [ua_valid (:valid apk) skk_valid nonce_valid ktoken_valid])))

(defn get-http-status
    [request_valid]
    (cond
      (= request_valid true) 200 ;; pass all security checks.
      (= request_valid false) 400 ;; Bad request failed security checks.(at least one failed)
       :else 500)) ;; Internal Server Error.

(defn response_with_status [request_valid body]
  {:status (get-http-status request_valid) 
   :body (if (= request_valid true) body nil)}) 

(def aeskey (hash/sha512 "myse345dfcretPwd421341sg234231fgvxv3243124asxz"))

(defn get-headers-str
  [request]
  (let [guid   (gen/guid)
        guids  (clojure.string/replace guid #"-" "")
       ]
  (prn (format "nonce-%s"(sapi/to-base64-from-codec (.getBytes (nonce/get-nonce)))))
  ;;()
  (prn (sapi/to-base64-from-bytes aeskey))
  (prn (format "encrypted-%s"(sapi/to-base64-from-codec "16de814afaf3a815a8b6a9e99410c5c8" aeskey)))
  (prn (format "encrypted-%s"(sapi/to-base64-from-codec "9478b869b379427b48d5e76eeca02dcc" aeskey)))))

;;(defn wtype-data
;;  [request]
;;  (prn (:identity request))
;;  (let [req_valid (get-headers-new request)
;;        req_auth (authenticated? request)
;;        result (if (and (= req_valid true) (= req_auth true)) (jdbc/execute! db ["select * from fn_get_worktypes()"]) nil)]
;;        (response_with_status req_valid (json/write-str result))))

(def cache-data (w/fifo-cache-factory { :wtype (jdbc/execute! db ["select * from fn_get_worktypes();"])
                                        :etype (jdbc/execute! db ["select * from fn_get_empltypes();"]) }))

(defn wtype-data
  [request]
  (prn request)
  (prn (:identity request))
  (prn ":req_valid - " (:req_valid request))
  (let [req_auth (authenticated? request)
        result (if (= req_auth true) (w/lookup cache-data :wtype) nil)]
        (response_with_status req_auth (json/write-str result))))

;;(defn wtype-data
; ; [request]
 ;; (prn (:identity request))
 ;; (let [req_auth (authenticated? request)
  ;;      result (if (= req_auth true) (jdbc/execute! db ["select * from fn_get_worktypes()"]) nil)]
   ;;     (response_with_status req_auth (json/write-str result))))
    
(defn etype-data
  [request]
  (let [req_valid (get-headers-new request)
        result (if (= req_valid true) (jdbc/execute! db ["select * from fn_get_empltypes()"]) nil)]
      (response_with_status req_valid (json/write-str result))))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body]) ;; incp/rate-limit-interceptor
(def custom-interceptors [ incp/rate-limit-interceptor incp/coerce-body-interceptor incp/content-negotiation-interceptor 
                           incp/check-headers-interceptor incp/validate-headers-interceptor incp/authentication-interceptor 
                           (body-params/body-params) incp/content-length-json-body ])

;; Tabular routes
(def routes #{;;["/" :get (conj common-interceptors `home-page)]
              ["/wtypes" :get (conj custom-interceptors `wtype-data)]
              ["/etypes" :get (conj custom-interceptors `etype-data)]})

;; Consumed by jobs-dict-api.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        ;;:h2? true
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;;:http3 true  ;; enable http/3 support
                                        ;;:http3-pem-work-directory "/some/path" ;; a pre-created directory for quic configuration
                                        ;;:ssl-port 5443 ;; ssl-port is used by http/3
                                        ;;:http2 true
                                        :ssl-port 8443
                                        :ssl? true
                                        :keystore "resources/devjetty.jks"
                                        :key-password "dfvgbh12345"
                                        :keystore-type "jks"
                                        ;; Alternatively, You can specify your own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        }})
