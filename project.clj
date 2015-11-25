(defproject gneiss "0.1.0-SNAPSHOT"
  :description "it's gneiss"
  :url "https://github.com/ane/gneiss"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [;; reducers on java 1.6
                 [org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/clojure "1.7.0"]]
  :plugins [[lein-environ "1.0.0"]]
  :main ^:skip-aot gneiss.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:env {:squiggly {:checkers [:eastwood]
                                    :eastwood-exclude-linters [:unlimited-use]}}}
             :cloverage {:plugins [[lein-cloverage "1.0.2"]]}})
