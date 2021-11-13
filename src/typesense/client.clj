(ns typesense.client
  (:require [typesense.api :as api]
            [typesense.util :as util]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [slingshot.slingshot :refer [try+]]))

(defn- http-ex-data->typesense-ex-info
  [{:keys [status body]}]
  (let [message (-> (json/read-str body :key-fn keyword) :message)]
    (case status
      400 (ex-info message {:type ::bad-request :message message})
      401 (ex-info message {:type ::unauthorized :message message})
      404 (ex-info message {:type ::not-found :message message})
      409 (ex-info message {:type ::conflict :message message})
      422 (ex-info message {:type ::unprocessable-entity :message message})
      503 (ex-info message {:type ::service-unavailable :message message})
      (ex-info message {:type ::unspecified-api-error :message message}))))

(defn create-collection!
  "Create collection using the supplied collection schema."
  [settings schema]
  (try+
   (let [{:keys [uri req]} (api/create-collection-req settings schema)]
     (util/http-response-json->hash-map (http/post uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn drop-collection!
  "Permanently drops a collection. This action cannot be undone.
  For large collections, this might have an impact on read latencies."
  [settings collection-name]
  (try+
   (let [{:keys [uri req]} (api/drop-collection-req settings collection-name)]
     (util/http-response-json->hash-map (http/delete uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn list-collections
  "Returns a summary of all your collections.
  The collections are returned sorted by creation date,
  with the most recent collections appearing first."
  [settings]
  (try+
   (let [{:keys [uri req]} (api/list-collections-req settings)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn retrieve-collection
  "Retrieves collection on collection name."
  [settings collection-name]
  (try+
   (let [{:keys [uri req]} (api/retrieve-collection-req settings collection-name)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn create-document!
  "Indexes the document."
  [settings collection-name document]
  (try+
   (let [{:keys [uri req]} (api/create-document-req settings collection-name document)]
     (util/http-response-json->hash-map (http/post uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn upsert-document!
  "Indexes the document."
  [settings collection-name document]
  (try+
   (let [{:keys [uri req]} (api/upsert-document-req settings collection-name document)]
     (util/http-response-json->hash-map (http/post uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn retrieve-document
  "Retrieves the document on id in the specified collection."
  [settings collection-name id]
  (try+
   (let [{:keys [uri req]} (api/retrieve-document-req settings collection-name id)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn delete-document!
  "Deletes the document on id in the specified collection."
  [settings collection-name id]
  (try+
   (let [{:keys [uri req]} (api/delete-document-req settings collection-name id)]
     (util/http-response-json->hash-map (http/delete uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn update-document!
  "Update an individual document from a collection by using its id.
  The update can be partial"
  [settings collection-name id document]
  (try+
   (let [{:keys [uri req]} (api/update-document-req settings collection-name id document)]
     (util/http-response-json->hash-map (http/patch uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn import-documents!
  "Imports documents in the specified collection."
  ([settings collection-name documents]
   (import-documents! settings collection-name documents {}))
  ([settings collection-name documents options]
   (try+
    (let [{:keys [uri req]} (api/import-documents-req settings collection-name documents options)]
      (util/http-response-jsonline->hash-maps (http/post uri req)))
    (catch [:type :clj-http.client/unexceptional-status] e
      (throw (http-ex-data->typesense-ex-info e))))))

(defn delete-documents!
  "Delete documents."
  [settings collection-name options]
  (try+
   (let [{:keys [uri req]} (api/delete-documents-req settings collection-name options)]
     (util/http-response-json->hash-map (http/delete uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn export-documents
  "Exports documents in the specified collection."
  [settings collection-name options]
  (try+
   (let [{:keys [uri req]} (api/export-documents-req settings collection-name options)]
     (util/http-response-jsonline->hash-maps (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn search
  "Search for documents using the specified query options."
  [settings collection-name options]
  (try+
   (let [{:keys [uri req]} (api/search-req settings collection-name options)]
     (util/http-response-jsonline->hash-maps (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn create-api-key!
  "Create api-key."
  [settings options]
  (try+
   (let [{:keys [uri req]} (api/create-api-key-req settings options)]
     (util/http-response-json->hash-map (http/post uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn retrieve-api-key
  "Retrives api-key on id."
  [settings id]
  (try+
   (let [{:keys [uri req]} (api/retrieve-api-key-req settings id)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn list-api-keys
  "List api-keys."
  [settings]
  (try+
   (let [{:keys [uri req]} (api/list-api-keys-req settings)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn delete-api-key!
  "Deletes api-key on id."
  [settings id]
  (try+
   (let [{:keys [uri req]} (api/delete-api-key-req settings id)]
     (util/http-response-json->hash-map (http/delete uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn upsert-override!
  "Upsert override."
  [settings collection-name override-name override]
  (try+
   (let [{:keys [uri req]} (api/upsert-override-req settings collection-name override-name override)]
     (util/http-response-json->hash-map (http/put uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn list-overrides
  "Lists overrides."
  [settings collection-name]
  (try+
   (let [{:keys [uri req]} (api/list-overrides-req settings collection-name)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn retrieve-override
  "Retrieve override on name."
  [settings collection-name override-name]
  (try+
   (let [{:keys [uri req]} (api/retrieve-override-req settings collection-name override-name)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn delete-override!
  "Delete override on name."
  [settings collection-name override-name]
  (try+
   (let [{:keys [uri req]} (api/delete-override-req settings collection-name override-name)]
     (util/http-response-json->hash-map (http/delete uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn upsert-alias!
  "Upsert alias."
  [settings collection-name alias-collection]
  (try+
   (let [{:keys [uri req]} (api/upsert-alias-req settings collection-name alias-collection)]
     (util/http-response-json->hash-map (http/put uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn list-aliases
  "List aliases."
  [settings]
  (try+
   (let [{:keys [uri req]} (api/list-aliases-req settings)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn retrieve-alias
  "Retrieves alias on collection-name."
  [settings collection-name]
  (try+
   (let [{:keys [uri req]} (api/retrieve-alias-req settings collection-name)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn delete-alias!
  "Delete alias on collection-name"
  [settings collection-name]
  (try+
   (let [{:keys [uri req]} (api/delete-alias-req settings collection-name)]
     (util/http-response-json->hash-map (http/delete uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn upsert-synonym!
  "Upsert synonym."
  [settings collection-name synonym-name synonyms]
  (try+
   (let [{:keys [uri req]} (api/upsert-synonym-req settings collection-name synonym-name synonyms)]
     (util/http-response-json->hash-map (http/put uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn retrieve-synonym
  "Retrieve synonym on synonym-name in collection."
  [settings collection-name synonym-name]
  (try+
   (let [{:keys [uri req]} (api/retrieve-synonym-req settings collection-name synonym-name)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn list-synonyms
  "List synonyms in collection"
  [settings collection-name]
  (try+
   (let [{:keys [uri req]} (api/list-synonyms-req settings collection-name)]
     (util/http-response-json->hash-map (http/get uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))

(defn delete-synonym!
  "Delete synonym on synonym-name in collection."
  [settings collection-name synonym-name]
  (try+
   (let [{:keys [uri req]} (api/delete-synonym-req settings collection-name synonym-name)]
     (util/http-response-json->hash-map (http/delete uri req)))
   (catch [:type :clj-http.client/unexceptional-status] e
     (throw (http-ex-data->typesense-ex-info e)))))
