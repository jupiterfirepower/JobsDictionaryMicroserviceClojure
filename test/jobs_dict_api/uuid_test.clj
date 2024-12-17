(ns jobs-dict-api.uuid-test
  (:require [clojure.test :refer :all]
            [jobs-dict-api.uuid :as g]))

(deftest uuid-gen-test
  (let [guid (g/uid)
        cnt (count (re-seq #"-" guid))]
        (is (= cnt 4))))

(deftest guid-gen-test
  (let [guid (g/guid)
        cnt (count (re-seq #"-" guid))]
        (is (= cnt 4))))

(deftest rand-str-test
  (let [rstr (g/rand-str 32)
        length (.length rstr)]
        (is (= length 32))))

(deftest rand-str-test-second
  (let [rstr (g/rand-str 16)
        length (.length rstr)]
        (is (= length 16))))

(deftest get-hex-token-test
  (let [rstr (g/get-hex-token 64)
        length (.length rstr)]
        (is (= length 128))))

(deftest generate-token-test
  (let [rstr (g/generate-token)
        length (.length rstr)]
        (is (= length 43))))

(deftest random-str-test
  (let [rstr (g/random-str 32)
        length (.length rstr)]
        (is (= length 32))))