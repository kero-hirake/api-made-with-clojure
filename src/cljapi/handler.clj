(ns cljapi.handler
  (:require
   [ring.util.http-response :as res])) 

(defmulti handler
  (fn [req]
    [(get-in req [:reitit.core/match :data :name])
     (get req :request-method)]))

(defmethod handler :default
  [_]
  (res/not-found "not found"))