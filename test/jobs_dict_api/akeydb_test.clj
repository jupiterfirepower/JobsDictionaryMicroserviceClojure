(ns jobs-dict-api.akeydb-test
  (:require [clojure.test :refer :all]
            [jobs-dict-api.uuid :as gen]
            [jobs-dict-api.akeydb :as ddb]))

(deftest datomic-add-apikey-test
  (is (contains? (ddb/add-apikey (gen/guid))
                 :db-before)))

(deftest datomic-is-apikey-valid-test
  (let [ guid (gen/guid) ]
        (is (contains? (ddb/add-apikey guid)
                 :db-before))
         (is (= true (ddb/is-apikey-valid guid)))))

(deftest datomic-add-apikey-test
  (is (contains? (ddb/add-apikey (gen/guid))
                 :db-before)))

;;(deftest datomic-closedb-test
;;  (is (= (ddb/closedb)) true))
