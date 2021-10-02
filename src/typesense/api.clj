(ns typesense.api
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [clojure.walk :as walk]))

(def ^:private api-key-header-name "X-TYPESENSE-API-KEY")

(defn- collection-uri
  "Returns the collection uri resource path."
  ([settings]
   (str (:uri settings) "/collections"))
  ([settings collection-name]
   (str (collection-uri settings) "/" collection-name)))

(defn- document-uri
  "Returns the document uri resource path."
  [settings collection-name]
  (str (:uri settings) "/collections/" collection-name "/documents"))

(defn- keys-uri
  "Return the keys uri resource path."
  [settings]
  (str (:uri settings) "/keys"))

(defn- overrides-uri
  "Returns the overrides uri resource path."
  [settings collection-name]
  (str (:uri settings) "/collections/" collection-name "/overrides"))

(defn- build-query
  "Convert param pairs into a valid query string."
  [query-params]
  (if (= (count query-params) 0)
    ""
    (->> query-params
         walk/stringify-keys
         (map #(str (key %) "=" (val %)))
         (str/join \&)
         (str "?"))))

(defn- json-lines
  "Take a vector of maps and returns json-line format."
  [maps]
  (reduce (fn [acc x] (str acc (json/generate-string x) "\n"))
          ""
          maps))

(defn create-collection-req
  [settings schema]
  {:uri (collection-uri settings)
   :req {:headers {api-key-header-name (:key settings)
                   "Content-Type" "application/json"}
         :body (json/generate-string schema)}})

(defn drop-collection-req
  [settings collection-name]
  {:uri (collection-uri settings collection-name)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn list-collections-req
  [settings]
  {:uri (collection-uri settings)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn retrieve-collection-req
  [settings collection-name]
  {:uri (collection-uri settings collection-name)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn create-document-req
  [settings collection-name document]
  {:uri (document-uri settings collection-name)
   :req {:headers {api-key-header-name (:key settings)
                   "Content-Type" "application/json"}
         :body (json/generate-string document)}})

(defn upsert-document-req
  [settings collection-name document]
  {:uri (str (document-uri settings collection-name) "?action=upsert")
   :req {:headers {api-key-header-name (:key settings)
                   "Content-Type" "application/json"}
         :body (json/generate-string document)}})

(defn retrieve-document-req
  [settings collection-name id]
  {:uri (str (document-uri settings collection-name) "/" id)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn delete-document-req
  [settings collection-name id]
  {:uri (str (document-uri settings collection-name) "/" id)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn update-document-req
  [settings collection-name id document]
  {:uri (str (document-uri settings collection-name) "/" id)
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
   {:uri (str (document-uri settings collection-name)
              "/import"
              (build-query parameters))
    :req {:headers {api-key-header-name (:key settings)
                    "Content-Type" "text/plain"}
          :body (json-lines documents)}}))

(defn delete-documents-req
  [settings collection-name parameters]
  {:uri (str (document-uri settings collection-name)
             (build-query parameters))
   :req {:headers {api-key-header-name (:key settings)}}})

(defn export-documents-req
  [settings collection-name parameters]
  {:uri (str (document-uri settings collection-name)
             "/export"
             (build-query parameters))
   :req {:headers {api-key-header-name (:key settings)}}})

(defn search-req
  [settings collection-name parameters]
  {:uri (str (document-uri settings collection-name)
             "/search"
             (build-query parameters))
   :req {:headers {api-key-header-name (:key settings)}}})

(defn create-api-key-req
  [settings parameters]
  {:uri (keys-uri settings)
   :req {:headers {api-key-header-name (:key settings)}
         :body (json/generate-string parameters)}})

(defn retrieve-api-key-req
  [settings id]
  {:uri (str (keys-uri settings) "/" id)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn list-api-keys-req
  [settings]
  {:uri (keys-uri settings)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn delete-api-key-req
  [settings id]
  {:uri (str (keys-uri settings) "/" id)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn upsert-override-req
  [settings collection-name override-name override]
  {:uri (str (overrides-uri settings collection-name) "/" override-name)
   :req {:headers {api-key-header-name (:key settings)
                   "Content-Type" "text/plain"}
         :body (json/generate-string override)}})

(defn list-overrides-req
  [settings collection-name]
  {:uri (overrides-uri settings collection-name)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn retrieve-override-req
  [settings collection-name override-name]
  {:uri (str (overrides-uri settings collection-name) "/" override-name)
   :req {:headers {api-key-header-name (:key settings)}}})

(defn delete-override-req
  [settings collection-name override-name]
  {:uri (str (overrides-uri settings collection-name) "/" override-name)
   :req {:headers {api-key-header-name (:key settings)}}})
