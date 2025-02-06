(defproject progrock "0.1.2"
  :description "A functional progress bar for the command line"
  :url "https://github.com/weavejester/progrock"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :plugins [[lein-codox "0.10.8"]]
  :codox {:output-path "codox"
          :source-uri "http://github.com/weavejester/progrock/blob/{version}/{filepath}#L{line}"})
