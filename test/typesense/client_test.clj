(ns ^:integration typesense.client-test
  (:require [typesense.client :as sut]
            [clojure.set :as set]
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
(deftest client-primary-workflow-tests
  (testing "Create collection"
    (let [expected {:default_sorting_field "num_employees"
                    :enable_nested_fields false
                    :fields
                    [{:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "company_name"
                      :facet false
                      :type "string"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort false}
                     {:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "num_employees"
                      :facet false
                      :type "int32"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort true}
                     {:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "country"
                      :facet true
                      :type "string"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort false}]
                    :name "companies_collection_test"
                    :num_documents 0
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
      ;; We make individual test for :created_at since it changes each run.
      (is (> (:created_at response) 0))
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (dissoc response :created_at)))))

  (testing "Update collection"
    (let [expected {:fields
                    [{:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "year_founded"
                      :nested false
                      :nested_array 0
                      :facet false
                      :range_index false
                      :type "int32"
                      :symbols_to_index []
                      :async_reference false
                      :num_dim 0
                      :token_separators []
                      :reference ""
                      :optional true
                      :stem false
                      :stem_dictionary ""
                      :vec_dist "cosine"
                      :embed nil
                      :hnsw_params {:M 16 :ef_construction 200}
                      :sort true}]}
          collection-name "companies_collection_test"
          update-schema {:fields [{:name "year_founded"
                                   :type "int32"
                                   :optional true}]}
          response (sut/update-collection! settings collection-name update-schema)]
      (is (= expected response))))

  (testing "List collections"
    (let [expected [{:default_sorting_field "num_employees"
                     :enable_nested_fields false
                     :fields
                     [{:infix false
                       :index true
                       :store true
                       :locale ""
                       :name "company_name"
                       :facet false
                       :type "string"
                       :optional false
                       :stem false
                       :stem_dictionary ""
                       :sort false}
                      {:infix false
                       :index true
                       :store true
                       :locale ""
                       :name "num_employees"
                       :facet false
                       :type "int32"
                       :optional false
                       :stem false
                       :stem_dictionary ""
                       :sort true}
                      {:infix false
                       :index true
                       :store true
                       :locale ""
                       :name "country"
                       :facet true
                       :type "string"
                       :optional false
                       :stem false
                       :stem_dictionary ""
                       :sort false}
                      {:infix false
                       :index true
                       :store true
                       :locale ""
                       :name "year_founded"
                       :facet false
                       :type "int32"
                       :optional true
                       :stem false
                       :stem_dictionary ""
                       :sort true}]
                     :name "companies_collection_test"
                     :num_documents 0
                     :symbols_to_index []
                     :token_separators []}]
          response (sut/list-collections settings)]
      ;; We make individual test for :created_at since it changes each run.
      (is (true? (every? #(> (:created_at %) 0) response)))
      ;; We remove :created_at it cannot be asserted since it changes each run.
      (is (= expected (map #(dissoc % :created_at) response)))))

  (testing "Retrieve collection"
    (let [expected {:default_sorting_field "num_employees"
                    :enable_nested_fields false
                    :fields
                    [{:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "company_name"
                      :facet false
                      :type "string"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort false}
                     {:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "num_employees"
                      :facet false
                      :type "int32"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort true}
                     {:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "country"
                      :facet true
                      :type "string"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort false}
                     {:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "year_founded"
                      :facet false
                      :type "int32"
                      :optional true
                      :stem false
                      :stem_dictionary ""
                      :sort true}]
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
                    :enable_nested_fields false
                    :fields
                    [{:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "company_name"
                      :facet false
                      :type "string"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort false}
                     {:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "num_employees"
                      :facet false
                      :type "int32"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort true}
                     {:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "country"
                      :facet true
                      :type "string"
                      :optional false
                      :stem false
                      :stem_dictionary ""
                      :sort false}
                     {:infix false
                      :index true
                      :store true
                      :locale ""
                      :name "year_founded"
                      :facet false
                      :type "int32"
                      :optional true
                      :stem false
                      :stem_dictionary ""
                      :sort true}]
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
    (let [expected {:company_name "Hotel Sanders København A/S"
                    :country "DK"
                    :id "0"
                    :num_employees 5215}
          document {:company_name "Hotel Sanders København A/S"
                    :num_employees 5215
                    :country "DK"}
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
    (let [expected {:company_name "Hotel Sanders København A/S"
                    :country "DK"
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
               :found 1
               :hits
               [{:document
                 {:company_name "Innovationsoft A/S"
                  :country "Finland"
                  :id "1"
                  :num_employees 10}
                 :highlight
                 {:company_name
                  {:matched_tokens ["Innovation"]
                   :snippet "<mark>Innovation</mark>soft A/S"}}
                 :highlights
                 [{:field "company_name"
                   :matched_tokens ["Innovation"]
                   :snippet "<mark>Innovation</mark>soft A/S"}]
                 :text_match 578730089005449337
                 :text_match_info
                 {:best_field_score "1108074561536"
                  :num_tokens_dropped 0
                  :typo_prefix_score 1
                  :best_field_weight 15
                  :fields_matched 1
                  :score "578730089005449337"
                  :tokens_matched 1}}]
               :out_of 1
               :page 1
               :request_params
               {:collection_name "companies_documents_test"
                :per_page 10
                :first_q "Innovation"
                :q "Innovation"}
               :search_cutoff false
               :search_time_ms 0}
          res (sut/search settings
                          "companies_documents_test"
                          {:q "Innovation"
                           :query_by "company_name"})]
      (is (= exp res))))

  ;; Creating test setup for multi search
  (let [schema {:name "products_multi_search_test"
                :fields [{:name "name"
                          :type "string"}
                         {:name "price"
                          :type "int32"}]}]
    (sut/create-collection! settings schema)
    (sut/create-document! settings
                          "products_multi_search_test"
                          {:id "1"
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
                   :highlight
                   {:name {:matched_tokens ["shoe"] :snippet "<mark>shoe</mark>"}}
                   :highlights
                   [{:field "name"
                     :matched_tokens ["shoe"]
                     :snippet "<mark>shoe</mark>"}]
                   :text_match 578730123365711993
                   :text_match_info
                   {:best_field_score "1108091339008"
                    :num_tokens_dropped 0
                    :typo_prefix_score 0
                    :best_field_weight 15
                    :fields_matched 1
                    :score "578730123365711993"
                    :tokens_matched 1}}]
                 :out_of 1
                 :page 1
                 :request_params
                 {:collection_name "products_multi_search_test"
                  :first_q "shoe"
                  :per_page 10
                  :q "shoe"}
                 :search_cutoff false
                 :search_time_ms 0}
                {:facet_counts []
                 :found 1
                 :hits
                 [{:document {:id "1" :name "Nike"}
                   :highlight
                   {:name {:matched_tokens ["Nike"] :snippet "<mark>Nike</mark>"}}
                   :highlights
                   [{:field "name"
                     :matched_tokens ["Nike"]
                     :snippet "<mark>Nike</mark>"}]
                   :text_match 578730123365711993
                   :text_match_info
                   {:best_field_score "1108091339008"
                    :num_tokens_dropped 0
                    :typo_prefix_score 0
                    :best_field_weight 15
                    :fields_matched 1
                    :score "578730123365711993"
                    :tokens_matched 1}}]
                 :out_of 1
                 :page 1
                 :request_params
                 {:collection_name "brands_multi_search_test"
                  :first_q "Nike"
                  :per_page 10
                  :q "Nike"}
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

  ;; Initialize test collection with documents for geosearch.
  (let [schema {:name "places"
                :fields [{:name "title" :type "string"}
                         {:name "points" :type "int32"}
                         {:name "location" :type "geopoint"}]
                :default_sorting_field "points"}
        document {:points 1
                  :title "Louvre Museuem"
                  :location [48.86093481609114 2.33698396872901]}]
    (sut/create-collection! settings schema)
    (sut/create-document! settings "places" document))

  (testing "Geosearch"
    (let [exp {:facet_counts []
               :found 1
               :hits
               [{:document
                 {:id "0"
                  :location [48.86093481609114 2.33698396872901]
                  :points 1
                  :title "Louvre Museuem"}
                 :geo_distance_meters {:location 1020}
                 :highlight {}
                 :highlights []}]
               :out_of 1
               :page 1
               :request_params {:collection_name "places"
                                :per_page 10
                                :first_q "*"
                                :q "*"}
               :search_cutoff false}
          res (sut/search settings
                          "places"
                          {:q "*"
                           :query_by "title"
                           :filter_by "location:(48.90615915923891, 2.3435897727061175, 5.1 km)"
                           :sort_by "location(48.853, 2.344):asc"})]
      ;; We test :search_time_ms individually since it can chane each run.
      (is (number? (res :search_time_ms)))
      (is (= (dissoc exp :search_time_ms)
             (dissoc res :search_time_ms)))))

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
    (let [exp {:overrides
               [{:excludes [{:id "287"}]
                 :filter_curated_hits false
                 :id "customize_apple"
                 :includes [{:id "422" :position 1} {:id "54" :position 2}]
                 :remove_matched_tokens false
                 :rule {:match "exact" :query "apple"}
                 :stop_processing true}]}
          res (sut/list-overrides settings "companies_curation_test")]
      (is (= exp res))))

  (testing "Retrieve override"
    (let [exp {:excludes [{:id "287"}]
               :filter_curated_hits false
               :id "customize_apple"
               :includes [{:id "422" :position 1} {:id "54" :position 2}]
               :remove_matched_tokens false
               :rule {:match "exact" :query "apple"}
               :stop_processing true}
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
    (let [exp {:id "coat-synonyms"
               :root ""
               :synonyms ["blazer" "coat" "jacket"]}
          res (sut/retrieve-synonym settings "products_synonyms_test" "coat-synonyms")]
      (is (= exp res))))

  (testing "List synonyms"
    (let [exp {:synonyms [{:id "coat-synonyms"
                           :root ""
                           :synonyms ["blazer" "coat" "jacket"]}]}
          res (sut/list-synonyms settings "products_synonyms_test")]
      (is (= exp res))))

  (testing "Delete synonym"
    (let [exp {:id "coat-synonyms"}
          res (sut/delete-synonym! settings "products_synonyms_test" "coat-synonyms")]
      (is (= res exp)))))

(defn api-key-constant-values
  "Helper function to get unique values of api-key."
  [k]
  (dissoc k :id :value :expires_at :value_prefix))

(deftest client-api-key-tests
  (testing "Create api key"
    (let [exp {:actions ["document:search"]
               :collections ["companies"]
               :description "Search only companies key."
               :expires_at 64723363199
               :id 0
               :autodelete false
               :value "sK0jo6CSn1EBoJJ8LKPjRZCtsJ1JCFkt"}
          key {:description "Search only companies key."
               :actions ["document:search"]
               :collections ["companies"]}
          res (sut/create-api-key! settings key)]
      ;; We test individual cases since they will change each run.
      (is (> (count (:value res)) 0))
      (is (> (:expires_at res) 0))
      (is (>= (:id res) 0))
      ;; We test the values that do not change each run.
      (is (= (api-key-constant-values exp) (api-key-constant-values res)))))

  (testing "Retrieve api key"
    ;; We have to retrieve the first key-id this way because they change each run.
    (let [id (-> (sut/list-api-keys settings) :keys first :id)
          exp {:actions ["document:search"]
               :collections ["companies"]
               :description "Search only companies key."
               :expires_at 64723363199
               :autodelete false
               :id 49
               :value_prefix "Bx3y"}
          res (sut/retrieve-api-key settings id)]
      ;; We make individual tests for the parameters since they change each run.
      (is (>= (:id res) 0))
      (is (> (:expires_at res) 0))
      (is (> (count (:value_prefix res)) 0))
      ;; We test the values that do not change each run.
      (is (= (api-key-constant-values exp) (api-key-constant-values res)))))

  (testing "List api keys"
    (let [exp {:keys [{:actions ["document:search"]
                       :collections ["companies"]
                       :description "Search only companies key."
                       :autodelete false
                       :expires_at 64723363199
                       :id 17
                       :value_prefix "vLbB"}]}
          res (sut/list-api-keys settings)]
      ;; We make individual tests for the parameters since they change each run.
      (is (every? #(>= (:id %) 0) (:keys res)))
      (is (every? #(>= (:expires_at %) 0) (:keys res)))
      (is (every? #(> (count (:value_prefix %)) 0) (:keys res)))
      ;; We test the values that do not change each run.
      (is (= (update-in exp [:keys] #(map api-key-constant-values %))
             (update-in res [:keys] #(map api-key-constant-values %))))))

  (testing "Delete api key"
    (let [id (-> (sut/list-api-keys settings) :keys first :id)
          exp {:id id}
          res (sut/delete-api-key! settings id)]
      (is (= exp res))))

  (testing "Getting the health information for a Typesense node"
    (let [res (sut/health settings)]
      (is (= res {:ok true}))))

  (testing "Getting metrics information"
    ;; Compare just the keys, the values change everytime the endpoint is called.
    (let [res (sut/metrics settings)
          exp #{:system_cpu1_active_percentage
                :typesense_memory_allocated_bytes
                :system_network_sent_bytes
                :typesense_memory_resident_bytes
                :system_cpu_active_percentage
                :system_memory_used_bytes
                :system_network_received_bytes
                :system_disk_total_bytes
                :typesense_memory_metadata_bytes
                :typesense_memory_fragmentation_ratio
                :system_disk_used_bytes
                :system_memory_total_bytes
                :typesense_memory_mapped_bytes
                :typesense_memory_retained_bytes
                :typesense_memory_active_bytes}]
      (is (set/subset? exp (set (keys res))))))

  (testing "Getting stats from a Typesense node"
    ;; Compare just the keys, the values might change everytime the endpoint is called.
    (let [exp {:import_latency_ms 0
               :write_requests_per_second 0
               :import_requests_per_second 0
               :write_latency_ms 0
               :latency_ms {}
               :pending_write_batches 0
               :search_requests_per_second 0
               :delete_requests_per_second 0
               :search_latency_ms 0
               :requests_per_second {}
               :total_requests_per_second 0.0
               :overloaded_requests_per_second 0
               :delete_latency_ms 0}
          res (sut/stats settings)]
      (is (= (keys res) (keys exp))))))
