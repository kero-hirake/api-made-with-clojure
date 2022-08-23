(ns cljapi.systen
  (:require
   [cljapi.component.handler :as c.handler]
   [cljapi.component.server :as c.server]
   [com.stuartsierra.component :as component]))

(defn- new-system []
  (component/system-map
   :handler (c.handler/map->Handler {})
   :server (component/using
            (c.server/map->Jetty9Server {:opts {:join? false
                                                :port 8000}})
            [:handler])))

(defn start []
  (let [system (new-system)]
    (component/start system)))

(defn stop [system]
  (component/stop system))

(defonce system (atom nil))

(defn go []
  (when @system
    (stop @system)
    (reset! @system nil))
  (reset! system (start)))