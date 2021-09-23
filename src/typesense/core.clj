(ns typesense.core
  (:require [java-http-clj.core :as http]
            [cheshire.core :as json]
            [clojure.string :as str]
            [clojure.walk :as walk]))

(def ^:private api-key-header-name "X-TYPESENSE-API-KEY")

(defn- typesense-post
  "Calls http-post with default Typesense headers."
  [settings uri data]
  (let [req-map {:headers {"Content-Type" "application/json"
                           api-key-header-name (:key settings)}
                 :body data}]
    (http/post uri req-map)))

(defn- typesense-get
  "Calls http-get with default Typesense headers."
  [settings uri]
  (let [req-map {:headers {api-key-header-name (:key settings)}}]
    (http/get uri req-map)))

(defn- typesense-delete
  "Calls http-delete with default Typesense headers."
  [settings uri]
  (let [req-map {:headers {api-key-header-name (:key settings)}}]
    (http/delete uri req-map)))

(defn- handle-response
  [response]
  (-> response
      :body
      (json/parse-string true)))

(defn- handle-jsonline-response
  [response]
  (->> response
       :body
       (str/split-lines)
       (into [] (map #(json/parse-string % true)))))

(defn- json-lines
  "Take a vector of maps and returns json-line format."
  [maps]
  (reduce (fn [acc x] (str acc (json/generate-string x) "\n"))
          ""
          maps))

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

(defn settings
  [typesense-uri typesense-key]
  {:uri typesense-uri
   :api-key typesense-key})

(defn create-collection
  "Create collection using the supplied collection schema."
  [settings collection]
  (let [uri (collection-uri settings)
        data (json/generate-string collection)]
    (handle-response (typesense-post settings uri data))))

(defn drop-collection
  "Permanently drops a collection. This action cannot be undone.
  For large collections, this might have an impact on read latencies."
  [settings collection-name]
  (let [uri (collection-uri settings collection-name)]
    (handle-response (typesense-delete settings uri))))

(defn list-collections
  "Returns a summary of all your collections.
  The collections are returned sorted by creation date,
  with the most recent collections appearing first."
  [settings]
  (let [uri (collection-uri settings)]
    (handle-response (typesense-get settings uri))))

(defn retrieve-collection
  "Retrieves collection on collection name."
  [settings collection-name]
  (let [url (collection-uri settings collection-name)]
    (handle-response (typesense-get settings url))))

(defn create-document
  "Indexes the document."
  [settings collection-name document]
  (let [url (document-uri settings collection-name)
        data (json/generate-string document)]
    (handle-response (typesense-post settings url data))))

(defn upsert-document
  "Indexes the document."
  [settings collection-name document]
  (let [uri (str (document-uri settings collection-name) "/?action=upsert")
        data (json/generate-string document)]
    (handle-response (typesense-post settings uri data))))

(defn retrieve-document
  "Retrieves the document on id in the specified collection."
  [settings collection-name id]
  (let [uri (str (document-uri settings collection-name) "/" id)]
    (handle-response (typesense-get settings uri))))

(defn delete-document
  "Deletes the document on id in the specified collection."
  [settings collection-name id]
  (let [uri (str (document-uri settings collection-name) "/" id)]
    (handle-response (typesense-delete settings uri))))

(defn import-documents
  "Imports documents in the specified collection."
  ([settings collection-name documents]
   (import-documents settings collection-name documents {:action "create"}))
  ([settings collection-name documents options]
   (let [uri (str (document-uri settings collection-name)
                  "/import"
                  (build-query options))
         data (json-lines documents)]
     (handle-jsonline-response (typesense-post settings uri data)))))

(defn export-documents
  "Exports documents in the specified collection."
  [settings collection-name options]
  (let [uri (str (document-uri settings collection-name)
                 "/export"
                 (build-query options))]
    (handle-jsonline-response (typesense-get settings uri))))

(defn search
  "Search for documents using the specified query parameters."
  [settings collection-name search-parameters]
  (let [uri (str (document-uri settings collection-name)
                 "/search"
                 (build-query search-parameters))]
    (handle-jsonline-response (typesense-get settings uri))))
