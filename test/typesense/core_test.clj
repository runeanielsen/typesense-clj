(ns typesense.core-test
  (:require [typesense.core :as sut]
            [clojure.test :as test :refer [deftest is are use-fixtures]]))

(def ^:private test-settings  (sut/settings "http://localhost:8108" "key"))

(defn setup-test-collection [f]
  (sut/create-collection test-settings
                         {:name "test_collection"
                          :fields [{:name "test_name"
                                    :type "string"}
                                   {:name "test_count"
                                    :type "int32"}]
                          :default_sorting_field "test_count"})
  (sut/create-document test-settings
                       "test_collection"
                       {:test_name "test_document_one"
                        :test_count 1
                        :id "0"})
  (f)
  (doseq [x (sut/list-collections test-settings)]
    (sut/drop-collection test-settings (:name x))))

(use-fixtures :each setup-test-collection)

(deftest settings
  (let [expected {:api-uri "http://localhost:8108"
                  :api-key "key"}
        conn (sut/settings "http://localhost:8108" "key")]
    (is (= expected conn))))

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
        response (sut/create-collection test-settings collection)]
    (are [x y] (= x y)
      (:name expected) (:name response)
      (:fields expected) (:fields response)
      (:num_documents expected) (:num_documents response)
      (:default_sorting_field expected) (:default_sorting_field response))))

(deftest drop-collection
  (let [response (sut/drop-collection test-settings "test_collection")
        expected {:name "test_collection"
                  :num_documents 1
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
  (let [response (sut/list-collections test-settings)
        expected {:name "test_collection"
                  :num_documents 1
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
  (let [response (sut/retrieve-collection test-settings "test_collection")
        expected {:name "test_collection"
                  :num_documents 1
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
  (let [document {:test_name "test_document_two"
                  :test_count 2
                  :id "1"}
        response (sut/create-document test-settings "test_collection" document)]
    (is (= response document))))

(deftest upsert-document
  (let [document {:test_name "test_name"
                  :test_count 10
                  :id "0"}
        response (sut/upsert-document test-settings "test_collection" document)]
    (is (= response document))))

(deftest retrieve-document
  (let [expected {:test_name "test_document_one"
                  :test_count 1
                  :id "0"}
        response (sut/retrieve-document test-settings "test_collection" 0)]
    (is (= expected response))))

(deftest delete-document
  (let [expected {:test_name "test_document_one"
                  :test_count 1
                  :id "0"}
        response (sut/delete-document test-settings "test_collection" 0)]
    (is (= expected response))))

(deftest import-documents
  (let [expected [{:success true}
                  {:success true}
                  {:success true}]
        documents [{:test_name "test_document_two"
                    :test_count 2}
                   {:test_name "test_document_three"
                    :test_count 3}
                   {:test_name "test_document_four"
                    :test_count 4}]
        response (sut/import-documents test-settings "test_collection" documents)]
    (is (= expected response))))
