(defproject jobs-dict-api "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [;;[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojure "1.12.0"]
                 [io.pedestal/pedestal.service "0.7.2"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.7.2"]
                 ;; [io.pedestal/pedestal.immutant "0.6.3"]
                 ;; [io.pedestal/pedestal.tomcat "0.6.3"]
                 ;; ---------------------------------
                 ;;[metosin/porsas "0.0.1-alpha13"]
                 ;; ---------------------------------
                 ;;[alaisi/postgres.async "0.8.0"]
                 [com.h2database/h2 "2.3.232"]
                 [io.staticweb/rate-limit "1.1.0"]
                 [likid_geimfari/secrets "2.1.1"]
                 [cheshire "5.13.0"]
                 [org.clojure/core.async "1.6.681"]
                 [com.griffinscribe/clojure-aes "0.1.4"]
                 [org.xerial/sqlite-jdbc "3.47.1.0"]
                 [mount "0.1.20"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [cljc.java-time "0.1.18"]
                 [org.clojure/data.codec "0.1.1"]
                 [buddy/buddy-core "1.11.423"]
                 [org.clojars.amjil/buddy-auth "2.1.0"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [ch.qos.logback/logback-classic "1.4.6"]
                 [keycloak-clojure "1.31.2"]
                 ;;[ch.qos.logback/logback-classic "1.2.10" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.35"]
                 [org.slf4j/jcl-over-slf4j "1.7.35"]
                 [org.slf4j/log4j-over-slf4j "1.7.35"]
                 [com.github.seancorfield/next.jdbc "1.3.967"]
                 [org.postgresql/postgresql "42.7.4"]
                 [org.clojure/data.json "2.5.1"]
                 [pedestal-service/lein-template "0.6.4"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.10"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "jobs-dict-api.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.6.3"]]}
             :uberjar {:aot [jobs-dict-api.server]}}
  :main ^{:skip-aot true} jobs-dict-api.server)
