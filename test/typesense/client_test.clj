(ns typesense.client-test
  (:require [typesense.client :as sut]
            [clojure.test :as t :refer [deftest are is testing]]))

(def settings {:uri "http://localhost:8108"
               :key "key"})

(defn- clean-collections
  "Cleans all collections in Typesense."
  []
  (let [collections (sut/list-collections settings)]
    (doseq [collection collections]
      (sut/delete-collection! settings (:name collection)))))

(defn- clean-typesense-fixture
  "Cleanup before each integration test run."
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
                    :name "companies_collection_test"
                    :num_documents  0
                    :symbols_to_index []
                    :token_separators []}
          schema {:name "companies_collection_test"
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

  (testing "List collections"
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
                     :name "companies_collection_test"
                     :num_documents 0
                     :symbols_to_index []
                     :token_separators []}]
          response (sut/list-collections settings)]
      (is (true? (every? #(> (:created_at %) 0) response)))
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (map #(dissoc % :created_at) response)))))

  (testing "Retrieve collection"
    (let [expected {:default_sorting_field "num_employees",
                    :fields
                    [{:facet false,
                      :index true,
                      :name "company_name",
                      :optional false,
                      :type "string"}
                     {:facet false,
                      :index true,
                      :name "num_employees",
                      :optional false,
                      :type "int32"}
                     {:facet true,
                      :index true,
                      :name "country",
                      :optional false,
                      :type "string"}],
                    :name "companies_collection_test",
                    :num_documents 0,
                    :symbols_to_index [],
                    :token_separators []}
          response (sut/retrieve-collection settings "companies_collection_test")]
      (is (> (:created_at response) 0))
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (dissoc response :created_at)))))

  (testing "Delete collection"
    (let [expected {:default_sorting_field "num_employees",
                    :fields
                    [{:facet false,
                      :index true,
                      :name "company_name",
                      :optional false,
                      :type "string"}
                     {:facet false,
                      :index true,
                      :name "num_employees",
                      :optional false,
                      :type "int32"}
                     {:facet true,
                      :index true,
                      :name "country",
                      :optional false,
                      :type "string"}],
                    :name "companies_collection_test",
                    :num_documents 0,
                    :symbols_to_index [],
                    :token_separators []}
          response (sut/delete-collection! settings "companies_collection_test")]
      (is (> (:created_at response) 0))
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (dissoc response :created_at))))))

(deftest document-workflow-test
  ;; Initialize test collection for documents
  (let [schema {:name "companies_document_test"
                :fields [{:name "company_name"
                          :type "string"}
                         {:name "num_employees"
                          :type "int32"}
                         {:name "country"
                          :type "string"
                          :facet true}]
                :default_sorting_field "num_employees"}]
    (sut/create-collection! settings schema))

  (let [collection-name "companies_document_test"]
    (testing "Create document"
      (let [expected {:company_name "Stark Industries",
                      :country "USA",
                      :id "0",
                      :num_employees 5215}
            document {:company_name "Stark Industries"
                      :num_employees 5215
                      :country "USA"}
            response (sut/create-document! settings collection-name document)]
        (is (= expected response))))

    (testing "Upsert document"
      (let [expected {:company_name "Awesome Inc."
                      :num_employees 10
                      :country "Norway"
                      :id "1"}
            document {:company_name "Awesome Inc."
                      :num_employees 10
                      :country "Norway"}
            response (sut/upsert-document! settings collection-name document)]
        (is (= expected response))))

    (testing "Retrieve document"
      (let [expected {:company_name "Awesome Inc."
                      :num_employees 10
                      :country "Norway"
                      :id "1"}
            response (sut/retrieve-document settings collection-name 1)]
        (is (= expected response))))

    (testing "Update document"
      (let [expected {:company_name "Mega Awesome Inc."
                      :num_employees 10
                      :country "Norway"
                      :id "1"}
            update-doc {:company_name "Mega Awesome Inc."}
            response (sut/update-document! settings collection-name 1 update-doc)]
        (is (= expected response))))))
