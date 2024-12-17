(ns jobs-dict-api.sqlite-test
  (:require [clojure.test :refer :all]
            [jobs-dict-api.uuid :as g]
            [jobs-dict-api.sqlite :as sqlite]
            ))

(deftest add-apikey-test
  (sqlite/sqlite-start)
  (let [res_count (sqlite/add-apikey (g/guid)) ]
        (is (= (first res_count) 1))
        (sqlite/sqlite-stop)))

(deftest add-del-apikey-test
  (sqlite/sqlite-start)
  (let [ guid (g/guid)
         res_count (sqlite/add-apikey guid) 
         del_count (sqlite/del-apikey guid)]
        (is (= (first res_count) 1))
        (is (= (first del_count) 1))
        (sqlite/sqlite-stop)))

(deftest valid-apikey-test
  (sqlite/sqlite-start)
  (let [ guid (g/guid)
         res_count (sqlite/add-apikey guid) 
         valid (sqlite/is-apikey-valid guid)]
        (is (= (first res_count) 1))
        (is (= valid true))
        (sqlite/sqlite-stop)))

(deftest add-many-apikey-test
  (sqlite/sqlite-start)
  (let [ guid (g/guid)
         res_count (sqlite/add-apikey guid) 
         del_count (sqlite/del-apikey guid)]
        (is (= (first res_count) 1))
        (is (= (first del_count) 1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (is (= (first (sqlite/add-apikey (g/guid)))  1))
        (sqlite/sqlite-stop)))