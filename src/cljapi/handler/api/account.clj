(ns cljapi.handler.api.account
  (:require
   [cljapi.handler :as h]
   [cljapi.router :as r]
   [cljapi.validation.account :as v.account]
   [ring.util.http-response :as res]))

(defmethod h/handler [::r/account :get]
  [req]
  (let [[error values] (v.account/validate-get-account req)]
    (if error
      (res/bad-request error)
      (res/ok {:validated-values values}))))

(defmethod h/handler [::r/account :post]
  [req]
  (let [[error values] (v.account/validate-post-account req)]
    (if error
      (res/bad-request error)
      (res/ok {:validated-values values}))))

(defmethod h/handler [::r/account-by-id :put]
  [req]
  (res/ok {:method :put
           :path-params (:path-params req)}))

(defmethod h/handler [::r/account-by-id :delete]
  [req]
  (res/ok {:method :delete
           :path-params (:path-params req)}))
