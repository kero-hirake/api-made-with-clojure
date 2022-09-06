(ns cljapi.router
  (:require
   [camel-snake-kebab.core :as csk]
   [cljapi.handler :as h]
   [clojure.core.memoize :as memo]
   [muuntaja.core :as muu]
   [muuntaja.middleware :as muu.middleware]
   [reitit.coercion.schema]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as rrc]
   [ring.middleware.defaults :as m.defaults]
   [schema.core :as s]))

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
      (assoc-in [:formats "application/json" :encoder-opts]
                {:encode-key-fn memoized->camelCaseString})
      ;JSON以外のacceptでリクエストされたときに返らないように制限する
      (update :formats #(select-keys % ["application/json"]))
      muu/create))

(def router 
  (ring/router
   [["/health" {:name ::health
                :handler h/handler}]
    ["/api" {:middleware [[m.defaults/wrap-defaults ring-defaults-config]
                          [muu.middleware/wrap-format muuntaja-config]
                          muu.middleware/wrap-params
                          rrc/coerce-exceptions-middleware
                          rrc/coerce-request-middleware]
             ; /api以下では型定義に基づいて変換するようにするための設定
             :coercion reitit.coercion.schema/coercion}
     ["/hello" {:name ::hello
                :handler h/handler}]
     ["/goodbye" {:name ::goodbye
                  :handler h/handler}]
     ["/account" {:name ::account
                  :get {:handler h/handler}
                  :post {:handler h/handler}}]
     ["/account/:id" {:name ::account-by-id
                      :parameters {:path {:id s/Int}}
                      :put {:handler h/handler}
                      :delete {:handler h/handler}}]]]))
