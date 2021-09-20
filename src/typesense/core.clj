(ns typesense.core
  (:require [java-http-clj.core :as http]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn- typesense-post
  "Calls http-post with default Typesense headers."
  [settings uri data]
  (let [req-map {:headers {"Content-Type" "application/json"
                           "X-TYPESENSE-API-KEY" (:api-key settings)}
                 :body data}]
    (http/post uri req-map)))

(defn- typesense-get
  "Calls http-get with default Typesense headers."
  [settings uri]
  (let [req-map {:headers {"X-TYPESENSE-API-KEY" (:api-key settings)}}]
    (http/get uri req-map)))

(defn- typesense-delete
  "Calls http-delete with default Typesense headers."
  [settings uri]
  (let [req-map {:headers {"X-TYPESENSE-API-KEY" (:api-key settings)}}]
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
  (let [jsonlines
        (reduce (fn [acc x] (str acc (json/generate-string x) "\n"))
                ""
                (butlast maps))]
    (str jsonlines (json/generate-string (last maps)))))

(defn settings
  [typesense-uri typesense-key]
  {:api-uri typesense-uri
   :api-key typesense-key})

(defn create-collection
  "Create collection using the supplied collection schema."
  [settings collection]
  (let [uri (str (:api-uri settings) "/collections")
        data (json/generate-string collection)]
    (handle-response (typesense-post settings uri data))))

(defn drop-collection
  "Permanently drops a collection. This action cannot be undone.
  For large collections, this might have an impact on read latencies."
  [settings collection-name]
  (let [uri (str (:api-uri settings) "/collections/" collection-name)]
    (handle-response (typesense-delete settings uri))))

(defn list-collections
  "Returns a summary of all your collections.
  The collections are returned sorted by creation date,
  with the most recent collections appearing first."
  [settings]
  (let [uri (str (:api-uri settings) "/collections")]
    (handle-response (typesense-get settings uri))))

(defn retrieve-collection
  "Retrieves collection on collection name."
  [settings collection-name]
  (let [url (str (:api-uri settings) "/collections/" collection-name)]
    (handle-response (typesense-get settings url))))

(defn create-document
  "Indexes the document."
  [settings collection-name document]
  (let [url (str (:api-uri settings) "/collections/" collection-name "/documents")
        data (json/generate-string document)]
    (handle-response (typesense-post settings url data))))

(defn upsert-document
  "Indexes the document."
  [settings collection-name document]
  (let [uri (str (:api-uri settings) "/collections/" collection-name "/documents?action=upsert")
        data (json/generate-string document)]
    (handle-response (typesense-post settings uri data))))

(defn retrieve-document
  "Retrieves the document on id in the specified collection."
  [settings collection-name id]
  (let [uri (str (:api-uri settings) "/collections/" collection-name "/documents/" id)]
    (handle-response (typesense-get settings uri))))

(defn delete-document
  "Deletes the document on id in the specified collection."
  [settings collection-name id]
  (let [uri (str (:api-uri settings) "/collections/" collection-name "/documents/" id)]
    (handle-response (typesense-delete settings uri))))

(defn import-documents
  "Imports documents on in the specified collection."
  [settings collection-name documents]
  (let [uri (str (:api-uri settings) "/collections/" collection-name "/documents/import?action=create")
        data (json-lines documents)]
    (handle-jsonline-response (typesense-post settings uri data))))
