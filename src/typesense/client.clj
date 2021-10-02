(ns typesense.client
  (:require [typesense.api :as api]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn- handle-json-response
  "Handles JSON response from Typesense."
  [response]
  (-> response
      :body
      (json/parse-string true)))

(defn- handle-jsonline-response
  "Handles JSON-line response from Typesense."
  [response]
  (->> response
       :body
       str/split-lines
       (map #(json/parse-string % true))
       (into [])))

(defn create-collection!
  "Create collection using the supplied collection schema."
  [settings schema]
  (let [{uri :uri req :req} (api/create-collection-req settings
                                                       schema)]
    (handle-json-response (client/post uri req))))

(defn drop-collection!
  "Permanently drops a collection. This action cannot be undone.
  For large collections, this might have an impact on read latencies."
  [settings collection-name]
  (let [{uri :uri req :req} (api/drop-collection-req settings
                                                     collection-name)]
    (handle-json-response (client/delete uri req))))

(defn list-collections!
  "Returns a summary of all your collections.
  The collections are returned sorted by creation date,
  with the most recent collections appearing first."
  [settings]
  (let [{uri :uri req :req} (api/list-collections-req settings)]
    (handle-json-response (client/get uri req))))

(defn retrieve-collection!
  "Retrieves collection on collection name."
  [settings collection-name]
  (let [{uri :uri req :req} (api/retrieve-collection-req settings
                                                         collection-name)]
    (handle-json-response (client/get uri req))))

(defn create-document!
  "Indexes the document."
  [settings collection-name document]
  (let [{uri :uri req :req} (api/create-document-req
                             settings
                             collection-name
                             document)]
    (handle-json-response (client/post uri req))))

(defn upsert-document!
  "Indexes the document."
  [settings collection-name document]
  (let [{uri :uri req :req} (api/upsert-document-req settings
                                                     collection-name
                                                     document)]
    (handle-json-response (client/post uri req))))

(defn retrieve-document!
  "Retrieves the document on id in the specified collection."
  [settings collection-name id]
  (let [{uri :uri req :req} (api/retrieve-document-req settings
                                                       collection-name
                                                       id)]
    (handle-json-response (client/get uri req))))

(defn delete-document!
  "Deletes the document on id in the specified collection."
  [settings collection-name id]
  (let [{uri :uri req :req} (api/delete-document-req settings
                                                     collection-name
                                                     id)]
    (handle-json-response (client/delete uri req))))

(defn update-document!
  "Update an individual document from a collection by using its id.
  The update can be partial"
  [settings collection-name id document]
  (let [{uri :uri req :req} (api/update-document-req settings
                                                     collection-name
                                                     id
                                                     document)]
    (handle-json-response (client/patch uri req))))

(defn import-documents!
  "Imports documents in the specified collection."
  ([settings collection-name documents]
   (import-documents! settings collection-name documents {}))
  ([settings collection-name documents parameters]
   (let [{uri :uri req :req} (api/import-documents-req settings
                                                       collection-name
                                                       documents
                                                       parameters)]
     (handle-jsonline-response (client/post uri req)))))

(defn delete-documents!
  "Delete documents."
  [settings collection-name parameters]
  (let [{uri :uri req :req} (api/delete-documents-req settings
                                                      collection-name
                                                      parameters)]
    (handle-json-response (client/delete uri req))))

(defn export-documents!
  "Exports documents in the specified collection."
  [settings collection-name parameters]
  (let [{uri :uri req :req} (api/export-documents-req settings
                                                      collection-name
                                                      parameters)]
    (handle-jsonline-response (client/get uri req))))

(defn search!
  "Search for documents using the specified query parameters."
  [settings collection-name parameters]
  (let [{uri :uri req :req} (api/search-req settings
                                            collection-name
                                            parameters)]
    (handle-jsonline-response (client/get uri req))))

(defn create-api-key!
  "Create api-key."
  [settings parameters]
  (let [{uri :uri req :req} (api/create-api-key-req settings
                                                    parameters)]
    (handle-json-response (client/post uri req))))

(defn retrieve-api-key!
  "Retrives api-key on id."
  [settings id]
  (let [{uri :uri req :req} (api/retrieve-api-key-req settings
                                                      id)]
    (handle-json-response (client/get uri req))))

(defn list-api-keys!
  "List api-keys."
  [settings]
  (let [{uri :uri req :req} (api/list-api-keys-req settings)]
    (handle-json-response (client/get uri req))))

(defn delete-api-key!
  "Deletes api-key on id."
  [settings id]
  (let [{uri :uri req :req} (api/delete-api-key-req settings
                                                    id)]
    (handle-json-response (client/delete uri req))))

(defn upsert-override!
  "Upsert override."
  [settings collection-name override-name override]
  (let [{uri :uri req :req} (api/upsert-override-req settings
                                                     collection-name
                                                     override-name
                                                     override)]
    (handle-json-response (client/put uri req))))

(defn list-overrides!
  "Lists overrides."
  [settings collection-name]
  (let [{uri :uri req :req} (api/list-overrides-req settings
                                                    collection-name)]
    (handle-json-response (client/get uri req))))

(defn retrieve-override!
  "Retrieve override on name."
  [settings collection-name override-name]
  (let [{uri :uri req :req} (api/retrieve-override-req settings
                                                       collection-name
                                                       override-name)]
    (handle-json-response (client/get uri req))))

(defn delete-override!
  "Delete override on name."
  [settings collection-name override-name]
  (let [{uri :uri req :req} (api/delete-override-req settings
                                                     collection-name
                                                     override-name)]
    (handle-json-response (client/delete uri req))))
