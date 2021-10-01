(ns typesense.api-test
  (:require [typesense.api :as sut]
            [clojure.test :refer [deftest is]]
            [cheshire.core :as json]))

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
                   :body (json/generate-string schema)}}]
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
                   :body (json/generate-string document)}}]
    (is (= exp req))))

(deftest upsert-document-req-test
  (let [document {:test_name "test_document_two"
                  :test_count 2
                  :id "1"}
        req (sut/upsert-document-req settings "test_collection" document)
        exp {:uri "http://localhost:8108/collections/test_collection/documents?action=upsert"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"
                             "Content-Type" "application/json"}
                   :body (json/generate-string document)}}]
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
                   :body (json/generate-string document)}}]
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
        exp {:uri "http://localhost:8108/collections/test_collection/documents?filter_by=test_count:=>0&batch_size=40"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest export-doucments-req-test
  (let [req (sut/export-documents-req settings
                                      "test_collection"
                                      {:filter_by "test_count:=>0"})
        exp {:uri "http://localhost:8108/collections/test_collection/documents/export?filter_by=test_count:=>0"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
    (is (= exp req))))

(deftest search-req-test
  (let [req (sut/search-req settings
                            "test_collection"
                            {:q "test_document_one" :query_by "test_name"})
        exp {:uri "http://localhost:8108/collections/test_collection/documents/search?q=test_document_one&query_by=test_name"
             :req {:headers {"X-TYPESENSE-API-KEY" "key"}}}]
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
