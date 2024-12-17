(ns jobs-dict-api.secapi-test
  (:require [clojure.test :refer :all]
            [jobs-dict-api.sqlite :as sqlite]
            [jobs-dict-api.secapi :as sapi]))

(deftest is-useragent-valid-false-test
  (is (= (sapi/is-useragent-valid {:headers { "user-agent" "4353451344513" }})
                 false)))

(deftest is-useragent-valid-false-test-second
  (is (= (sapi/is-useragent-valid {:headers { }})
                 false)))


(deftest is-useragent-valid-false-test-third
  (is (= (sapi/is-useragent-valid nil)
                 false)))

(deftest is-useragent-valid-true-test
  (is (= (sapi/is-useragent-valid {:headers { "user-agent" "JobsSpecialUserAgent" }})
                 true)))

(deftest is-apikey-valid-false-test-second
  (is (= (:valid (sapi/is-apikey-valid {:headers { }}))
                 false)))

(deftest is-apikey-valid-false-test-third
  (is (= (:valid (sapi/is-apikey-valid nil))
                 false)))

(deftest is-apikey-valid-true-test
  (sqlite/sqlite-start)
  (let [request { :headers { "x-api-key" "VBPIIxCie9VjWpMTs5JF8vg3WJlXROcEj/DS4yjOnNjzEy845O9glH7GSSJmaW0NUfjTWZYtTgIxOikh7KxBVXQjgMyETbXVkmFHGsOz5oE=" }} ]
       (is (= (:valid (sapi/is-apikey-valid request)) true)))
    (sqlite/sqlite-stop))

(deftest is-apikey-valid-false-test
  (sqlite/sqlite-start)
  (let [request {:headers { "x-api-key" "4353451344513" }}
        result (sapi/is-apikey-valid request)
       ]
    (is (= (result :valid) false))
  )
  (sqlite/sqlite-stop))

(deftest is-nonce-valid-false-test
  (is (= (sapi/is-nonce-valid {:headers { }}) false)))

(deftest is-nonce-valid-false-test-third
  (is (= (sapi/is-nonce-valid nil) false)))

(deftest is-nonce-valid-false-test-second
  (is (= (sapi/is-nonce-valid {:headers { "s-nonce" "4353451344513" }}) false)))

(deftest is-nonce-valid-true-test
  (is (= (sapi/is-nonce-valid {:headers { "s-nonce" "LzgH9sZy3zTshDjwsXF4LyHNlRjbf4EGzfHvAhT6g9nLHexRLEOEZKpzH53kZFQzo0LZCu+FVVfTNtWBPBV4NK5hBSUXoMVY14nFKPMMnwcTcsTx2TAxMmMoW9ZmnevU" }}) true)
                 ))

(deftest is-secret-valid-false-test
  (is (= (sapi/is-secret-valid {:headers { }}) false)))

(deftest is-secret-valid-false-test-third
  (is (= (sapi/is-secret-valid nil) false)))

(deftest is-secret-valid-false-test-second
  (is (= (sapi/is-secret-valid {:headers { "x-api-secret" "435345134451" }}) false)))

(deftest is-secret-valid-true-test
  (is (= (sapi/is-secret-valid {:headers { "x-api-secret" "xjVuDYqL/kVyHMBrkZLMLDvEbvY6bSgf09QZWk4z/hirdC2W1559feHepwwe4LLNiCmzB/QunzlnQAtzXQsHbwNBJHKpI323DbPB5iTMxVo=" }}) true)))
