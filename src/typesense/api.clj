(ns typesense.api
  (:require [typesense.util :as util]
            [clojure.data.json :as json]))

(def ^:private api-key-header-name "X-TYPESENSE-API-KEY")

(defn- collection-uri
  "Returns the full collection uri resource path."
  ([uri]
   (str uri "/collections"))
  ([uri collection-name]
   (str (collection-uri uri) "/" collection-name)))

(defn- document-uri
  "Returns the full document uri resource path."
  ([uri collection-name]
   (str uri "/collections/" collection-name "/documents"))
  ([uri collection-name id]
   (str (document-uri uri collection-name) "/" id)))

(defn- keys-uri
  "Return the full keys uri resource path."
  ([uri]
   (str uri "/keys"))
  ([uri id]
   (str (keys-uri uri) "/" id)))

(defn- overrides-uri
  "Returns the full overrides uri resource path."
  ([uri collection-name]
   (str uri "/collections/" collection-name "/overrides"))
  ([uri collection-name override-name]
   (str (overrides-uri uri collection-name) "/" override-name)))

(defn- aliases-uri
  "Returns the full alias uri resource path."
  ([uri]
   (str uri "/aliases"))
  ([uri collection-name]
   (str uri "/aliases/" collection-name)))

(defn- synonyms-uri
  "Returns the full synonyms uri resource path."
  ([uri collection-name]
   (str uri "/collections/" collection-name "/synonyms"))
  ([uri collection-name synonym-name]
   (str (synonyms-uri uri collection-name) "/" synonym-name)))

(defn- multi-search-uri
  "Returns the full multi-search uri resource path."
  [uri common-search-params opt-query-params]
  (let [query-parameter-map (merge common-search-params opt-query-params)]
    (str uri "/multi_search" (util/build-query query-parameter-map))))

(defn create-collection-req
  [{:keys [uri key]} schema]
  {:uri (collection-uri uri)
   :req {:headers {api-key-header-name key
                   "Content-Type" "application/json"}
         :body (json/write-str schema)}})

(defn drop-collection-req
  [{:keys [uri key]} collection-name]
  {:uri (collection-uri uri collection-name)
   :req {:headers {api-key-header-name key}}})

(defn list-collections-req
  [{:keys [uri key]}]
  {:uri (collection-uri uri)
   :req {:headers {api-key-header-name key}}})

(defn retrieve-collection-req
  [{:keys [uri key]} collection-name]
  {:uri (collection-uri uri collection-name)
   :req {:headers {api-key-header-name key}}})

(defn create-document-req
  [{:keys [uri key]} collection-name document]
  {:uri (document-uri uri collection-name)
   :req {:headers {api-key-header-name key
                   "Content-Type" "application/json"}
         :body (json/write-str document)}})

(defn upsert-document-req
  [{:keys [uri key]} collection-name document]
  {:uri (str (document-uri uri collection-name) "?action=upsert")
   :req {:headers {api-key-header-name key
                   "Content-Type" "application/json"}
         :body (json/write-str document)}})

(defn retrieve-document-req
  [{:keys [uri key]} collection-name id]
  {:uri (document-uri uri collection-name id)
   :req {:headers {api-key-header-name key}}})

(defn delete-document-req
  [{:keys [uri key]} collection-name id]
  {:uri (document-uri uri collection-name id)
   :req {:headers {api-key-header-name key}}})

(defn update-document-req
  [{:keys [uri key]} collection-name id document]
  {:uri (document-uri uri collection-name id)
   :req {:headers {api-key-header-name key
                   "Content-Type" "application/json"}
         :body (json/write-str document)}})

(defn import-documents-req
  ([settings collection-name documents]
   (import-documents-req settings collection-name documents {}))
  ([{:keys [uri key]} collection-name documents options]
   {:uri (str (document-uri uri collection-name)
              "/import"
              (util/build-query options))
    :req {:headers {api-key-header-name key
                    "Content-Type" "text/plain"}
          :body (util/maps->json-lines documents)}}))

