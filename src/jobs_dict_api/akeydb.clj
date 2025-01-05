(ns jobs-dict-api.akeydb
    (:require [datomic.api :as d]))

;; jobs-dict-api.akeydb ONLY FOR UNIT TESTS
;; for production use sqlite. Datomic not support delete data. Datalog.

;;(require '[datomic.api :as d])
(def db-uri "datomic:mem://akeys")
;; :db/id 1
(def one-minute-in-miliseconds 60000)

(def ^:private config (clojure.edn/read-string (slurp "resources/settings.edn")))

(def ^:private akey-window
    (Long/parseLong (:akey-window (:secrets config))))

(d/create-database db-uri)

(def conn (d/connect db-uri))

(def data-schema [{:db/ident :data/akey
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/index true
                    :db/fulltext false
                    :db/doc "Session Api Key"}

                   {:db/ident :data/expired
                    :db/valueType :db.type/long
                    :db/cardinality :db.cardinality/one
                    :db/doc "Api Key Expiration DateTime"}])

@(d/transact conn data-schema)

(def test-dataset [ { :data/akey "16de814afaf3a815a8b6a9e99410c5c8"
                      :data/expired (+ (System/currentTimeMillis) (* akey-window one-minute-in-miliseconds))} 
                    { :data/akey "17mn814afaf3a815a8b6a9e99410c5c8"
                      :data/expired (+ (System/currentTimeMillis) (* akey-window one-minute-in-miliseconds))}])

@(d/transact conn test-dataset)

(def all-akeys-q '[ :find ?akey ?expired
                    :where [?data :data/akey ?akey]
                           [?data :data/expired ?expired]
                           ])
(defn get-data-by-akey-q
      [current_akey]
      (format "[ :find ?akey ?expired
                 :where [?data :data/akey ?akey]
                        [?data :data/expired ?expired]
                        [?data :data/akey \"%s\"]]" current_akey))

(d/q all-akeys-q (d/db conn))
(d/q (get-data-by-akey-q "16de814afaf3a815a8b6a9e99410c5c8") (d/db conn))

(defn add-apikey
   [apikey]
   (let [current-data [ { :data/akey apikey
                          :data/expired (+ (System/currentTimeMillis) (* akey-window one-minute-in-miliseconds))} ]]
        @(d/transact conn current-data)))

(defn is-apikey-valid
      [akey]
      (let [ result (d/q (get-data-by-akey-q akey) (d/db conn))
             apikey (first (first result))
             expired (second (first result))
           ]
          (if (and (= akey apikey) (<= (System/currentTimeMillis) expired)) true false)))

(defn closedb []
      (d/delete-database db-uri))
;;