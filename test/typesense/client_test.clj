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
  "Clean all api-keys."
  []
  (let [keys (sut/list-api-keys settings)]
    (doseq [key (:keys keys)]
      (sut/delete-api-key! settings (:id key)))))

(defn- clean-aliases
  "Cleans all aliases."
  []
  (let [aliases (sut/list-aliases settings)]
    (doseq [key (:aliases aliases)]
      (sut/delete-alias! settings key))))

(defn- clean-typesense-fixture
  "Cleanup before each integration test run."
  [f]
  (clean-collections)
  (clean-api-keys)
  (clean-aliases)
  (f))

(t/use-fixtures :once clean-typesense-fixture)

;; Handles the primary workflow for interactiong with the Typesense Client.
;; The flow is inside of a single deftest because the interaction with the API
;; is stateful so it simplifies the test cases to keep them together.
;; If we had decided to split them out listing collections might result in issues.
;; hmm...
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
      (is (= expected response))))

  ;; Initize test collection for documents
  (let [schema {:name "companies_documents_test"
                :fields [{:name "company_name"
                          :type "string"}
                         {:name "num_employees"
                          :type "int32"}
                         {:name "country"
                          :type "string"
                          :facet true}]
                :default_sorting_field "num_employees"}]
    (sut/create-collection! settings schema))

  (testing "Create documents"
    (let [exp [{:success true} {:success true}]
          res (sut/create-documents! settings
                                     "companies_documents_test"
                                     [{:id "1"
                                       :company_name "Innovationsoft A/S"
                                       :num_employees 10
                                       :country "Finland"}
                                      {:id "2"
                                       :company_name "GoSoftware"
                                       :num_employees 5000
                                       :country "Sweden"}])]
      (is (= exp res))))

  (testing "Upsert documents"
    (let [exp [{:success true} {:success true}]
          res (sut/upsert-documents! settings
                                     "companies_documents_test"
                                     [{:id "1"
                                       :company_name "Innovationsoft A/S"
                                       :num_employees 10
                                       :country "Finland"}
                                      {:id "3"
                                       :company_name "Awesomesoftwaresoft"
                                       :num_employees 105
                                       :country "Denmark"}])]
      (is (= exp res))))

  (testing "Update documents"
    (let [exp [{:success true} {:success true}]
          res (sut/update-documents! settings
                                     "companies_documents_test"
                                     [{:id "1"
                                       :company_name "Innovationsoft A/S"
                                       :num_employees 10
                                       :country "Finland"}
                                      {:id "2"
                                       :company_name "GoSoftware"
                                       :num_employees 5000
                                       :country "Sweden"}])]
      (is (= exp res))))

  (testing "Delete documents"
    (let [exp {:num_deleted 2}
          res (sut/delete-documents! settings
                                     "companies_documents_test"
                                     {:filter_by "num_employees:>=100"})]
      (is (= exp res))))

  (testing "Export documents"
    (let [exp [{:id "1"
                :company_name "Innovationsoft A/S"
                :num_employees 10
                :country "Finland"}]
          res (sut/export-documents settings
                                    "companies_documents_test"
                                    {:filter_by "num_employees:<=100"})]
      (is (= exp res))))

  (testing "Search"
    (let [exp {:facet_counts []
               :found 0
               :hits []
               :out_of 1
               :page 1
               :request_params
               {:collection_name "companies_documents_test"
                :per_page 10
                :q "Stark"}
               :search_cutoff false
               :search_time_ms 0}
          res (sut/search settings
                          "companies_documents_test"
                          {:q "Stark"
                           :query_by "company_name"})]
      (is (= res exp))))

  ;; Creating test setup for multi search
  (let [schema {:name "products_multi_search_test"
                :fields [{:name "name"
                          :type "string"}
                         {:name "price"
                          :type "int32"}]}]
    (sut/create-collection! settings schema)
    (sut/create-document! settings "products_multi_search_test" {:id "1"
                                                                 :name "shoe"
                                                                 :price 75}))

  (let [schema {:name "brands_multi_search_test"
                :fields [{:name "name"
                          :type "string"}]}]
    (sut/create-collection! settings schema)
    (sut/create-document! settings "brands_multi_search_test" {:id "1" :name "Nike"}))

  (testing "Multi search"
    (let [exp {:results
               [{:facet_counts []
                 :found 1
                 :hits
                 [{:document {:id "1" :name "shoe" :price 75}
                   :highlights
                   [{:field "name"
                     :matched_tokens ["shoe"]
                     :snippet "<mark>shoe</mark>"}]
                   :text_match 33514497}]
                 :out_of 1
                 :page 1
                 :request_params
                 {:collection_name "products_multi_search_test" :per_page 10 :q "shoe"}
                 :search_cutoff false
                 :search_time_ms 0}
                {:facet_counts []
                 :found 1
                 :hits
                 [{:document {:id "1" :name "Nike"}
                   :highlights
                   [{:field "name"
                     :matched_tokens ["Nike"]
                     :snippet "<mark>Nike</mark>"}]
                   :text_match 33514497}]
                 :out_of 1
                 :page 1
                 :request_params
                 {:collection_name "brands_multi_search_test" :per_page 10 :q "Nike"}
                 :search_cutoff false
                 :search_time_ms 0}]}
          res (sut/multi-search settings
                                {:searches [{:collection "products_multi_search_test"
                                             :q "shoe"
                                             :filter_by "price:=[50..120]"}
                                            {:collection "brands_multi_search_test"
                                             :q "Nike"}]}
                                {:query_by "name"})]
      (is (= exp res))))

  ;; Initialize test collection for curation.
  (let [schema {:name "companies_curation_test"
                :fields [{:name "company_name"
                          :type "string"}
                         {:name "num_employees"
                          :type "int32"}
                         {:name "country"
                          :type "string"
                          :facet true}]
                :default_sorting_field "num_employees"}]
    (sut/create-collection! settings schema))

  (testing "Upsert override"
    (let [exp {:excludes [{:id "287"}]
               :id "customize_apple"
               :includes [{:id "422" :position 1} {:id "54" :position 2}]
               :rule {:match "exact" :query "apple"}}
          res (sut/upsert-override! settings
                                    "companies_curation_test"
                                    "customize_apple"
                                    {:rule {:query "apple"
                                            :match "exact"}
                                     :includes [{:id "422" :position 1}
                                                {:id "54" :position 2}]
                                     :excludes [{:id "287"}]})]
      (is (= exp res))))

  (testing "List all overrides"
    (let [exp {:overrides [{:excludes [{:id "287"}]
                            :id "customize_apple"
                            :includes [{:id "422" :position 1} {:id "54" :position 2}]
                            :rule {:match "exact" :query "apple"}}]}
          res (sut/list-overrides settings "companies_curation_test")]
      (is (= exp res))))

  (testing "Retrieve override"
    (let [exp {:excludes [{:id "287"}]
               :id "customize_apple"
               :includes [{:id "422" :position 1} {:id "54" :position 2}]
               :rule {:match "exact" :query "apple"}}
          res (sut/retrieve-override settings
                                     "companies_curation_test"
                                     "customize_apple")]
      (is (= exp res))))

  (testing "Delete override"
    (let [exp {:id "customize_apple"}
          res (sut/delete-override! settings
                                    "companies_curation_test"
                                    "customize_apple")]
      (is (= exp res))))

  ;; Initialize test collection for alias.
  (let [schema {:name "companies_alias_test"
                :fields [{:name "company_name"
                          :type "string"}
                         {:name "num_employees"
                          :type "int32"}
                         {:name "country"
                          :type "string"
                          :facet true}]
                :default_sorting_field "num_employees"}]
    (sut/create-collection! settings schema))

  (testing "Upsert alias"
    (let [exp {:collection_name "companies_alias_test" :name "companies"}
          res (sut/upsert-alias! settings
                                 "companies"
                                 {:collection_name "companies_alias_test"})]
      (is (= exp res))))

  (testing "List all aliases"
    (let [exp {:aliases [{:collection_name "companies_alias_test" :name "companies"}]}
          res (sut/list-aliases settings)]
      (is (= exp res))))

  (testing "Retrieve alias"
    (let [exp {:collection_name "companies_alias_test" :name "companies"}
          res (sut/retrieve-alias settings "companies")]
      (is (= exp res))))

  (testing "Delete alias"
    (let [exp {:collection_name "companies_alias_test" :name "companies"}
          res (sut/delete-alias! settings "companies")]
      (is (= exp res))))

  ;; Initialize test collection for synonyms.
  (let [schema {:name "products_synonyms_test"
                :fields [{:name "company_name"
                          :type "string"}
                         {:name "num_employees"
                          :type "int32"}
                         {:name "country"
                          :type "string"
                          :facet true}]
                :default_sorting_field "num_employees"}]
    (sut/create-collection! settings schema))

  (testing "Upsert synonym"
    (let [exp {:id "coat-synonyms" :synonyms ["blazer" "coat" "jacket"]}
          res (sut/upsert-synonym! settings
                                   "products_synonyms_test"
                                   "coat-synonyms"
                                   {:synonyms ["blazer" "coat" "jacket"]})]
      (is (= exp res))))

  (testing "Retrieve synonym"
    (let [exp {:id "coat-synonyms" :root "" :synonyms ["blazer" "coat" "jacket"]}
          res (sut/retrieve-synonym settings "products_synonyms_test" "coat-synonyms")]
      (is (= exp res))))

  (testing "List synonyms"
    (let [exp {:synonyms [{:id "coat-synonyms" :root "" :synonyms ["blazer" "coat" "jacket"]}]}
          res (sut/list-synonyms settings "products_synonyms_test")]
      (is (= exp res))))

  (testing "Delete synonym"
    (let [exp {:id "coat-synonyms"}
          res (sut/delete-synonym! settings "products_synonyms_test" "coat-synonyms")]
      (is (= res exp)))))

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
      ;; We remove :id :value and :expires_at since they change each run.
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
      ;; We make individual tests for the parameters since they change each run.
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
      ;; We make individual tests for the parameters since they change each run.
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
