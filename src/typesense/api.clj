(ns typesense.api
  (:require [typesense.utils :as utils]
            [cheshire.core :as json]))

(def ^:private api-key-header-name "X-TYPESENSE-API-KEY")

(defn create-collection-req
  [settings schema]
  {:uri (utils/collection-uri settings)
   :req {:headers {api-key-header-name (:key settings)
                   "Content-Type" "application/json"}
         :body (json/generate-string schema)}})

(defn drop-collection-req
  [settings collection-name]
  {:uri (utils/collection-uri settings collection-name)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn list-collections-req
  [settings]
  {:uri (utils/collection-uri settings)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn retrieve-collection-req
  [settings collection-name]
  {:uri (utils/collection-uri settings collection-name)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn create-document-req
  [settings collection-name document]
  {:uri (utils/document-uri settings collection-name)
   :req {:headers {api-key-header-name (:key settings)
                   "Content-Type" "application/json"}
         :body (json/generate-string document)}})

(defn upsert-document-req
  [settings collection-name document]
  {:uri (str (utils/document-uri settings collection-name) "?action=upsert")
   :req {:headers {api-key-header-name (:key settings)
                   "Content-Type" "application/json"}
         :body (json/generate-string document)}})

(defn retrieve-document-req
  [settings collection-name id]
  {:uri (str (utils/document-uri settings collection-name) "/" id)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn delete-document-req
  [settings collection-name id]
  {:uri (str (utils/document-uri settings collection-name) "/" id)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn update-document-req
  [settings collection-name id document]
  {:uri (str (utils/document-uri settings collection-name) "/" id)
   :req {:headers {api-key-header-name (:key settings)
                   "Content-Type" "application/json"}
         :body (json/generate-string document)}})

(defn import-documents-req
  ([settings collection-name documents]
   (import-documents-req settings
                         collection-name
                         documents
                         {}))
  ([settings collection-name documents parameters]
   {:uri (str (utils/document-uri settings collection-name)
              "/import"
              (utils/build-query parameters))
    :req {:headers {api-key-header-name (:key settings)
                    "Content-Type" "text/plain"}
          :body (utils/json-lines documents)}}))

(defn delete-documents-req
  [settings collection-name parameters]
  {:uri (str (utils/document-uri settings collection-name)
             (utils/build-query parameters))
   :req {:headers {api-key-header-name (:key settings)}}})

(defn export-documents-req
  [settings collection-name parameters]
  {:uri (str (utils/document-uri settings collection-name)
             "/export"
             (utils/build-query parameters))
   :req {:headers {api-key-header-name (:key settings)}}})

(defn search-req
  [settings collection-name parameters]
  {:uri (str (utils/document-uri settings collection-name)
             "/search"
             (utils/build-query parameters))
   :req {:headers {api-key-header-name (:key settings)}}})
