(ns jobs-dict-api.uuid
  (:require [clojure.string :as string]
            [secrets.core]
            [secrets.tools]
            [secrets.constants]))

(import 'java.security.SecureRandom 'java.util.Base64)


(defn- char-range [lo hi]
  (range (int lo) (inc (int hi))))

(def ^:private alpha-numeric
  (map char (concat (char-range \a \z)
                    (char-range \A \Z)
                    (char-range \0 \9))))

(def ^:private hex
  (map char (concat (char-range \a \f)
                    (char-range \0 \9))))

(defn- generate-code [length pool]
  (apply str
         (take length
               (repeatedly #(rand-nth pool)))))

(def ^:private uuid-groups-substrings
  [[0 8]
   [8 12]
   [12 16]
   [16 20]
   [20 32]])

(defn- uuid []
  (let [chars  (generate-code 32 hex)
        groups (map #(subs chars (first %) (second %))
                    uuid-groups-substrings)]
    (string/join "-" groups)))

(let [random (SecureRandom.)
      base64 (.withoutPadding (Base64/getUrlEncoder))]
  (defn generate-token []
    (let [buffer (byte-array 32)]
      (.nextBytes random buffer)
      (.encodeToString base64 buffer)))) 

(defn uid []
      (uuid))

(defn rand-str [len]
    (apply str (for [i (range len)] (char (+ (rand 26) 65)))))

(defn guid []
      (str (random-uuid)))

(defn juuid []
      (str (java.util.UUID/randomUUID)))

(def ^:private uuid4-marker-index 14) ; abcdefab-cdef-4abc-...
(def ^:private uuid4-marker "4")      ;               ^

(defn- uuid4 []  ;; uuid version 4 (1 - 7)
  (let [uuid   (uuid)
        before (subs uuid 0 uuid4-marker-index)
        after  (subs uuid (inc uuid4-marker-index))]
    (str before uuid4-marker after)))

(def ^:private rng (SecureRandom.))

(defn random-str [len]
     (let [ empty_str ""
            passwd (char-array len)
	          chars (map char (concat (range 48 58) ; 0-9
	                          (range 65 91) ; A-Z
	                          (range 97 123) ; a-z
		                        [\~ \! \@ \# \$ \% \^ \& \* \( \) \- \+ \=]))
	          nchars (count chars)]
	          (clojure.string/join empty_str
		                   (mapcat (fn [char]
		                            (str (nth chars (.nextInt rng nchars))))
		                            passwd))))

(defn get-hex-token [len]
    (secrets.core/token-hex len))