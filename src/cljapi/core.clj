(ns cljapi.core
  (:require 
   [cljapi.systen :as system]))


(defn -main
  [& _args]
  (system/start))

(comment 
  (-main)
  )