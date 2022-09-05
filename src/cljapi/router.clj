(ns cljapi.router
  (:require
   [camel-snake-kebab.core :as csk] 
   [cljapi.handler :as h]
   [clojure.core.memoize :as memo]
   [muuntaja.core :as muu]
   [muuntaja.middleware :as muu.middleware]
   [reitit.ring :as ring]
   [ring.middleware.defaults :as m.defaults]))

(def ^:private ring-defaults-config
  (-> m.defaults/api-defaults
      (assoc :proxy true)))

(def ^:private memoized->camelCaseString
  "kebab-case keywordをjsonにするときcamelCaseにしたい
   バリエーションは少ないのでキャッシュする"
  (memo/lru csk/->camelCaseString {} :lru/threshold 1024))

(def ^:private muuntaja-config
  "https://cljdoc.org/d/metosin/muuntaja/0.6.8/doc/configuration"
  (-> muu/default-options
      ;JSONにencodeする時にキーをcamelCaseにする
      (assoc-in [:format "application/json" :encoder-opts]
                {:encode-key-fn memoized->camelCaseString})
      ;JSON以外のacceptでリクエストされたときに返らないように制限する
      (update :formatts #(select-keys % ["application/json"]))
      muu/create))

(def router 
  (ring/router
   [["/health" {:name ::health
                :handler h/handler}]
    ["/api" {:middleware [[m.defaults/wrap-defaults ring-defaults-config]
                          [muu.middleware/wrap-format muuntaja-config]
                          muu.middleware/wrap-params]}
     ["/hello" {:name ::hello
                :handler h/handler}]
     ["/goodbye" {:name ::goodbye
                  :handler h/handler}]]]))
