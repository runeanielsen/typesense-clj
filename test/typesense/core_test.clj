(ns typesense.core-test
  (:require [typesense.core :as sut]
            [clojure.test :as test :refer [deftest is are use-fixtures]]))

(def ^:private test-settings
  {:uri "http://localhost:8108" :key "key"})

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

(deftest import-documents-create
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

(deftest import-documents-create-batch-size
  (let [expected [{:success true}
                  {:success true}
                  {:success true}]
        documents [{:test_name "test_document_two"
                    :test_count 2}
                   {:test_name "test_document_three"
                    :test_count 3}
                   {:test_name "test_document_four"
                    :test_count 4}]
        response (sut/import-documents test-settings
                                       "test_collection"
                                       documents
                                       {:batch_size 40})]
    (is (= expected response))))

(deftest import-documents-upsert
  (let [expected [{:success true}
                  {:success true}
                  {:success true}]
        documents [{:test_name "upsert_document_one"
                    :test_count 1
                    :id "0"}
                   {:test_name "test_document_two"
                    :test_count 2
                    :id "1"}
                   {:test_name "test_document_three"
                    :test_count 3
                    :id "2"}]
        response (sut/import-documents test-settings
                                       "test_collection"
                                       documents
                                       {:action "upsert"})]
    (is (= expected response))))

(deftest import-documents-update
  (let [expected [{:success true}]
        documents [{:test_name "upsert_document_one"
                    :test_count 1
                    :id "0"}]
        response (sut/import-documents test-settings
                                       "test_collection"
                                       documents
                                       {:action "update"})]
    (is (= expected response))))

(deftest search
  (let [expected [[{:facet_counts [],
                    :found 1
                    :hits [{:document {:id "0", :test_count 1
                                       :test_name "test_document_one"},
                            :highlights
                            [{:field "test_name",
                              :matched_tokens ["test_document_one"],
                              :snippet "<mark>test_document_one</mark>"}],
                            :text_match 33514500}],
                    :request_params {:collection_name "test_collection",
                                     :per_page 10,
                                     :q "test_document_one"}}]]
        response (sut/search test-settings
                             "test_collection"
                             {:q "test_document_one" :query_by "test_name"})]
    (are [x y] (= x y)
      (:found expected) (:found response)
      (:hits expected) (:hits response)
      (:request_params expected) (:request_params response)
      (:facet_counts expected) (:facet_counts response))))
