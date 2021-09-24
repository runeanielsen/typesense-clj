(ns typesense.core
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [clj-http.client :as client]))

(def ^:private api-key-header-name "X-TYPESENSE-API-KEY")

(defn- typesense-post
  "Calls http-post with default Typesense headers."
  [settings uri data]
  (let [req {:headers {"Content-Type" "application/json"
                       api-key-header-name (:key settings)}
             :body data}]
    (client/post uri req)))

(defn- typesense-get
  "Calls http-get with default Typesense headers."
  [settings uri]
  (let [req {:headers {api-key-header-name (:key settings)}}]
    (client/get uri req)))

(defn- typesense-patch
  "Calls http-patch with default Typesense headers."
  [settings uri data]
  (let [req {:headers {"Content-Type" "application/json"
                       api-key-header-name (:key settings)}
             :body data}]
    (client/patch uri req)))

(defn- typesense-delete
  "Calls http-delete with default Typesense headers."
  [settings uri]
  (let [req {:headers {api-key-header-name (:key settings)}}]
    (client/delete uri req)))

(defn- handle-json-response
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

(defn create-collection
  "Create collection using the supplied collection schema."
  [settings schema]
  (let [uri (collection-uri settings)
        data (json/generate-string schema)]
    (handle-json-response (typesense-post settings uri data))))

(defn drop-collection
  "Permanently drops a collection. This action cannot be undone.
  For large collections, this might have an impact on read latencies."
  [settings collection-name]
  (let [uri (collection-uri settings collection-name)]
    (handle-json-response (typesense-delete settings uri))))

(defn list-collections
  "Returns a summary of all your collections.
  The collections are returned sorted by creation date,
  with the most recent collections appearing first."
  [settings]
  (let [uri (collection-uri settings)]
    (handle-json-response (typesense-get settings uri))))

(defn retrieve-collection
  "Retrieves collection on collection name."
  [settings collection-name]
  (let [url (collection-uri settings collection-name)]
    (handle-json-response (typesense-get settings url))))

(defn create-document
  "Indexes the document."
  [settings collection-name document]
  (let [uri (document-uri settings collection-name)
        data (json/generate-string document)]
    (handle-json-response (typesense-post settings uri data))))

(defn upsert-document
  "Indexes the document."
  [settings collection-name document]
  (let [uri (str (document-uri settings collection-name) "/?action=upsert")
        data (json/generate-string document)]
    (handle-json-response (typesense-post settings uri data))))

(defn retrieve-document
  "Retrieves the document on id in the specified collection."
  [settings collection-name id]
  (let [uri (str (document-uri settings collection-name) "/" id)]
    (handle-json-response (typesense-get settings uri))))

(defn delete-document
  "Deletes the document on id in the specified collection."
  [settings collection-name id]
  (let [uri (str (document-uri settings collection-name) "/" id)]
    (handle-json-response (typesense-delete settings uri))))

(defn update-document
  "Update an individual document from a collection by using its id.
  The update can be partial"
  [settings collection-name id document]
  (let [uri (str (document-uri settings collection-name) "/" id)
        data (json/generate-string document)]
    (println "Test: " uri)
    (handle-json-response (typesense-patch settings uri data))))

(defn import-documents
  "Imports documents in the specified collection."
  ([settings collection-name documents]
   (import-documents settings collection-name documents {:action "create"}))
  ([settings collection-name documents parameters]
   (let [uri (str (document-uri settings collection-name)
                  "/import"
                  (build-query parameters))
         data (json-lines documents)]
     (handle-jsonline-response (typesense-post settings uri data)))))

(defn delete-documents
  "Delete documents."
  [settings collection-name parameters]
  (let [uri (str (document-uri settings collection-name)
                 (build-query parameters))]
    (handle-json-response (typesense-delete settings uri))))

(defn export-documents
  "Exports documents in the specified collection."
  [settings collection-name parameters]
  (let [uri (str (document-uri settings collection-name)
                 "/export"
                 (build-query parameters))]
    (handle-jsonline-response (typesense-get settings uri))))

(defn search
  "Search for documents using the specified query parameters."
  [settings collection-name parameters]
  (let [uri (str (document-uri settings collection-name)
                 "/search"
                 (build-query parameters))]
    (handle-jsonline-response (typesense-get settings uri))))