(defn delete-documents-req
  [{:keys [uri key]} collection-name options]
  {:uri (str (document-uri uri collection-name)
             (util/build-query options))
   :req {:headers {api-key-header-name key}}})

(defn export-documents-req
  [{:keys [uri key]} collection-name options]
  {:uri (str (document-uri uri collection-name)
             "/export"
             (util/build-query options))
   :req {:headers {api-key-header-name key}}})

(defn search-req
  [{:keys [uri key]} collection-name options]
  {:uri (str (document-uri uri collection-name)
             "/search"
             (util/build-query options))
   :req {:headers {api-key-header-name key}}})

(defn multi-search-req
  [{:keys [uri key]} search-reqs common-search-params opt-query-params]
  {:uri (multi-search-uri uri common-search-params opt-query-params)
   :req {:headers {api-key-header-name key
                   "Content-Type" "application/json"}
         :body (json/write-str search-reqs)}})

(defn create-api-key-req
  [{:keys [uri key]} options]
  {:uri (keys-uri uri)
   :req {:headers {api-key-header-name key}
         :body (json/write-str options)}})

(defn retrieve-api-key-req
  [{:keys [uri key]} id]
  {:uri (keys-uri uri id)
   :req {:headers {api-key-header-name key}}})

(defn list-api-keys-req
  [{:keys [uri key]}]
  {:uri (keys-uri uri)
   :req {:headers {api-key-header-name key}}})

(defn delete-api-key-req
  [{:keys [uri key]} id]
  {:uri (keys-uri uri id)
   :req {:headers {api-key-header-name key}}})

(defn upsert-override-req
  [{:keys [uri key]} collection-name override-name override]
  {:uri (overrides-uri uri collection-name override-name)
   :req {:headers {api-key-header-name key
                   "Content-Type" "text/json"}
         :body (json/write-str override)}})

(defn list-overrides-req
  [{:keys [uri key]} collection-name]
  {:uri (overrides-uri uri collection-name)
   :req {:headers {api-key-header-name key}}})

(defn retrieve-override-req
  [{:keys [uri key]} collection-name override-name]
  {:uri (overrides-uri uri collection-name override-name)
   :req {:headers {api-key-header-name key}}})

(defn delete-override-req
  [{:keys [uri key]} collection-name override-name]
  {:uri (overrides-uri uri collection-name override-name)
   :req {:headers {api-key-header-name key}}})

(defn upsert-alias-req
  [{:keys [uri key]} collection-name alias-collection]
  {:uri (aliases-uri uri collection-name)
   :req {:headers {api-key-header-name key
                   "Content-Type" "text/json"}
         :body (json/write-str alias-collection)}})

(defn retrieve-alias-req
  [{:keys [uri key]} collection-name]
  {:uri (aliases-uri uri collection-name)
   :req {:headers {api-key-header-name key}}})

(defn list-aliases-req
  [{:keys [uri key]}]
  {:uri (aliases-uri uri)
   :req {:headers {api-key-header-name key}}})

(defn delete-alias-req
  [{:keys [uri key]} collection-name]
  {:uri (aliases-uri uri collection-name)
   :req {:headers {api-key-header-name key}}})

(defn upsert-synonym-req
  [{:keys [uri key]} collection-name synonym-name synonyms]
  {:uri (synonyms-uri uri collection-name synonym-name)
   :req {:headers {api-key-header-name key
                   "Content-Type" "text/json"}
         :body (json/write-str synonyms)}})

(defn retrieve-synonym-req
  [{:keys [uri key]} collection-name synonym-name]
  {:uri (synonyms-uri uri collection-name synonym-name)
   :req {:headers {api-key-header-name key}}})

(defn list-synonyms-req
  [{:keys [uri key]} collection-name]
  {:uri (synonyms-uri uri collection-name)
   :req {:headers {api-key-header-name key}}})

(defn delete-synonym-req
  [{:keys [uri key]} collection-name synonym-name]
  {:uri (synonyms-uri uri collection-name synonym-name)
   :req {:headers {api-key-header-name key}}})
