(ns jobs-dict-api.secapi
   (:require [jobs-dict-api.sqlite :as sqlite]
             [jobs-dict-api.crypt :as crypt]
             [buddy.core.codecs :as codecs]
             [clojure.data.codec.base64 :as b64]
             [next.jdbc :as jdbc]
             [jobs-dict-api.nonce :as nonce]))

(def ^:const str_empty "")

(def settings
    (clojure.edn/read-string (slurp "src/settings.edn")))

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
