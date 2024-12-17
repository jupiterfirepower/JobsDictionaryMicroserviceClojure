(ns jobs-dict-api.crypt
  (:require [buddy.core.crypto :as crypto]
            [buddy.core.nonce :as nonce]
            [buddy.core.codecs :as codecs]
            [buddy.core.hash :as hash]
            [clojure.data.codec.base64 :as b64]))

(def ^:private config (clojure.edn/read-string (slurp "src/settings.edn")))

(defn from-base64-bytes [original]
      (b64/decode (.getBytes original)))

(def ^:private aes-iv
    (:aes-iv (:secrets config)))

(def ^:private aes-key
    (:aes-key (:secrets config)))

(def ^:private aes-iv-bytes
    (from-base64-bytes aes-iv))

(def ^:private aes-key-bytes
    (from-base64-bytes aes-key))

(defn encrypt
     [text]
     (crypto/encrypt (codecs/to-bytes text) aes-key-bytes aes-iv-bytes
                               {:algorithm :aes256-cbc-hmac-sha512}))

(defn decrypt
    [encrypted]
    ;; And now, decrypt it using the same parameters:
    (-> (crypto/decrypt encrypted aes-key-bytes aes-iv-bytes {:algorithm :aes256-cbc-hmac-sha512})
        (codecs/bytes->str)))

(defn decrypt-from-str
    [encrypted]
    ;; And now, decrypt it using the same parameters:
    (-> (crypto/decrypt (codecs/to-bytes encrypted) aes-key-bytes aes-iv-bytes {:algorithm :aes256-cbc-hmac-sha512})
        (codecs/bytes->str)))



