(ns cljapi.handler.health
  (:require
   [ring.util.http-response :as res]))

(defn health [_]
  (res/ok "applicatoin is running"))