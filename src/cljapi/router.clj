(ns cljapi.router
  (:require
   [cljapi.handler :as h]
   [reitit.ring :as ring]))

(def router
  (ring/router
   [["/health" {:name ::health 
                :handler h/handler}]
    ["/api"
     ["/hello" {:name ::hello
                :handler h/handler}]
     ["/goodbye" {:name ::goodbye
                  :handler h/handler}]]]))
