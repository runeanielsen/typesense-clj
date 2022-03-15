(ns typesense.client-test
  (:require [typesense.client :as sut]
            [clojure.test :as t :refer [deftest is testing]]))

(def settings {:uri "http://localhost:8108"
               :key "key"})

(defn- clean-collections
  "Cleans all collections in Typesense."
  []
  (let [collections (sut/list-collections settings)]
    (doseq [collection collections]
      (sut/delete-collection! settings (:name collection)))))

(defn- clean-api-keys
  []
  (let [keys (sut/list-api-keys settings)]
    (doseq [key (:keys keys)]
      (sut/delete-api-key! settings (:id key)))))

(defn- clean-typesense-fixture
  "Cleanup before each integration test run."
  [f]
  (clean-collections)
  (clean-api-keys)
  (f))

(t/use-fixtures :once clean-typesense-fixture)

;; Handles the primary workflow for interactiong with the Typesense Client.
;; The flow is inside of a single deftest because the interaction with the API
;; is stateful so it simplifies the test cases to keep them together.
(deftest client-primary-workflow-tests
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
    (let [expected [{:default_sorting_field "num_employees"
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
    (let [expected {:default_sorting_field "num_employees"
                    :fields
                    [{:facet false
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
                    :token_separators []}
          response (sut/retrieve-collection settings "companies_collection_test")]
      (is (> (:created_at response) 0))
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (dissoc response :created_at)))))

  (testing "Delete collection"
    (let [expected {:default_sorting_field "num_employees"
                    :fields
                    [{:facet false
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
                    :token_separators []}
          response (sut/delete-collection! settings "companies_collection_test")]
      (is (> (:created_at response) 0))
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (dissoc response :created_at)))))

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

  (testing "Create document"
    (let [expected {:company_name "Stark Industries"
                    :country "USA"
                    :id "0"
                    :num_employees 5215}
          document {:company_name "Stark Industries"
                    :num_employees 5215
                    :country "USA"}
          response (sut/create-document! settings "companies_document_test" document)]
      (is (= expected response))))

  (testing "Upsert document"
    (let [expected {:company_name "Awesome Inc."
                    :num_employees 10
                    :country "Norway"
                    :id "1"}
          document {:company_name "Awesome Inc."
                    :num_employees 10
                    :country "Norway"}
          response (sut/upsert-document! settings "companies_document_test" document)]
      (is (= expected response))))

  (testing "Retrieve document"
    (let [expected {:company_name "Awesome Inc."
                    :num_employees 10
                    :country "Norway"
                    :id "1"}
          response (sut/retrieve-document settings "companies_document_test" 1)]
      (is (= expected response))))

  (testing "Update document"
    (let [expected {:company_name "Mega Awesome Inc."
                    :num_employees 10
                    :country "Norway"
                    :id "1"}
          update-doc {:company_name "Mega Awesome Inc."}
          response (sut/update-document! settings "companies_document_test" 1 update-doc)]
      (is (= expected response))))

  (testing "Delete document"
    (let [expected {:company_name "Stark Industries"
                    :country "USA"
                    :id "0"
                    :num_employees 5215}
          response (sut/delete-document! settings "companies_document_test" 0)]
      (is (= expected response)))))

(deftest client-api-key-tests
  (testing "Create api key"
    (let [exp {:actions ["document:search"]
               :collections ["companies"]
               :description "Search only companies key."
               :expires_at 64723363199
               :id 0
               :value "sK0jo6CSn1EBoJJ8LKPjRZCtsJ1JCFkt"}
          key {:description "Search only companies key."
               :actions ["document:search"]
               :collections ["companies"]}
          res (sut/create-api-key! settings key)]
      ;; We test individual cases since they will change each run.
      (is (> (count (:value res)) 0))
      (is (> (:expires_at res) 0))
      (is (>= (:id res) 0))
      ;; We remove :id, :value and :expires_at since they change each run.
      (let [exp (dissoc exp :id :value :expires_at)
            res (dissoc res :id :value :expires_at)]
        (is (= exp res)))))

  (testing "Retrieve api key"
    ;; We have to retrieve the first key-id this way because they change each run.
    (let [id (-> (sut/list-api-keys settings) :keys first :id)
          exp {:actions ["document:search"]
               :collections ["companies"]
               :description "Search only companies key."
               :expires_at 64723363199
               :id 49
               :value_prefix "Bx3y"}
          res (sut/retrieve-api-key settings id)]
      ;; We make individual tests for the following parameters since they change each run.
      (is (>= (:id res) 0))
      (is (> (:expires_at res) 0))
      (is (> (count (:value_prefix res)) 0))
      ;; We remove :id :value_prefex and :expires_at since they change each run.
      (is (= (dissoc exp :expires_at :id :value_prefix)
             (dissoc res :expires_at :id :value_prefix)))))

  (testing "List api keys"
    (let [exp {:keys [{:actions ["document:search"]
                       :collections ["companies"]
                       :description "Search only companies key."
                       :expires_at 64723363199
                       :id 17
                       :value_prefix "vLbB"}]}
          res (sut/list-api-keys settings)]
      ;; We make individual tests for the following parameters since they change each run.
      (is (every? #(>= (:id %) 0) (:keys res)))
      (is (every? #(>= (:expires_at %) 0) (:keys res)))
      (is (every? #(> (count (:value_prefix %)) 0) (:keys res)))
      ;; We remove :id :value_prefex and :expires_at since they change each run.
      (let [exp (assoc exp :keys (->> exp
                                      :keys
                                      (map #(dissoc % :id :expires_at :value_prefix))))
            res (assoc res :keys (->> res
                                      :keys
                                      (map #(dissoc % :id :expires_at :value_prefix))))]
        (is (= exp res)))))

  (testing "Delete api key"
    (let [id (-> (sut/list-api-keys settings) :keys first :id)
          exp {:id id}
          res (sut/delete-api-key! settings id)]
      (is (= exp res)))))
