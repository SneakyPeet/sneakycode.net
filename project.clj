(defproject sneakycode "0.1.0-SNAPSHOT"
  :description "sneakycode blog"
  :url "http://sneakycode.net"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [stasis "2.3.0"]
                 [ring "1.6.3"]
                 [org.clojure/tools.logging "0.4.0"]
                 [hiccup "1.0.5"]
                 [me.raynes/cegdown "0.1.1"]]
  :plugins [[lein-ring "0.10.0"]]
  :ring {:handler sneakycode.core/app
         :init sneakycode.core/app-init}
  :aliases {"dev" ["ring" "server-headless"]})
