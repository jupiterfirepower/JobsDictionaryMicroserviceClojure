(ns jobs-dict-api.secapi
   (:require [jobs-dict-api.sqlite :as sqlite]
             [jobs-dict-api.crypt :as crypt]
             [buddy.core.codecs :as codecs]
             [clojure.data.codec.base64 :as b64]
             [next.jdbc :as jdbc]
             [keycloak.deployment :as kc-deploy :refer [deployment client-conf]]
             [keycloak.backend :as kc-backend]
             [jobs-dict-api.nonce :as nonce]))

(def ^:const str_empty "")

(def settings
    (clojure.edn/read-string (slurp "src/settings.edn")))

(def keycloak-settings
     (:keycloak settings))

;;(def ^:const str_empty "")

(def keycloak-deployment (kc-deploy/deployment (kc-deploy/client-conf {:auth-server-url  (:auth-server-url keycloak-settings)
                                                                       :admin-realm      (:admin-realm keycloak-settings)
                                                                       :realm            (:realm keycloak-settings)
                                                                       :admin-username   (:admin-username keycloak-settings)
                                                                       :admin-password   (:admin-password  keycloak-settings)
                                                                       :client-admin-cli (:client-admin-cli  keycloak-settings)
                                                                       :client-id        (:client-id  keycloak-settings)
                                                                       :client-secret    (:client-secret  keycloak-settings)})))

(defn is-keycloak-token-valid [token]
      (try
          (let [ extracted_token (kc-backend/verify-then-extract keycloak-deployment token)
                 user_name (:username extracted_token) 
                 user_email (:email extracted_token) ] 
            ;;(prn extracted_token)
            (prn (format "user_name-%s, email: %s" user_name user_email))
          true)
     (catch IllegalArgumentException e
            (prn "catch e IllegalArgumentException: " e) 
            false)
     (catch org.keycloak.exceptions.TokenNotActiveException e
            (prn "catch e TokenNotActiveException: " e) 
            false)
     (catch org.keycloak.exceptions.TokenVerificationException e
            (prn "catch e TokenVerificationException: " e) 
            false)
     (catch org.keycloak.exceptions.TokenSignatureInvalidException e
            (prn "catch e TokenSignatureInvalidException: " e) 
            false)       
     (catch clojure.lang.ExceptionInfo e
            (prn "catch e clojure.lang.ExceptionInfo: " e) 
            false)
     (catch Exception e 
            (prn "catch e Exception: " e) 
            false)))

(defn get-keycloak-user-by-token [token]
      (try
          (let [ extracted_token (kc-backend/verify-then-extract keycloak-deployment token)
                 user_name (:username extracted_token) 
                 user_email (:email extracted_token) ] 
            ;;(prn extracted_token)
            (prn (format "user_name-%s, email: %s" user_name user_email))
          user_email)
     (catch IllegalArgumentException e
            (prn "catch e IllegalArgumentException: " e) 
            nil)
     (catch org.keycloak.exceptions.TokenNotActiveException e
            (prn "catch e TokenNotActiveException: " e) 
            nil)
     (catch org.keycloak.exceptions.TokenVerificationException e
            (prn "catch e TokenVerificationException: " e) 
            nil)
     (catch org.keycloak.exceptions.TokenSignatureInvalidException e
            (prn "catch e TokenSignatureInvalidException: " e) 
            nil)       
     (catch clojure.lang.ExceptionInfo e
            (prn "catch e clojure.lang.ExceptionInfo: " e) 
            nil)
     (catch Exception e 
            (prn "catch e Exception: " e) 
            nil)))

(def secret-key-value
    (:dictsecret (:secrets settings)))

(def user-agent-value
    (:user-agent (:secrets settings)))

(defn from-base64-bytes [original]
      (b64/decode (.getBytes original)))

(defn to-base64-from-bytes
      [bytes]
          (codecs/bytes->str (b64/encode bytes)))

(defn to-base64-from-codec
      [text]
      (let [encrypted (crypt/encrypt text) ]
            (codecs/bytes->str (b64/encode encrypted))))

(defn is-useragent-valid
  [request]
  (let [ result (:headers request)
         uagent (if (contains? result "user-agent") (result "user-agent") str_empty)
         ua_valid (= uagent user-agent-value)
       ] 
       (prn (format "uagent-%s" uagent))
       ua_valid))

(defn get-keycloak-token
  [request]
  (try
     (let [ result (:headers request)
            token_bearer (if (contains? result "authorization") (result "authorization") str_empty)
            token (second (clojure.string/split  token_bearer #" "))
          ] 
       (prn (format "token-%s" token))
       token)
     (catch clojure.lang.ExceptionInfo e
            str_empty)
     (catch Exception e ;;(prn "catch e: " e)
            str_empty)))

(defn is-apikey-valid
  [request]
  (try
     (let [ result (:headers request)
            apikey (if (contains? result "x-api-key") (result "x-api-key") str_empty)
            apikeyb (from-base64-bytes apikey)
            akey (if (> (.length apikey) 0) (crypt/decrypt apikeyb) str_empty)
            apk_valid (sqlite/is-apikey-valid akey)
          ] 
       (prn (format "akey-%s" akey))
       {:akey akey :valid apk_valid})
     (catch clojure.lang.ExceptionInfo e
            {:akey "incorrect apikey not decrypted" :valid false})
     (catch Exception e ;;(prn "catch e: " e)
            {:akey "incorrect apikey not decrypted" :valid false})))

(defn is-nonce-valid
  [request]
  (try
     (let [ result (:headers request)
            snonce (if (and (not= result nil) (contains? result "s-nonce")) (result "s-nonce") str_empty)
            nonceb (from-base64-bytes snonce)
            nonce (if (> (.length snonce) 0) (crypt/decrypt nonceb) str_empty)
            nonce_valid (nonce/is-nonce-valid nonce)
          ]
       (prn (format "nonce-%s" nonce))
       nonce_valid)
     (catch clojure.lang.ExceptionInfo e ;;(prn "catch e: " e)
     ;;                                    (prn "incorrect nonce not decrypted")
                                         false)
     (catch Exception e ;;(prn "catch e: " e)
                                        false)))

(defn is-secret-valid
  [request]
  (try
     (let [ result (:headers request)
            ssecret (if (and (not= result nil) (contains? result "x-api-secret")) (result "x-api-secret") str_empty)
            secretb (from-base64-bytes ssecret)
            secret (if (> (.length ssecret) 0) (crypt/decrypt secretb) str_empty)
            skk_valid (= secret secret-key-value)
         ]   
       (prn (format "secret-%s" secret))
       skk_valid)
     (catch clojure.lang.ExceptionInfo e ;;(prn "catch e: " e)
                                        (prn "incorrect secret not decrypted")
                                        false)
     (catch Exception e false)))

(defn validate-headers-value
  [request]
  (let [ ua_valid (is-useragent-valid request)
         apk (is-apikey-valid request)
         skk_valid (is-secret-valid request)
         nonce_valid (is-nonce-valid request)
         token (get-keycloak-token request)
         ktoken_valid (is-keycloak-token-valid token)
       ] 
       ;;(prn request)
       (prn ua_valid)
       (prn apk)
       (prn skk_valid)
       (prn nonce_valid)
       (prn ktoken_valid)
       (every? true? [ua_valid (:valid apk) skk_valid nonce_valid ktoken_valid])))
