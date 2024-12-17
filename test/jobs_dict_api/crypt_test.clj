(ns jobs-dict-api.crypt-test
  (:require [clojure.test :refer :all]
            [jobs-dict-api.crypt :as crypt]))

(def original_text "Hello World.")

(deftest encrypt-decrypt-test
  (is (=
        (crypt/decrypt (crypt/encrypt original_text))
        original_text)))