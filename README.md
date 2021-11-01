# Clojure client for Typesense

**Still under development - API might change a bit in the future until finished**

Clojure client for [Typesense 0.21](https://github.com/typesense/typesense)

All of the examples uses the `typesense.client` namespace. The examples shows the simplest way to get started using the client, but all parameters described on Typesense API documentation should work, if that is not the case, please make a pull-request or open an issue.

# Settings

Two values are currently required for settings.

* `Uri` the base-uri for Typesense, an example is "https://localhost:8108".
* `Key` the api-key required for the header `X-TYPESENSE-API-KEY`.

Example of configs:

```clojure
(def settings {:uri "http://localhost:8108"
               :key "my-super-secret-api-key"})
```


# Collection

This section describes how to use the collection, further information can be found [here.](https://typesense.org/docs/0.21.0/api/collections.html#create-a-collection)

## Create collection

The different `types` for the schema can be found [here](https://typesense.org/docs/0.21.0/api/collections.html#create-a-collection).

The examples displays the creation of collection named `companies`.

```clojure
(create-collection! settings {:name "companies"
                              :fields [{:name "company_name"
                                        :type "string"}
                                       {:name "num_employees"
                                        :type "int32"}
                                       {:name "country"
                                        :type "string"
                                        :facet true}]
                              :default_sorting_field "num_employees"})
```

## Drop collection

Permanently drops a collection on the `collection-name`. This action cannot be undone.
For large collections, this might have an impact on read latencies.

```clojure
(drop-collection! settings "companies")
```

## List collections

Returns a summary of all your collections. The collections are returned sorted by creation date, with the most recent collections appearing first.

```clojure
(list-collections settings)
```

## Retrieve collection

Retrieves the collection on the `collection-name`.

```clojure
(retrieve-collection settings "companies")
```

# Documents

This section describes how to use the documents, further information can be found [here.](https://typesense.org/docs/0.21.0/api/documents.html)

## Create document

Creates the document in a given collection. The document should comply with the `schema` of the collection.

```clojure
(create-document! settings "companies" {:company_name "Stark Industries"
                                        :num_employees 5215 :country "USA""})
```

## Upsert document

Upserts the document in a given collection. The document will either be created or updated depending on if it already exists.

```clojure
(upsert-document! settings "companies" {:company_name "Stark Industries"
                                        :num_employees 5215
                                        :country "USA""})
```

## Retrieve document

Retrives document in a collection on `id`.

```clojure
(retrieve-document settings "companies" 1)
```

## Delete document

Deletes document in a collection on `id`.

```clojure
(delete-document! settings "companies" 1)
```

## Update document

Update document in a collection on id. The update can be partial.

```clojure
(update-document! settings "companies" {:company_name "Stark innovation"} 1)
```

# Import documents

Create/upsert/update documents.

## Import documents

```clojure
(import-documents! settings
                   "companies"
                   [{:company_name "Innovationsoft A/S"
                     :num_employees 10
                     :country "Finland"}
                    {:company_name "GoSoftware"
                     :num_employees 5000
                     :country Sweden}])
```

## Upsert documents

```clojure
(import-documents! settings
                   "companies"
                   [{:company_name "Innovationsoft A/S"
                     :num_employees 10
                     :country "Finland"}
                    {:company_name "GoSoftware"
                     :num_employees 5000
                     :country Sweden}]
                   {:action "upsert"})
```

## Update documents

```clojure
(import-documents! settings
                   "companies"
                   [{:company_name "Innovationsoft A/S"
                     :num_employees 10
                     :country "Finland"}
                    {:company_name "GoSoftware"
                     :num_employees 5000
                     :country Sweden}]
                   {:action "update"})
```

## Delete documents

Delete multiple documents on filter.

```clojure
(delete-documents! settings "companies" {:filter_by "num_employees:>=100"})
```

## Export documents

Export documents in collection.

```clojure
(export-documents settings "companies" {:filter_by "num_employees:>=100"})
```

## Search

Search for documents in a collection.

```clojure
(search settings "companies" {:q "Stark"
                              :query_by "test_name"})
```

# Api key

Typesense allows you to create API Keys with fine-grain access control. You can restrict access on both a per-collection and per-action level, [read more here](https://typesense.org/docs/0.21.0/api/api-keys.html#create-an-api-key)

## Create api key

```clojure
(create-api-key! settings {:description "Search only companies key."
                           :actions ["document:search"]
                           :collections ["companies"]})
```

## Retrieve api key

Retrieves api key on `id`.

```clojure
(retrieve-api-key settings 0)
```

## List api keys

List all api keys.

```clojure
(list-api-keys settings)
```

## Delete api key

Deletes api key on `id`.

```clojure
(delete-api-key! settings 0)
```

# Curation

Using overrides, you can include or exclude specific documents for a given query, read more [here.](https://typesense.org/docs/0.21.0/api/curation.html)

## Create or update an override

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
```

## List overrides

Lists all overrides.

```clojure
(list-overrides settings "companies")
```

## Retrieve override

Retrieves override on name.

```clojure
(retrieve-override settings "companies" "customize-apple")
```

## Delete override

Deletes override on name.

```clojure
(delete-override! settings "companies" "customize-apple")
```

# Collection alias

An alias is a virtual collection name that points to a real collection. Read more [here](https://typesense.org/docs/0.21.0/api/collection-alias.html)

## Create or update alias

Create or update alias.

```clojure
(upsert-alias! settings "companies" {:collection_name "companies_june11"})
```

## Retrieve alias

Retrieve alias on collection-name.

```clojure
(retrieve-alias settings "companies")
```

## List aliases

List aliases.

```clojure
(list-aliases settings)
```

## Delete alias

Delete alias on collection name.

```clojure
(delete-alias! settings "companies")
```

# Synonyms

The synonyms feature allows you to define search terms that should be considered equivalent, read more [here.](https://typesense.org/docs/0.21.0/api/synonyms.html)

## Create or update synonym

Create or update synonym.

```clojure
(upsert-synonym! settings "products" "coat-synonyms" {:synonyms ["blazer" "coat" "jacket"]})
```

## Retrieve synonym

Retrieve synonym on synonym name in collection.

```clojure
(retrieve-synonym settings "products" "coat-synonyms")
```

## List synonyms

List synonyms in collection.

```clojure
(list-synonyms settings "products")
```

## Delete synonym

Delete synonym on synonym-name in collection.

```clojure
(delete-synonym! settings "products" "coat-synonyms")
```

## Exceptions

### API Errors

Typesense API exceptions in the [Typesense-api-errors](https://typesense.org/docs/0.21.0/api/api-errors.html) spec.

| Type                                      | Description                                                                |
|:------------------------------------------|:---------------------------------------------------------------------------|
| `:typesense.client/bad-request`           | Bad Request - The request could not be understood due to malformed syntax. |
| `:typesense.client/unauthorized`          | Unauthorized - Your API key is wrong.                                      |
| `:typesense.client/not-found`             | Not Found - The requested resource is not found.                           |
| `:typesense.client/conflict`              | Conflict - When a resource already exists.                                 |
| `:typesense.client/unprocessable-entity`  | Unprocessable Entity - Request is well-formed, but cannot be processed.    |
| `:typesense.client/service-unavailable`   | Service Unavailable - We’re temporarily offline. Please try again later.   |
| `:typesense.client/unspecified-api-error` | If Typesense throws an error that is not specified in the spec.            |
