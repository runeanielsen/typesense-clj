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
  (let [{:keys [uri req]} (api/create-collection-req settings
                                                     schema)]
    (handle-json-response (client/post uri req))))

(defn drop-collection!
  "Permanently drops a collection. This action cannot be undone.
  For large collections, this might have an impact on read latencies."
  [settings collection-name]
  (let [{:keys [uri req]} (api/drop-collection-req settings
                                                   collection-name)]
    (handle-json-response (client/delete uri req))))

(defn list-collections!
  "Returns a summary of all your collections.
  The collections are returned sorted by creation date,
  with the most recent collections appearing first."
  [settings]
  (let [{:keys [uri req]} (api/list-collections-req settings)]
    (println uri)
    (println req)
    (handle-json-response (client/get uri req))))

(defn retrieve-collection!
  "Retrieves collection on collection name."
  [settings collection-name]
  (let [{:keys [uri req]} (api/retrieve-collection-req settings
                                                       collection-name)]
    (handle-json-response (client/get uri req))))

(defn create-document!
  "Indexes the document."
  [settings collection-name document]
  (let [{:keys [uri req]} (api/create-document-req
                           settings
                           collection-name
                           document)]
    (handle-json-response (client/post uri req))))

(defn upsert-document!
  "Indexes the document."
  [settings collection-name document]
  (let [{:keys [uri req]} (api/upsert-document-req settings
                                                   collection-name
                                                   document)]
    (handle-json-response (client/post uri req))))

(defn retrieve-document!
  "Retrieves the document on id in the specified collection."
  [settings collection-name id]
  (let [{:keys [uri req]} (api/retrieve-document-req settings
                                                     collection-name
                                                     id)]
    (handle-json-response (client/get uri req))))

(defn delete-document!
  "Deletes the document on id in the specified collection."
  [settings collection-name id]
  (let [{:keys [uri req]} (api/delete-document-req settings
                                                   collection-name
                                                   id)]
    (handle-json-response (client/delete uri req))))

(defn update-document!
  "Update an individual document from a collection by using its id.
  The update can be partial"
  [settings collection-name id document]
  (let [{:keys [uri req]} (api/update-document-req settings
                                                   collection-name
                                                   id
                                                   document)]
    (handle-json-response (client/patch uri req))))

(defn import-documents!
  "Imports documents in the specified collection."
  ([settings collection-name documents]
   (import-documents! settings collection-name documents {}))
  ([settings collection-name documents parameters]
   (let [{:keys [uri req]} (api/import-documents-req settings
                                                     collection-name
                                                     documents
                                                     parameters)]
     (handle-jsonline-response (client/post uri req)))))

(defn delete-documents!
  "Delete documents."
  [settings collection-name parameters]
  (let [{:keys [uri req]} (api/delete-documents-req settings
                                                    collection-name
                                                    parameters)]
    (handle-json-response (client/delete uri req))))

(defn export-documents!
  "Exports documents in the specified collection."
  [settings collection-name parameters]
  (let [{:keys [uri req]} (api/export-documents-req settings
                                                    collection-name
                                                    parameters)]
    (handle-jsonline-response (client/get uri req))))

(defn search!
  "Search for documents using the specified query parameters."
  [settings collection-name parameters]
  (let [{:keys [uri req]} (api/search-req settings
                                          collection-name
                                          parameters)]
    (handle-jsonline-response (client/get uri req))))

(defn create-api-key!
  "Create api-key."
  [settings parameters]
  (let [{:keys [uri req]} (api/create-api-key-req settings
                                                  parameters)]
    (handle-json-response (client/post uri req))))

(defn retrieve-api-key!
  "Retrives api-key on id."
  [settings id]
  (let [{:keys [uri req]} (api/retrieve-api-key-req settings
                                                    id)]
    (handle-json-response (client/get uri req))))

(defn list-api-keys!
  "List api-keys."
  [settings]
  (let [{:keys [uri req]} (api/list-api-keys-req settings)]
    (handle-json-response (client/get uri req))))

(defn delete-api-key!
  "Deletes api-key on id."
  [settings id]
  (let [{:keys [uri req]} (api/delete-api-key-req settings
                                                  id)]
    (handle-json-response (client/delete uri req))))

(defn upsert-override!
  "Upsert override."
  [settings collection-name override-name override]
  (let [{:keys [uri req]} (api/upsert-override-req settings
                                                   collection-name
                                                   override-name
                                                   override)]
    (handle-json-response (client/put uri req))))

(defn list-overrides!
  "Lists overrides."
  [settings collection-name]
  (let [{:keys [uri req]} (api/list-overrides-req settings
                                                  collection-name)]
    (handle-json-response (client/get uri req))))

(defn retrieve-override!
  "Retrieve override on name."
  [settings collection-name override-name]
  (let [{:keys [uri req]} (api/retrieve-override-req settings
                                                     collection-name
                                                     override-name)]
    (handle-json-response (client/get uri req))))

(defn delete-override!
  "Delete override on name."
  [settings collection-name override-name]
  (let [{:keys [uri req]} (api/delete-override-req settings
                                                   collection-name
                                                   override-name)]
    (handle-json-response (client/delete uri req))))

(defn upsert-alias!
  "Upsert alias."
  [settings collection-name alias-collection]
  (let [{:keys [uri req]} (api/upsert-alias-req settings
                                                collection-name
                                                alias-collection)]
    (handle-json-response (client/put uri req))))

(defn list-aliases
  "List aliases."
  [settings]
  (let [{:keys [uri req]} (api/list-aliases-req settings)]
    (handle-json-response (client/get uri req))))

(defn retrieve-alias!
  "Retrieves alias on collection-name."
  [settings collection-name]
  (let [{:keys [uri req]} (api/retrieve-alias-req settings
                                                  collection-name)]
    (handle-json-response (client/get uri req))))

(defn delete-alias!
  "Delete alias on collection-name"
  [settings collection-name]
  (let [{:keys [uri req]} (api/delete-alias-req settings
                                                collection-name)]
    (handle-json-response (client/delete uri req))))

(defn upsert-synonym!
  "Upsert synonym."
  [settings collection-name synonym-name synonyms]
  (let [{:keys [uri req]} (api/upsert-synonym-req settings
                                                  collection-name
                                                  synonym-name
                                                  synonyms)]
    (handle-json-response (client/put uri req))))

(defn retrieve-synonym!
  "Retrieve synonym on synonym-name in collection."
  [settings collection-name synonym-name]
  (let [{:keys [uri req]} (api/retrieve-synonym-req settings
                                                    collection-name
                                                    synonym-name)]
    (handle-json-response (client/get uri req))))

(defn list-synonyms!
  "List synonyms in collection"
  [settings collection-name]
  (let [{:keys [uri req]} (api/list-synonyms-req settings
                                                 collection-name)]
    (handle-json-response (client/get uri req))))

(defn delete-synonym!
  "Delete synonym on synonym-name in collection."
  [settings collection-name synonym-name]
  (let [{:keys [uri req]} (api/delete-synonym-req settings
                                                  collection-name
                                                  synonym-name)]
    (handle-json-response (client/delete uri req))))
