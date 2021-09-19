(ns typesense.core
  (:require [java-http-clj.core :as http]
            [cheshire.core :as json]))

(def typesense-api-url "http://localhost:8108")
(def typesense-api-key "key")

(def post-request-options {:headers {"Content-Type" "application/json"
                                     "X-TYPESENSE-API-KEY" typesense-api-key}})

(defn create-collection
  "Create collection."
  [collection]
  (let [create-collection-url (str typesense-api-url "/collections")
        req-map (assoc post-request-options
                       :body
                       (json/generate-string collection))]
    (-> (http/post create-collection-url req-map)
        :body
        (json/parse-string true))))

(defn drop-collection
  "Drop collection."
  [collection-name]
  (let [drop-collection-url (str typesense-api-url "/collections/" collection-name)]
    (-> (http/delete drop-collection-url post-request-options)
        :body
        (json/parse-string true))))
