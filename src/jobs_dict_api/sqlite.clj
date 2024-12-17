(ns jobs-dict-api.sqlite
  (:require [clojure.java.jdbc :as jdbc]
            [mount.core :as mount]
            [cljc.java-time local-date-time]
            ))

(def db-spec
  { :classname   "org.sqlite.JDBC"
    :dbtype      "sqlite"
    :dbname      ":memory:"
    :host        :none })

(declare dbx)

(def db-uri "jdbc:sqlite::memory:")

(defn on-start []
  (let [spec {:connection-uri db-uri}
        conx (jdbc/get-connection db-spec)]
    (assoc spec :connection conx)))

(defn on-stop []
  (-> dbx :connection .close)
  nil)

(mount/defstate
  ^{:on-reload :noop}
  dbx
  :start (on-start)
  :stop (on-stop))

(defn sqlite-start []
    (mount/start #'dbx)
    (jdbc/execute! dbx ["
        create table ApiKeys (
        Id integer primary key autoincrement,
        ApiKey varchar(64) not null,
        Expired datetime,
        Created datetime not null default current_timestamp);"])

    (jdbc/execute! dbx ["
         insert into ApiKeys(Id,ApiKey,Expired) VALUES (NULL, '16de814afaf3a815a8b6a9e99410c5c8', DATETIME(current_timestamp, '+30 minutes'));"]))

(defn sqlite-stop []
  (mount/stop #'dbx))

(defn add-apikey
   [apikey]
   (let [insert_command (format "insert into ApiKeys(Id,ApiKey,Expired) VALUES (NULL, '%s', DATETIME(current_timestamp, '+15 minutes'));" apikey)]
        (jdbc/execute! dbx [insert_command])))

(defn del-apikey
   [apikey]
   (let [del_cmd (format "delete from ApiKeys where ApiKey='%s'" apikey)]
        (jdbc/execute! dbx [del_cmd])))

(defn- is-akey-valid
   [apikey]
   (let [ sel_cmd (format "select count(*) from ApiKeys where ApiKey='%s' and (Expired is null or Expired >= DATETIME('now'))" apikey)
          result (jdbc/query dbx [sel_cmd])
          res_count (first (vals (first result))) ]
          ;;(prn sel_cmd)
          ;;(prn result)
          ;;(prn res_count)
        (if (= res_count 1) true false)))

(defn is-apikey-valid
   [apikey]
   (cond
    (> (.length apikey) 0) (is-akey-valid apikey)
    (= (.length apikey) 0) false
    :else false))        

        




