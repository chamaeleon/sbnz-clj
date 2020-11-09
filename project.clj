(defproject sbnz "0.1.0-SNAPSHOT"
  :description "SBNZ (Subtract, Branch if Not Zero) VM Interpreter"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [instaparse "1.4.10"]]
  :main ^:skip-aot sbnz.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
