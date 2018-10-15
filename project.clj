(defproject sneakycode "0.1.0-SNAPSHOT"
  :description "sneakycode blog"
  :url "http://sneakycode.net"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [stasis "2.3.0"]
                 [ring "1.6.3"]
                 [org.clojure/tools.logging "0.4.0"]
                 [hiccup "1.0.5"]
                 [me.raynes/cegdown "0.1.1"]
                 [cheshire "5.8.0"]
                 [clj-time "0.14.2"]
                 [clj-rss "0.2.3"]
                 [me.raynes/fs "1.4.6"]
                 [clj-http "3.7.0"]
                 [com.novemberain/pantomime "2.9.0"]
                 [clygments "1.0.0"]
                 [enlive "1.1.6"]
                 [org.clojure/clojurescript "1.10.339" :exclusions [[com.google.code.findbugs/jsr305]
                                                                    [com.google.code.gson/gson]]]
                 [zprint "0.4.10"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler sneakycode.core/app
         :init sneakycode.core/app-init
         :auto-refresh? true}
  :aliases {"dev" ["ring" "server-headless" "4321"]
            "build" ["run" "-m" "sneakycode.deploy"]
            })
