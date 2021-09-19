(ns typesense.core-test
  (:require [typesense.core :as sut]
            [clojure.test :refer [deftest are]]))

(deftest create-collection
  (let [collection {:name "companies"
                    :fields [{:name "company_name"
                              :type "string"}
                             {:name "num_employees"
                              :type "int32"}
                             {:name "country"
                              :type "string"
                              :facet true}]
                    :default_sorting_field "num_employees"}
        expected {:name "companies"
                  :num_documents 0
                  :fields [{:name "company_name"
                            :type "string"
                            :facet false
                            :optional false
                            :index true}
                           {:name "num_employees"
                            :type "int32"
                            :facet false
                            :optional false
                            :index true}
                           {:name "country"
                            :type "string"
                            :facet true
                            :index true
                            :optional false}]
                  :default_sorting_field "num_employees"}
        response (sut/create-collection collection)]
    (are [x y] (= x y)
      (:name expected) (:name response)
      (:fields expected) (:fields response)
      (:num_documents expected) (:num_documents response)
      (:default_sorting_field expected) (:default_sorting_field response))))
