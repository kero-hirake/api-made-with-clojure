(ns cljapi.handler.api.greeting
  (:require
   [ring.util.http-response :as res]))

(defn hello [_]
  (res/ok "hello world"))

(defn goodbye [_]
  (res/ok "goodbye"))
