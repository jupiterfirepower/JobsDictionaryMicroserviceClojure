(ns jobs-dict-api.inceptors
  (:require [io.pedestal.interceptor :as interceptor]
            [jobs-dict-api.uuid :as gen]
            [clojure.data.json :as json]
            [io.pedestal.http.content-negotiation :as content-negotiation]))

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
                                                 "Content-Length" (str content-length) })
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
  (get-in context [:request :accept :field] "text/plain"))

(defn transform-content
  [body content-type]
  (case content-type
    "text/html" body
    "text/plain" body
    "application/edn" (pr-str body)
    "application/json" (json/write-str body)))

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