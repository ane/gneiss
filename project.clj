(defproject gneiss "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[acyclic/squiggly-clojure "0.1.2-SNAPSHOT"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/clojure "1.6.0"]]
  :plugins [[lein-environ "1.0.0"]]
  :main ^:skip-aot gneiss.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:env {:squiggly {:checkers [:eastwood]
                                    :eastwood-exclude-linters [:unlimited-use]}}}})
