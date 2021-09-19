(ns typesense.core
  (:require [java-http-clj.core :as http]
            [cheshire.core :as json]))

(def typesense-api-url "http://localhost:8108")
(def typesense-api-key "key")

(def get-request-options {:headers {"X-TYPESENSE-API-KEY" typesense-api-key}})
(def post-request-options {:headers {"Content-Type" "application/json"
                                     "X-TYPESENSE-API-KEY" typesense-api-key}})
(def delete-request-options {:headers {"X-TYPESENSE-API-KEY" typesense-api-key}})

(defn create-collection
  "Create collection on the supplied schema."
  [collection]
  (let [url (str typesense-api-url "/collections")
        req-map (assoc post-request-options
                       :body
                       (json/generate-string collection))]
    (-> (http/post url req-map)
        :body
        (json/parse-string true))))

(defn drop-collection
  "Permanently drops a collection. This action cannot be undone.
  For large collections, this might have an impact on read latencies."
  [collection-name]
  (let [url (str typesense-api-url "/collections/" collection-name)]
    (-> (http/delete url delete-request-options)
        :body
        (json/parse-string true))))

(defn list-collections
  "Returns a summary of all your collections.
  The collections are returned sorted by creation date,
  with the most recent collections appearing first."
  []
  (let [url (str typesense-api-url "/collections")]
    (-> (http/get url get-request-options)
        :body
        (json/parse-string true))))

(defn retrieve-collection
  "Retrieves collection on collection name."
  [collection-name]
  (let [url (str typesense-api-url "/collections/" collection-name)]
    (-> (http/get url get-request-options)
        :body
        (json/parse-string true))))
