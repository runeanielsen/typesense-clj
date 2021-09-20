(ns typesense.core
  (:require [java-http-clj.core :as http]
            [cheshire.core :as json]))

(def typesense-url "http://localhost:8108")
(def typesense-key "key")

(def get-req-map {:headers {"X-TYPESENSE-API-KEY" typesense-key}})
(def post-req-map {:headers {"Content-Type" "application/json" "X-TYPESENSE-API-KEY" typesense-key}})
(def delete-req-map {:headers {"X-TYPESENSE-API-KEY" typesense-key}})

(defn- handle-response
  [response]
  (-> response
      :body
      (json/parse-string true)))

(defn create-collection
  "Create collection on the supplied schema."
  [collection]
  (let [url (str typesense-url "/collections")
        req-map (assoc post-req-map
                       :body
                       (json/generate-string collection))]
    (handle-response (http/post url req-map))))

(defn drop-collection
  "Permanently drops a collection. This action cannot be undone.
  For large collections, this might have an impact on read latencies."
  [collection-name]
  (let [url (str typesense-url "/collections/" collection-name)]
    (handle-response (http/delete url delete-req-map))))

(defn list-collections
  "Returns a summary of all your collections.
  The collections are returned sorted by creation date,
  with the most recent collections appearing first."
  []
  (let [url (str typesense-url "/collections")]
    (handle-response (http/get url get-req-map))))

(defn retrieve-collection
  "Retrieves collection on collection name."
  [collection-name]
  (let [url (str typesense-url "/collections/" collection-name)]
    (handle-response (http/get url get-req-map))))

(defn index-document
  "Indexes the document."
  [collection-name document]
  (let [url (str typesense-url "/collections/" collection-name "/documents")
        req-map (assoc post-req-map
                       :body
                       (json/generate-string document))]
    (handle-response (http/post url req-map))))
