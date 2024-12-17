(ns jobs-dict-api.passwdb
   (:require [datomic.api :as d]))

(def db-uri "datomic:mem://pwd")

(d/create-database db-uri)

(def conn (d/connect db-uri))
(def data-schema [{:db/ident :data/email
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/index true
                    :db/fulltext false
                    :db/doc "User Email"}

                   {:db/ident :data/password
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/index true
                    :db/fulltext false
                    :db/doc "User Password"}])

@(d/transact conn data-schema)

(def test-dataset [{:data/email "test@data.com"
                      :data/password "testpwd"}
                     {:data/email "test1@data.com"
                      :data/password "test1pwd"}
                     {:data/email "test2@data.com"
                      :data/password "test2pwd"}])

@(d/transact conn test-dataset)

(def all-email-q '[:find ?email ?password
                    :where [?data :data/email ?email]
                           [?data :data/password ?password]
                           ])

(def all-email-q '[:find ?email ?password
                    :where [?data :data/email ?email]
                           [?data :data/password ?password]
                           ])

(def get-by-email-q '[:find ?email ?password
                    :where [?data :data/email ?email]
                           [?data :data/password ?password]
                           [?data :data/email "test@data.com"]
                           ])
(defn get-by-emeil-q-p
     [email]
     (format "[:find ?email ?password
               :where [?data :data/email ?email]
                      [?data :data/password ?password]
                      [?data :data/email \"%s\"]]" email))

(defn get-password-by-email
      [email]
      (let [data (d/q (get-by-emeil-q-p email) (d/db conn))]
         (second (first data))))

(d/q all-email-q (d/db conn))
(d/q get-by-email-q (d/db conn))
(d/q (get-by-emeil-q-p "test2@data.com") (d/db conn))

