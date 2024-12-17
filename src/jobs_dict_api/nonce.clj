(ns jobs-dict-api.nonce
  (:require [clojure.string :as str]))

(def ^:private ticks-per-second 10000000)
(def ^:private ticks-per-milisecond 10000)
(def ^:private ticks-at-epoch 621355968000000000)

(defn ^:private sign-for-nonce
    [nonce]
    (* (double nonce) Math/PI Math/E))

(defn ^:private sum-sign-for-nonce
    [nonce]
    (let [ rsfirst (Math/ceil (* nonce Math/PI))
           rssecond (Math/ceil (* nonce Math/E)) ]
           (+ rsfirst rssecond)))

(defn ^:private sign-for-nonce
    [nonce]
    (+ nonce 100 200))

(defn ^:private sum-sign-for-nonce
    [nonce]
    (let [ rsfirst (- nonce 12334)
           rssecond (+ nonce 31452234) ]
           (+ rsfirst rssecond)))

(defn ^:private is-signed-nonce-valid
    [snonce]
    (try
        (let  [ splitted (str/split snonce #"-") 
                reverse  (apply str (reverse (first splitted)))
                ;;reverse  (first splitted)
                nonce    (Long/parseLong reverse)
                signs    (Long/parseLong (second splitted))
                signsSum (Long/parseLong (clojure.core/nth splitted 2))
                roundedSign (sign-for-nonce nonce)
                roundedSumSign (sum-sign-for-nonce nonce)
                ticks    (+ (* (System/currentTimeMillis) ticks-per-milisecond) ticks-at-epoch)
                diff     (- ticks nonce)
                seconds  (/ diff ticks-per-second) ]
            ;;(prn "reverse " reverse)
            ;;(prn "nonce " nonce)
            ;;(prn "signs " signs)
            ;;(prn "signs " roundedSign)
            ;;(prn "1 " (= signs roundedSign))
            ;;(prn "2 " (= signsSum roundedSumSign))
            (if (and (= signs roundedSign) (= signsSum roundedSumSign) ) true false)) ;; (> diff 0) (<= seconds 5)
    (catch Exception e false)))

(defn get-nonce
      []
      (let [ ticks (+ (* (System/currentTimeMillis) ticks-per-milisecond) ticks-at-epoch)
             sign (sign-for-nonce ticks)
             sumSign (sum-sign-for-nonce ticks)
           ]
           (format "%s-%s-%s" (apply str (reverse (str ticks))) (str sign) (str sumSign))))

(defn is-nonce-valid
    [snonce]
    (if (and (> (.length snonce) 0) (= (count (re-seq #"-" snonce)) 2)) (is-signed-nonce-valid snonce) false))

