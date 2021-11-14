(ns typesense.api-test
  (:require [typesense.api :as sut]
            [clojure.test :refer [deftest is]]
            [clojure.data.json :as json]))

(def ^:private settings
  {:uri "http://localhost:8108" :key "key"})

(deftest create-collection-req-test
  (let [schema {:name "test_collection"
                :fields [{:name "test_name"
                          :type "string"}
                         {:name "test_count"
                          :type "int32"}]
                :default_sorting_field "test_count"}
        req (sut/create-collection-req settings schema)
        exp {:uri "http://localhost:8108/collections"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"
                             "Content-Type" "application/json"}
                   :body (json/write-str schema)}}]
    (is (= exp req))))

(deftest drop-collection-req-test
  (let [req (sut/drop-collection-req settings "test_collection")
        exp {:uri "http://localhost:8108/collections/test_collection"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest list-collections-req-test
  (let [req (sut/list-collections-req settings)
        exp {:uri "http://localhost:8108/collections"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest retrieve-collection-req-test
  (let [req (sut/retrieve-collection-req settings "test_collection")
        exp {:uri "http://localhost:8108/collections/test_collection"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest create-document-req-test
  (let [document {:test_name "test_document_two"
                  :test_count 2
                  :id "1"}
        req (sut/create-document-req settings "test_collection" document)
        exp {:uri "http://localhost:8108/collections/test_collection/documents"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"
                             "Content-Type" "application/json"}
                   :body (json/write-str document)}}]
    (is (= exp req))))

(deftest upsert-document-req-test
  (let [document {:test_name "test_document_two"
                  :test_count 2
                  :id "1"}
        req (sut/upsert-document-req settings "test_collection" document)
        exp {:uri "http://localhost:8108/collections/test_collection/documents?action=upsert"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"
                             "Content-Type" "application/json"}
                   :body (json/write-str document)}}]
    (is (= exp req))))

(deftest retrieve-document-req-test
  (let [req (sut/retrieve-document-req settings "test_collection" 0)
        exp {:uri "http://localhost:8108/collections/test_collection/documents/0"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest delete-document-req-test
  (let [req (sut/delete-document-req settings "test_collection" 0)
        exp {:uri "http://localhost:8108/collections/test_collection/documents/0"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest update-document-req-test
  (let [document {:test_name "test_document_updated"
                  :id "0"}
        req (sut/update-document-req settings
                                     "test_collection"
                                     0
                                     document)
        exp {:uri "http://localhost:8108/collections/test_collection/documents/0"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"
                             "Content-Type" "application/json"}
                   :body (json/write-str document)}}]
    (is (= exp req))))

(deftest import-document-req-test
  (let [documents [{:test_name "test_document_two"
                    :test_count 2}
                   {:test_name "test_document_three"
                    :test_count 3}
                   {:test_name "test_document_four"
                    :test_count 4}]
        req (sut/import-documents-req settings
                                      "test_collection"
                                      documents)
        exp {:uri "http://localhost:8108/collections/test_collection/documents/import"
             :req {:headers {"X-TYPESENSE-API-KEY" "key",
                             "Content-Type" "text/plain"}
                   :body "{\"test_name\":\"test_document_two\",\"test_count\":2}\n{\"test_name\":\"test_document_three\",\"test_count\":3}\n{\"test_name\":\"test_document_four\",\"test_count\":4}\n"}}]
    (is (= exp req))))

(deftest import-document-req-upsert-test
  (let [documents [{:test_name "test_document_two"
                    :test_count 2}
                   {:test_name "test_document_three"
                    :test_count 3}
                   {:test_name "test_document_four"
                    :test_count 4}]
        req (sut/import-documents-req settings
                                      "test_collection"
                                      documents {:action "upsert" :batch_size 100})
        exp {:uri "http://localhost:8108/collections/test_collection/documents/import?action=upsert&batch_size=100"
             :req {:headers {"X-TYPESENSE-API-KEY" "key",
                             "Content-Type" "text/plain"}
                   :body "{\"test_name\":\"test_document_two\",\"test_count\":2}\n{\"test_name\":\"test_document_three\",\"test_count\":3}\n{\"test_name\":\"test_document_four\",\"test_count\":4}\n"}}]
    (is (= exp req))))

(deftest import-document-req-update-test
  (let [documents [{:test_name "test_document_two"
                    :test_count 2}
                   {:test_name "test_document_three"
                    :test_count 3}
                   {:test_name "test_document_four"
                    :test_count 4}]
        req (sut/import-documents-req settings
                                      "test_collection"
                                      documents {:action "update" :batch_size 100})
        exp {:uri "http://localhost:8108/collections/test_collection/documents/import?action=update&batch_size=100"
             :req {:headers {"X-TYPESENSE-API-KEY" "key",
                             "Content-Type" "text/plain"}
                   :body "{\"test_name\":\"test_document_two\",\"test_count\":2}\n{\"test_name\":\"test_document_three\",\"test_count\":3}\n{\"test_name\":\"test_document_four\",\"test_count\":4}\n"}}]
    (is (= exp req))))

(deftest delete-documents-req-test
  (let [req (sut/delete-documents-req settings
                                      "test_collection"
                                      {:filter_by "test_count:=>0"
                                       :batch_size 40})
        exp {:uri "http://localhost:8108/collections/test_collection/documents?filter_by=test_count%3A%3D%3E0&batch_size=40"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest export-documents-req-test
  (let [req (sut/export-documents-req settings
                                      "test_collection"
                                      {:filter_by "test_count:=>0"})
        exp {:uri "http://localhost:8108/collections/test_collection/documents/export?filter_by=test_count%3A%3D%3E0"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest search-req-test
  (let [req (sut/search-req settings
                            "test_collection"
                            {:q "test_document_one" :query_by "test_name"})
        exp {:uri "http://localhost:8108/collections/test_collection/documents/search?q=test_document_one&query_by=test_name"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest multi-search-req-test
  (let [req (sut/multi-search-req settings
                                  {:searches [{:collection "products"
                                               :q "shoe"
                                               :filter_by "price:=[50..120]"}
                                              {:collection "brands"
                                               :q "Nike"}]}
                                  {:query_by "name"})
        exp {:uri "http://localhost:8108/multi_search?query_by=name"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"
                             "Content-Type" "application/json"}
                   :body "{\"searches\":[{\"collection\":\"products\",\"q\":\"shoe\",\"filter_by\":\"price:=[50..120]\"},{\"collection\":\"brands\",\"q\":\"Nike\"}]}"}}]
    (is (= exp req))))

(deftest create-api-key-req-test
  (let [req (sut/create-api-key-req settings
                                    {:description "Search only companies key."
                                     :actions ["document:search"]
                                     :collections ["companies"]})
        exp {:uri "http://localhost:8108/keys"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}
                   :body "{\"description\":\"Search only companies key.\",\"actions\":[\"document:search\"],\"collections\":[\"companies\"]}"}}]
    (is (= exp req))))

(deftest retrieve-api-key-req-test
  (let [req (sut/retrieve-api-key-req settings 0)
        exp {:uri "http://localhost:8108/keys/0"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest lsit-api-keys-req-test
  (let [req (sut/list-api-keys-req settings)
        exp {:uri "http://localhost:8108/keys"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest delete-api-key-req-test
  (let [req (sut/delete-api-key-req settings 0)
        exp {:uri "http://localhost:8108/keys/0"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest upsert-override-req-test
  (let [override {:rule {:query "apple"
                         :match "exact"}
                  :includes [{:id "422" :position 1}
                             {:id "54" :position 2}]
                  :excludes [{:id "287"}]}
        req (sut/upsert-override-req settings
                                     "companies"
                                     "customize-apple"
                                     override)
        exp {:uri
             "http://localhost:8108/collections/companies/overrides/customize-apple"
             :req
             {:headers {"X-TYPESENSE-API-KEY" "key"
                        "Content-Type" "text/json"}
              :body "{\"rule\":{\"query\":\"apple\",\"match\":\"exact\"},\"includes\":[{\"id\":\"422\",\"position\":1},{\"id\":\"54\",\"position\":2}],\"excludes\":[{\"id\":\"287\"}]}"}}]
    (is (= exp req))))

(deftest list-overrides-req-test
  (let [req (sut/list-overrides-req settings
                                    "companies")
        exp {:uri "http://localhost:8108/collections/companies/overrides"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest retrieve-override-req-test
  (let [req (sut/retrieve-override-req settings
                                       "companies"
                                       "customize-apple")
        exp {:uri "http://localhost:8108/collections/companies/overrides/customize-apple"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest delete-override-req-test
  (let [req (sut/delete-override-req settings
                                     "companies"
                                     "customize-apple")
        exp {:uri "http://localhost:8108/collections/companies/overrides/customize-apple"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest upsert-alias-req-test
  (let [req (sut/upsert-alias-req settings
                                  "companies"
                                  {:collection_name "companies_june11"})
        exp {:uri "http://localhost:8108/aliases/companies"
             :req {:headers {"X-TYPESENSE-API-KEY" "key",
                             "Content-Type" "text/json"}
                   :body "{\"collection_name\":\"companies_june11\"}"}}]
    (is (= exp req))))

(deftest retrieve-alias-req-test
  (let [req (sut/retrieve-alias-req settings
                                    "companies")
        exp {:uri "http://localhost:8108/aliases/companies"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest list-aliases-req-test
  (let [req (sut/list-aliases-req settings)
        exp {:uri "http://localhost:8108/aliases"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest delete-alias-req-test
  (let [req (sut/delete-alias-req settings
                                  "companies")
        exp {:uri "http://localhost:8108/aliases/companies"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest upsert-synonym-req-test
  (let [req (sut/upsert-synonym-req settings
                                    "products"
                                    "coat-synonyms"
                                    {:synonyms ["blazer" "coat" "jacket"]})

        exp {:uri "http://localhost:8108/collections/products/synonyms/coat-synonyms"
             :req {:headers {"X-TYPESENSE-API-KEY" "key", "Content-Type" "text/json"}
                   :body "{\"synonyms\":[\"blazer\",\"coat\",\"jacket\"]}"}}]
    (is (= exp req))))

(deftest retrieve-synonym-req-test
  (let [req (sut/retrieve-synonym-req settings
                                      "products"
                                      "coat-synonyms")
        exp {:uri "http://localhost:8108/collections/products/synonyms/coat-synonyms"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest list-synonyms-req-test
  (let [req (sut/list-synonyms-req settings
                                   "products")
        exp {:uri "http://localhost:8108/collections/products/synonyms"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest delete-synonyms-req-test
  (let [req (sut/delete-synonym-req settings
                                    "products"
                                    "coat-synonyms")
        exp {:uri "http://localhost:8108/collections/products/synonyms/coat-synonyms"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))
