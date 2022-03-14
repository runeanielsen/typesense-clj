(ns typesense.client-test
  (:require [typesense.client :as sut]
            [clojure.test :as t :refer [deftest are is]]))

(def settings {:uri "http://localhost:8108"
               :key "key"})

(defn clean-collections
  "Cleans all collections in Typesense."
  []
  (let [collections (sut/list-collections settings)]
    (doseq [collection collections]
      (sut/delete-collection! settings (:name collection)))))

(defn clean-typesense-fixture
  [f]
  (clean-collections)
  (f))

(t/use-fixtures :once clean-typesense-fixture)

(deftest create-collection
  (let [expected {:default_sorting_field "num_employees"
                  :fields [{:facet false
                            :index true
                            :name "company_name"
                            :optional false
                            :type "string"}
                           {:facet false
                            :index true
                            :name "num_employees"
                            :optional false
                            :type "int32"}
                           {:facet true
                            :index true
                            :name "country"
                            :optional false
                            :type "string"}]
                  :name "companies"
                  :num_documents  0
                  :symbols_to_index []
                  :token_separators []}
        schema {:name "companies"
                :fields [{:name "company_name"
                          :type "string"}
                         {:name "num_employees"
                          :type "int32"}
                         {:name "country"
                          :type "string"
                          :facet true}]
                :default_sorting_field "num_employees"}
        response (sut/create-collection! settings schema)]
    (is (> (:created_at response) 0))
    (are [expected response] (= expected response)
      (:fields expected) (:fields response)
      (:name expected) (:name response)
      (:num_documents expected) (:num_documents response)
      (:symbols_to_index expected) (:symbols_to_index response)
      (:token_separators expected) (:token_separators response))))
