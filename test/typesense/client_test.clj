(ns typesense.client-test
  (:require [typesense.client :as sut]
            [clojure.test :as t :refer [deftest are is testing]]))

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

(deftest collection-workflow-test
  (testing "Create collection"
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
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (dissoc response :created_at)))))

  (testing "Testing list collections"
    (let [expected [{:default_sorting_field  "num_employees"
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
                     :num_documents 0
                     :symbols_to_index []
                     :token_separators []}]
          response (sut/list-collections settings)]
      response
      (is (true? (every? #(> (:created_at %) 0) response)))
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (map #(dissoc % :created_at) response))))))
