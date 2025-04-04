# Clojure client for Typesense

[![Clojars Project](https://img.shields.io/clojars/v/io.github.runeanielsen/typesense-clj.svg)](https://clojars.org/io.github.runeanielsen/typesense-clj)

Clojure client for [Typesense 28.0](https://github.com/typesense/typesense)

All of the examples uses the `typesense.client` namespace. The examples shows the simplest way to get started using the client, but all parameters described on Typesense API documentation should work, if that is not the case, please make a pull-request or open an issue.

The values shown in the example might not be 100% up to date with the current Typesense version, please go to the Typesense documentation to be sure of the return values.

## Settings

Two values are currently required for settings.

* `Uri` the base-uri for Typesense, an example is "https://localhost:8108".
* `Key` the api-key required for the header `X-TYPESENSE-API-KEY`.

Example of settings.

```clojure
(def settings {:uri "http://localhost:8108"
               :key "my-super-secret-api-key"})
```

## Collection

This section describes how to use the collection, further information can be found [here.](https://typesense.org/docs/28.0/api/collections.html#create-a-collection)

### Create collection

The different `types` for the schema can be found [here](https://typesense.org/docs/28.0/api/collections.html#create-a-collection).

The examples displays the creation of collection named `companies`.

```clojure
(create-collection!
 settings
 {:name "companies"
  :fields [{:name "company_name"
            :type "string"}
           {:name "num_employees"
            :type "int32"}
           {:name "country"
            :type "string"
            :facet true}]
  :default_sorting_field "num_employees"})

;; Example success response =>
{:default_sorting_field "num_employees"
 :enable_nested_fields false
 :fields [{:facet false
           :index true
           :name "company_name"
           :optional false
           :stem false
           :type "string"
           :infix false
           :locale ""
           :sort false}
          {:facet false
           :index true
           :name "num_employees"
           :optional false
           :stem false
           :type "int32"
           :infix false
           :locale ""
           :sort true}
          {:facet true
           :index true
           :name "country"
           :optional false
           :stem false
           :type "string"
           :infix false
           :locale ""
           :sort false}]
 :name "companies_collection_test"
 :num_documents 0
 :symbols_to_index []
 :token_separators []}
```

### Update collection

The different `types` for the schema can be found [here](https://typesense.org/docs/28.0/api/collections.html#update-or-alter-a-collection).

The examples shows updating the collection named named `companies` with a new field `year_founded`.

```clojure
(update-collection!
 settings
 "companies"
 {:fields [{:name "year_founded"
            :type "int32"
            :optional true}]})

;; Example success response =>
{:fields [{:facet false,
             :index true,
             :infix false,
             :locale "",
             :name "year_founded",
             :nested false,
             :nested_array 0,
             :num_dim 0,
             :optional true,
             :sort true,
             :stem false
             :type "int32",
             :vec_dist "cosine",
             +:embed nil,
             +:reference ""}]}
```

### Delete collection

Permanently drops a collection on the `collection-name`. This action cannot be undone.
For large collections, this might have an impact on read latencies.

```clojure
(delete-collection! settings "companies")

;; Example success response =>
{:created_at 1647261230
 :enable_nested_fields false
 :default_sorting_field "num_employees"
 :fields
 [{:facet false
   :index true
   :name "company_name"
   :optional false
   :stem false
   :type "string"}
  {:facet false
   :index true
   :name "num_employees"
   :optional false
   :stem false
   :type "int32"}
  {:facet true
   :index true
   :name "country"
   :stem false
   :optional false
   :type "string"}]
 :name "companies"
 :num_documents 0
 :symbols_to_index []
 :token_separators []}
```

### List collections

Returns a summary of all your collections. The collections are returned sorted by creation date, with the most recent collections appearing first.

```clojure
(list-collections settings)

;; Example success response =>
[{:default_sorting_field "num_employees"
  :enable_nested_fields false
  :fields [{:facet false
            :index true
            :name "company_name"
            :optional false
            :type "string"
            :infix false
            :locale ""
            :sort false}
           {:facet false
            :index true
            :name "num_employees"
            :optional false
            :type "int32"
            :infix false
            :locale ""
            :sort true}
           {:facet true
            :index true
            :name "country"
            :optional false
            :type "string"
            :infix false
            :locale ""
            :sort false}]
  :name "companies_collection_test"
  :num_documents 0
  :symbols_to_index []
  :token_separators []}]
```

### Retrieve collection

Retrieves the collection on the `collection-name`.

```clojure
(retrieve-collection settings "companies")

;; Example success response =>
{:default_sorting_field "num_employees"
 :enable_nested_fields false
 :fields [{:facet false
           :index true
           :infix false
           :locale ""
           :name "company_name"
           :optional false
           :sort false
           :type "string"}
          {:facet false
           :index true
           :infix false
           :locale ""
           :name "num_employees"
           :optional false
           :sort true
           :type "int32"}
          {:facet true
           :index true
           :infix false
           :locale ""
           :name "country"
           :optional false
           :sort false
           :type "string"}]
 :name "companies_collection_test"
 :num_documents 0
 :symbols_to_index []
 :token_separators []}
```

## Documents

This section describes how to use the documents, further information can be found [here.](https://typesense.org/docs/28.0/api/documents.html)

### Create document

Creates the document in a given collection. The document should comply with the `schema` of the collection.

```clojure
(create-document! settings "companies" {:company_name "Stark Industries"
                                        :num_employees 5215
                                        :country "USA"})

;; Example success response =>
{:company_name "Stark Industries"
 :country "USA"
 :id "0"
 :num_employees 5215}
 ```

### Upsert document

Upsert the document in a given collection. The document will either be created or updated depending on if it already exists.

```clojure
(upsert-document! settings "companies" {:company_name "Awesome Inc."
                                        :num_employees 10
                                        :country "Norway"})

;; Example success response =>
{:company_name "Awesome Inc."
 :num_employees 10
 :country "Norway"
 :id "1"}
```

### Retrieve document

Retrieves document in a collection on `id`. The `id` can be parsed in as `int` or `string`.

```clojure
(retrieve-document settings "companies" 1)

;; Example success response =>
{:company_name "Awesome Inc."
 :num_employees 10
 :country "Norway"
 :id "1"}
```

### Delete document

Deletes document in a collection on `id`. The `id` can be parsed in as `int` or `string`.

```clojure
(delete-document! settings "companies" 1)

;; Example success response =>
{:company_name "Stark Industries"
 :country "USA"
 :id "0"
 :num_employees 5215}
```

### Update document

Update document in a collection on id. The update can be partial.

```clojure
(update-document! settings "companies" {:company_name "Mega Awesome Inc."} 1)

;; Example success response =>
{:company_name "Mega Awesome Inc."
 :num_employees 10
 :country "Norway"
 :id "1"}
```

## Create/Upsert/Update/Delete Documents

Create/upsert/update documents. All of them takes optional parameters, an example is setting the batch size using `:batch_size 20`. Read more [here.](https://typesense.org/docs/28.0/api/documents.html#import-documents)

### Create documents

```clojure
(create-documents! settings
                   "companies"
                   [{:company_name "Innovationsoft A/S"
                     :num_employees 10
                     :country "Finland"}
                    {:company_name "GoSoftware"
                     :num_employees 5000
                     :country "Sweden"}])

;; Example success response =>
[{:success true} {:success true}]
```

### Upsert documents

```clojure
(upsert-documents! settings
                   "companies"
                   [{:company_name "Innovationsoft A/S"
                     :num_employees 10
                     :country "Finland"}
                    {:company_name "GoSoftware"
                     :num_employees 5000
                     :country "Sweden"}])

;; Example success response =>
[{:success true} {:success true}]
```

### Update documents

```clojure
(update-documents! settings
                   "companies"
                   [{:id "1"
                     :company_name "Innovationsoft A/S"
                     :num_employees 10
                     :country "Finland"}
                    {:id "2"
                     :company_name "GoSoftware"
                     :num_employees 5000
                     :country "Sweden"}])

;; Example success response =>
[{:success true} {:success true}]
```

### Delete documents

Delete multiple documents on filter.

```clojure
(delete-documents! settings "companies" {:filter_by "num_employees:>=100"})

;; Example success response =>
{:num_deleted 2}
```

### Export documents

Export documents in collection.

```clojure
(export-documents settings "companies" {:filter_by "num_employees:>=100"})

;; Example success response =>
[{:id "1"
  :company_name "Innovationsoft A/S"
  :num_employees 10
  :country "Finland"}]
```

## Search

Search for documents in a collection. You can find all the query arguments [here.](https://typesense.org/docs/28.0/api/documents.html#arguments)

```clojure
(search settings "companies" {:q "Innovation"
                              :query_by "company_name"})

;; Example success response =>
 {:facet_counts []
 :found 1
 :hits
 [{:document
   {:company_name "Innovationsoft A/S"
    :country "Finland"
    :id "1"
    :num_employees 10}
   :highlight
   {:company_name
    {:matched_tokens ["Innovation"]
     :snippet "<mark>Innovation</mark>soft A/S"}}
   :highlights
   [{:field "company_name"
     :matched_tokens ["Innovation"]
     :snippet "<mark>Innovation</mark>soft A/S"}]
   :text_match 578730089005449337
   :text_match_info
   {:best_field_score "1108074561536"
    :best_field_weight 15
    :fields_matched 1
    :score "578730089005449337"
    :tokens_matched 1}}]
 :out_of 1
 :page 1
 :request_params
 {:collection_name "companies_documents_test"
  :per_page 10
  :q "Innovation"}
 :search_cutoff false
 :search_time_ms 0}
```

## Multi search

You can send multiple search requests in a single HTTP request, using the Multi-Search feature. This is especially useful to avoid round-trip network latencies incurred otherwise if each of these requests are sent in separate HTTP requests. You can read more about multi-search [here.](https://typesense.org/docs/28.0/api/documents.html#federated-multi-search)

```clojure
(multi-search
 settings
 {:searches [{:collection "products"
              :q "shoe"
              :filter_by "price:=[50..120]"}
             {:collection "brands"
              :q "Nike"}]}
 {:query_by "name"})

;; Example success response =>
{:results
 [{:facet_counts []
   :found 1
   :hits
   [{:document {:id "1" :name "shoe" :price 75}
     :highlight
     {:name {:matched_tokens ["shoe"] :snippet "<mark>shoe</mark>"}}
     :highlights
     [{:field "name"
       :matched_tokens ["shoe"]
       :snippet "<mark>shoe</mark>"}]
     :text_match 578730123365711993
     :text_match_info
     {:best_field_score "1108091339008"
      :best_field_weight 15
      :fields_matched 1
      :score "578730123365711993"
      :tokens_matched 1}}]
   :out_of 1
   :page 1
   :request_params
   {:collection_name "products_multi_search_test"
    :per_page 10
    :q "shoe"}
   :search_cutoff false
   :search_time_ms 0}
  {:facet_counts []
   :found 1
   :hits
   [{:document {:id "1" :name "Nike"}
     :highlight
     {:name {:matched_tokens ["Nike"] :snippet "<mark>Nike</mark>"}}
     :highlights
     [{:field "name"
       :matched_tokens ["Nike"]
       :snippet "<mark>Nike</mark>"}]
     :text_match 578730123365711993
     :text_match_info
     {:best_field_score "1108091339008"
      :best_field_weight 15
      :fields_matched 1
      :score "578730123365711993"
      :tokens_matched 1}}]
   :out_of 1
   :page 1
   :request_params
   {:collection_name "brands_multi_search_test"
    :per_page 10
    :q "Nike"}
   :search_cutoff false
   :search_time_ms 0}]}
```

## Geosearch

```clojure
;; Create collection for geosearch with document.
(let [schema {:name "places"
              :fields [{:name "title" :type "string"}
                       {:name "points" :type "int32"}
                       {:name "location" :type "geopoint"}]
              :default_sorting_field "points"}
      document {:points 1
                :title "Louvre Museuem"
                :location [48.86093481609114 2.33698396872901]}]
  (create-collection! settings schema)
  (create-document! settings "places" document))

;; Search
(search settings
        "places"
        {:q "*"
         :query_by "title"
         :filter_by "location:(48.90615915923891 2.3435897727061175 5.1 km)"
         :sort_by "location(48.853 2.344):asc"})

;; Example success response =>
{:facet_counts []
 :found 1
 :hits
 [{:document
   {:id "0"
    :location [48.86093481609114 2.33698396872901]
    :points 1
    :title "Louvre Museuem"}
   :geo_distance_meters {:location 1020}
   :highlight {}
   :highlights []}]
 :out_of 1
 :page 1
 :request_params {:collection_name "places" :per_page 10 :q "*"}
 :search_cutoff false}
```

## Api keys

Typesense allows you to create API Keys with fine-grain access control. You can restrict access on both a per-collection and per-action level, [read more here](https://typesense.org/docs/28.0/api/api-keys.html#create-an-api-key)

### Create api key

```clojure
(create-api-key! settings {:description "Search only companies key."
                           :actions ["document:search"]
                           :collections ["companies"]})

;; Example response =>
{:actions ["document:search"]
 :collections ["companies"]
 :description "Search only companies key."
 :expires_at 64723363199
 :autodelete false
 :id 0
 :value "sK0jo6CSn1EBoJJ8LKPjRZCtsJ1JCFkt"}
```

### Retrieve api key

Retrieves api key on `id`.

```clojure
(retrieve-api-key settings 0)

;; Example response =>
{:actions ["document:search"]
 :collections ["companies"]
 :description "Search only companies key."
 :expires_at 64723363199
 :autodelete false
 :id 0
 :value_prefix "vLbB"}
```

### List api keys

List all api keys.

```clojure
(list-api-keys settings)

;; Example response =>
{:keys [{:actions ["document:search"]
         :collections ["companies"]
         :description "Search only companies key."
         :expires_at 64723363199
         :autodelete false
         :id 0
         :value_prefix "vLbB"}]}
```

### Delete api key

Deletes api key on `id`.

```clojure
(delete-api-key! settings 0)

;; Example success response =>
{:id 0}
```

## Curation

Using overrides, you can include or exclude specific documents for a given query, read more [here.](https://typesense.org/docs/28.0/api/curation.html)

### Create or update an override

Create or update override if already exist.

```clojure
(upsert-override! settings
                  "companies"
                  "customize-apple"
                  {:rule {:query "apple"
                          :match "exact"}
                   :includes [{:id "422" :position 1}
                              {:id "54" :position 2}]
                   :excludes [{:id "287"}]})

;; Examples success response =>
{:excludes [{:id "287"}]
 :id "customize_apple"
 :includes [{:id "422" :position 1} {:id "54" :position 2}]
 :rule {:match "exact" :query "apple"}}
```

### List overrides

List all overrides.

```clojure
(list-overrides settings "companies")

;; Example success response =>
{:overrides
 [{:excludes [{:id "287"}]
   :filter_curated_hits false
   :id "customize_apple"
   :includes [{:id "422" :position 1} {:id "54" :position 2}]
   :remove_matched_tokens false
   :rule {:match "exact" :query "apple"}
   :stop_processing true}]}
```

### Retrieve override

Retrieves override on name.

```clojure
(retrieve-override settings "companies" "customize-apple")

;; Example success response =>
{:excludes [{:id "287"}]
 :filter_curated_hits false
 :id "customize_apple"
 :includes [{:id "422" :position 1} {:id "54" :position 2}]
 :remove_matched_tokens false
 :rule {:match "exact" :query "apple"}
 :stop_processing true}
```

### Delete override

Deletes override on name.

```clojure
(delete-override! settings "companies" "customize-apple")

;; Example success response =>
{:id "customize_apple"}
```

## Collection alias

An alias is a virtual collection name that points to a real collection. Read more [here](https://typesense.org/docs/28.0/api/collection-alias.html)

### Create or update alias

Create or update alias.

```clojure
(upsert-alias! settings "companies" {:collection_name "companies_june11"})

;; Example success response =>
{:collection_name "companies_june11" :name "companies"}
```

### Retrieve alias

Retrieve alias on collection-name.

```clojure
(retrieve-alias settings "companies")

;; Example success response =>
{:collection_name "companies_alias_test" :name "companies"}
```

### List aliases

List aliases.

```clojure
(list-aliases settings)

;; Example success response =>
{:aliases [{:collection_name "companies_alias_test" :name "companies"}]}
```

### Delete alias

Delete alias on collection name.

```clojure
(delete-alias! settings "companies")

;; Example success response =>
{:collection_name "companies_alias_test" :name "companies"}
```

## Synonyms

The synonyms feature allows you to define search terms that should be considered equivalent, read more [here.](https://typesense.org/docs/28.0/api/synonyms.html)

### Create or update synonym

Create or update synonym.

```clojure
(upsert-synonym! settings "products" "coat-synonyms" {:synonyms ["blazer" "coat" "jacket"]})

;; Example success response =>
{:id "coat-synonyms" :synonyms ["blazer" "coat" "jacket"]}
```

### Retrieve synonym

Retrieve synonym on synonym name in collection.

```clojure
(retrieve-synonym settings "products" "coat-synonyms")

;; Example success response =>
{:id "coat-synonyms" :root "" :synonyms ["blazer" "coat" "jacket"]}
```

### List synonyms

List synonyms in collection.

```clojure
(list-synonyms settings "products")

;; Example success response =>
{:synonyms [{:id "coat-synonyms" :root "" :synonyms ["blazer" "coat" "jacket"]}]}
```

### Delete synonym

Delete synonym on synonym-name in collection.

```clojure
(delete-synonym! settings "products" "coat-synonyms")

;; Example success response =>
{:id "coat-synonyms"}
```

## Cluster operations

### Health

Get health information about a Typesense node.

```clojure
(health settings)

;; Example success response =>
{:ok true}
```

### Metrics

Get current RAM, CPU, Disk & Network usage metrics.

```clojure
(metrics settings)

;; Example success response =>
{:system_cpu8_active_percentage "0.00",
 :system_cpu12_active_percentage "9.09",
 :typesense_memory_allocated_bytes "87053184",
 :system_cpu5_active_percentage "9.09",
 :system_network_sent_bytes "475775",
 :system_cpu3_active_percentage "0.00",
 :system_cpu9_active_percentage "0.00",
 :typesense_memory_resident_bytes "97734656",
 :system_cpu_active_percentage "3.77",
 :system_memory_used_bytes "5583503360",
 :system_cpu14_active_percentage "9.09",
 :system_cpu15_active_percentage "0.00",
 :system_cpu6_active_percentage "0.00",
 :system_cpu10_active_percentage "10.00",
 :system_network_received_bytes "585752",
 :system_cpu13_active_percentage "0.00",
 :system_cpu11_active_percentage "0.00",
 :system_disk_total_bytes "16782462976",
 :typesense_memory_metadata_bytes "28598544",
 :system_cpu4_active_percentage "10.00",
 :system_cpu16_active_percentage "0.00",
 :typesense_memory_fragmentation_ratio "0.11",
 :system_disk_used_bytes "24072192",
 :system_memory_total_bytes "33564925952",
 :typesense_memory_mapped_bytes "255479808",
 :system_cpu2_active_percentage "18.18",
 :system_cpu1_active_percentage "9.09",
 :typesense_memory_retained_bytes "80064512",
 :system_cpu7_active_percentage "0.00",
 :typesense_memory_active_bytes "97734656"}
```

### Stats

Get stats about API endpoints.
Returns average requests per second and latencies for all requests in the last 10 seconds.

```clojure
(stats settings)

;; Example success response =>
{:import_latency_ms 0
 :write_requests_per_second 0
 :import_requests_per_second 0
 :write_latency_ms 0
 :latency_ms {}
 :pending_write_batches 0
 :search_requests_per_second 0
 :delete_requests_per_second 0
 :search_latency_ms 0
 :requests_per_second {}
 :total_requests_per_second 0.0
 :overloaded_requests_per_second 0
 :delete_latency_ms 0}
 ```

## Exceptions

### Typesense API Errors

Typesense API exceptions in the [Typesense-api-errors](https://typesense.org/docs/28.0/api/api-errors.html) spec.

| Type                                      | Description                                                                |
|:------------------------------------------|:---------------------------------------------------------------------------|
| `:typesense.client/bad-request`           | Bad Request - The request could not be understood due to malformed syntax. |
| `:typesense.client/unauthorized`          | Unauthorized - Your API key is wrong.                                      |
| `:typesense.client/not-found`             | Not Found - The requested resource is not found.                           |
| `:typesense.client/conflict`              | Conflict - When a resource already exists.                                 |
| `:typesense.client/unprocessable-entity`  | Unprocessable Entity - Request is well-formed, but cannot be processed.    |
| `:typesense.client/service-unavailable`   | Service Unavailable - We’re temporarily offline. Please try again later.   |
| `:typesense.client/unspecified-api-error` | If Typesense throws an error that is not specified in the spec.            |


## Development

### Tests

#### Run unit tests

The following command runs only unit tests.

```sh
bin/kaocha unit
```

#### Run integration tests

To run the integration tests you can run a local docker instance with the following command. This will start a instance of Typesense on `localhost:8108`. The Typesense instance will be cleaned before starting the integration tests.

```sh
docker run -p 8108:8108 -v/tmp/data:/data typesense/typesense:28.0 --data-dir /data --api-key=key
```

The following command runs only the integration tests.

```sh
bin/kaocha integration
```

#### Run all the tests.

The following command runs all tests.

```sh
bin/kaocha
```
