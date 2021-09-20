(ns typesense.core-test
  (:require [typesense.core :as sut]
            [clojure.test :as test :refer [deftest is are use-fixtures]]))

(defn setup-test-collection [f]
  (sut/create-collection {:name "test_collection"
                          :fields [{:name "test_name"
                                    :type "string"}
                                   {:name "test_count"
                                    :type "int32"}]
                          :default_sorting_field "test_count"})
  (f)
  (doseq [x (sut/list-collections)]
    (sut/drop-collection (:name x))))

(use-fixtures :each setup-test-collection)

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

(deftest drop-collection
  (let [response (sut/drop-collection "test_collection")
        expected {:name "test_collection"
                  :num_documents 0
                  :fields [{:name "test_name"
                            :type "string"
                            :facet false
                            :optional false
                            :index true}
                           {:name "test_count"
                            :type "int32"
                            :facet false
                            :optional false
                            :index true}]
                  :default_sorting_field "test_count"}]
    (are [x y] (= x y)
      (:name expected) (:name response)
      (:fields expected) (:fields response)
      (:num_documents expected) (:num_documents response)
      (:default_sorting_field expected) (:default_sorting_field response))))

(deftest list-collections
  (let [response (sut/list-collections)
        expected {:name "test_collection"
                  :num_documents 0
                  :fields [{:name "test_name"
                            :type "string"
                            :index true
                            :optional false
                            :facet false}
                           {:name "test_count"
                            :type "int32"
                            :index true
                            :optional false
                            :facet false}]
                  :default_sorting_field "test_count"}]
    (are [x y] (= x y)
      1 (count response)
      (:name expected) (-> response first :name)
      (:fields expected) (-> response first :fields)
      (:default_sorting_field expected) (-> response first :default_sorting_field)
      (:num_documents expected) (-> response first :num_documents))))

(deftest retrieve-collection
  (let [response (sut/retrieve-collection "test_collection")
        expected {:name "test_collection"
                  :num_documents 0
                  :fields [{:name "test_name"
                            :type "string"
                            :index true
                            :optional false
                            :facet false}
                           {:name "test_count"
                            :type "int32"
                            :index true
                            :optional false
                            :facet false}]
                  :default_sorting_field "test_count"}]
    (are [x y] (= x y)
      (:name expected) (:name response)
      (:fields expected) (:fields response)
      (:default_sorting_field expected) (:default_sorting_field response)
      (:num_documents expected) (:num_documents response))))

(deftest create-document
  (let [document {:test_name "test1234"
                  :test_count 10
                  :id "0"}
        response (sut/create-document "test_collection" document)]
    (is (= response document))))

(deftest upsert-document
  (let [document {:test_name "test1234"
                  :test_count 10
                  :id "0"}
        response (sut/upsert-document "test_collection" document)]
    (is (= response document))))
