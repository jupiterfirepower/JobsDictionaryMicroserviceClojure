(ns jobs-dict-api.nonce-test
  (:require [clojure.test :refer :all]
            [jobs-dict-api.nonce :as nonce]))

(deftest get-nonce-test
  (let [nonce (nonce/get-nonce)
        cnt (count (re-seq #"-" nonce))]
        (is (= cnt 2))))

(deftest get-nonce-test
  (let [nonce (nonce/get-nonce)
        cnt (count (re-seq #"-" nonce))
        valid (nonce/is-nonce-valid nonce)
        ]
        (is (= cnt 2))
        (is (= valid true))))

(deftest is-nonce-valid-false-test
  (let [nonce "123124124"
        valid (nonce/is-nonce-valid nonce)]
        (is (= valid false))))

(deftest is-nonce-valid-false-test-second
  (let [nonce ""
        valid (nonce/is-nonce-valid nonce)]
        (is (= valid false))))

(deftest is-nonce-valid-true-test
  (let [nonce (nonce/get-nonce)
        valid (nonce/is-nonce-valid nonce)]
        (is (= valid true))))