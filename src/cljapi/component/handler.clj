(ns cljapi.component.handler
  (:require
   [cljapi.handler.api.greeting]
   [cljapi.router :as router]
   [com.stuartsierra.component :as component]
   [reitit.ring :as ring]
   [ring.middleware.lint :as m.lint]
   [ring.middleware.reload :as m.reload]
   [ring.middleware.stacktrace :as m.stacktrace]))

(defn- build-handler
  [profile]
  (ring/ring-handler
   router/router
   nil
   {:middleware (if (= profile :prod)
                  []
                  [;;指定したディレクトリ以下の変更を検知してリロードさせるMiddleware
                   [m.reload/wrap-reload {:dirs ["src"]
                                          :reload-compile-errors? true}]
                   ;;クエストマップとレスポンスマップがRingの仕様を満たしているかをチェックするMiddleware
                   m.lint/wrap-lint
                   ;;例外をわかりやすく表示してくれるMiddleware
                   [m.stacktrace/wrap-stacktrace {:color? true}]])}))

(defrecord Handler [handler profile]
  component/Lifecycle
  (start [this]
    (assoc this :handler (build-handler profile)))
  (stop [this]
    (assoc this :handler nil)))
