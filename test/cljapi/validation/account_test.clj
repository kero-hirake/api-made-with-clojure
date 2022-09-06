(ns cljapi.validation.account-test 
  (:require
   [cljapi.validation.account :refer [validate-get-account validate-post-account]]
   #_[cljapi.validation.account :as cva]
   [clojure.string :as str]
   [clojure.test :refer [deftest testing is]]))

(deftest validate-get-account-test
  (testing "正常系"
    (is (= [nil 
            {:search ""}]
           (validate-get-account {:params {:search ""}})))
    (is (= [nil 
            {:search "foo"}]
           (validate-get-account {:params {:search "foo"}})))
    (is (= [nil 
            {:search (str/join (repeat 255 "a"))}]
           (validate-get-account {:params {:search (str/join (repeat 255 "a"))}}))))
  (testing "異常系"
    (is (= [{:search "文字列である必要があります"} 
            {}]
           (validate-get-account {:params {:search 123}})))
    (is (= [{:search "255文字以内で入力してください"} 
            {}]
           (validate-get-account {:params {:search (str/join (repeat 256 "a"))}})))))

(deftest validate-post-account-test
  (testing "正常系"
    (is (= [nil 
            {:name "ほげ"
             :email "hoge@example.com"}]
           (validate-post-account {:params {:name "ほげ"
                                            :email "hoge@example.com"}})))
    (is (= [nil 
            {:name (str/join (repeat 255 "a"))
             :email "hoge@example.com"}]
           (validate-post-account {:params {:name (str/join (repeat 255 "a"))
                                            :email "hoge@example.com"}})))
    (is (= [nil
            {:name "ほげ"
             :email "hoge@example.com"}]
           (validate-post-account {:params {:name "ほげ"
                                            :email "hoge@example.com"
                                            :foo "bar"}}))
        "余分なキーは除外される"))
  (testing "異常系"
    (is (= [{:email "必須入力項目です"
             :name "必須入力項目です"}
            {}]
           (validate-post-account {:params {}})))
    (is (= [{:email "文字列である必要があります"
             :name "文字列である必要があります"}
            {}]
           (validate-post-account {:params {:name 1
                                            :email 2}})))
    (is (= [{:email "メールアドレスの形式が正しくありません"
             :name "255文字以内で入力してください"}
            {}]
           (validate-post-account {:params {:name (str/join (repeat 256 "a"))
                                            :email "hoge"}}))) ))

