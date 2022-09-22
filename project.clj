(defproject dev.gethop/session.re-frame.cognito "0.1.0-alpha"
  :description "Re-frame library for session management using AWS Cognito"
  :url "https://github.com/gethop-dev/session.re-frame.cognito"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [re-frame "1.2.0"]
                 [cljsjs/amazon-cognito-identity-js "5.2.8-0"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/CLOJARS_USERNAME
                                      :password :env/CLOJARS_PASSWORD
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/CLOJARS_USERNAME
                                      :password :env/CLOJARS_PASSWORD
                                      :sign-releases false}]]
  :aliases {"fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]}
  :profiles {:dev [:project/dev :profiles/dev]
             :repl {:repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}
             :profiles/dev {}
             :project/dev {:dependencies [[com.bhauman/figwheel-main "0.2.17"]
                                          [com.bhauman/rebel-readline-cljs "0.1.4"]
                                          [cider/piggieback "0.5.1"]]
                           :resource-paths ["target"]
                           :clean-targets ^{:protect false} ["target"]
                           :plugins [[lein-cljfmt "0.8.0"]
                                     [day8.re-frame/re-frame-10x "1.2.2"]]}})
