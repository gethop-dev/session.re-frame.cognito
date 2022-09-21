(defproject simple "0.1.0-SNAPSHOT"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [re-frame "1.2.0"]
                 [cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]
                 [reagent "1.1.1"]
                 [cljsjs/amazon-cognito-identity-js "5.2.8-0"]]
  :aliases {"fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]}
  :source-paths ["src" "../../src"]
  :profiles {:dev [:project/dev :profiles/dev]
             :repl {:repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}
             :profiles/dev {}
             :project/dev {:dependencies [[com.bhauman/figwheel-main "0.2.17"]
                                          [com.bhauman/rebel-readline-cljs "0.1.4"]
                                          [cider/piggieback "0.5.1"]
                                          [day8.re-frame/tracing      "0.6.2"]
                                          [day8.re-frame/re-frame-10x "1.2.2"]]
                           :resource-paths ["target"]
                           :clean-targets ^{:protect false} ["target"]
                           :plugins [[jonase/eastwood "1.2.3"]
                                     [lein-cljfmt "0.8.0"]]}
             :eastwood {:linters [:all]
                        :exclude-linters [:keyword-typos
                                          :boxed-math
                                          :non-clojure-file
                                          :unused-namespaces
                                          :performance]
                        :debug [:progress :time]}})
