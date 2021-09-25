# Clojure client for Typesense

The current target support is

Still under development - not ready for use.

# Settings

Two values are currently required for settings.

* `Uri` the base-uri for Typesense, an example is "https://localhost:8108".
* `Key` the api-key required for the header `X-TYPESENSE-API-KEY`.

Example of configs:

```clojure
(def settings {:uri "http://localhost:8108" :key "my-super-secret-api-key"})
```

# Collection

This section describes how to use the collection part of Typesense-clj.

Further documentation regarding the query-parameters can be found [here.](https://typesense.org/docs/0.21.0/api/collections.html#create-a-collection)

## Create collection

The different `types` for the schema can be found [here](https://typesense.org/docs/0.21.0/api/collections.html#create-a-collection).

The examples displays the creation of collection named `companies`.

```clojure
(create-collection settings {:name "companies"
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
(drop-collection settings "companies")
```

## List collections

Returns a summary of all your collections. The collections are returned sorted by creation date, with the most recent collections appearing first.

```clojure
(list-collections settings)
```

## Retrive collection

Retrieves the collection on the `collection-name`.

```clojure
(retrieve-collection settings "companies")
```

# Documents

This section describes how to use the document part of Typesense-clj.

Further documentation regarding the query-parameters can be found [here.](https://typesense.org/docs/0.21.0/api/documents.html)

## Create document

Creates the document in a given collection. The document should comply with the `schema` of the collection.

```clojure
(create-document settings "companies" {:company_name "Stark Industries
                                       :num_employees 5215
                                       :country "USA""})
```

## Upsert document

Upserts the document in a given collection. The document will either be created or updated depending on if it already exists.

```clojure
(upsert-document settings "companies" {:company_name "Stark Industries
                                       :num_employees 5215
                                       :country "USA""})
```

## Retrieve document

Retrives document in a collection on `id`.

```clojure
(upsert-document settings "companies" 1)
```

## Delete document

Deletes document in a collection on `id`.

```clojure
(delete-document settings "companies" 1)
```

## Update document

Update document in a collection on id. The update can be partial.

```clojure
(update-document settings "companies" {:company_name "Stark innovation"} 1)
```

## Import documents

Create/upsert/update multiple documents in a collection.

All of the examples has an optional `:bulk_size` that can be set in the `parameters` map, the default is `40`.

### Create

Create with default `:bulk_size` of `40`.

```clojure
(import-documents settings
                  "companies"
                  [{:company_name "Innovationsoft A/S"
                    :num_employees 10
                    :country "Finland"}
                   {:company_name "GoSoftware"
                    :num_employees 5000
                    :country Sweden}])
```

Create with bulk size set to 100.

```clojure
(import-documents settings
                  "companies"
                  [{:company_name "Innovationsoft A/S"
                    :num_employees 10
                    :country "Finland"}
                   {:company_name "GoSoftware"
                    :num_employees 5000
                    :country Sweden}]
                  {:bulk_size 100})
```


### Upsert

```clojure
(import-documents settings
                  "companies"
                  [{:company_name "Innovationsoft A/S"
                    :num_employees 10
                    :country "Finland"}
                   {:company_name "GoSoftware"
                    :num_employees 5000
                    :country Sweden}]
                  {:action "upsert"})
```

### Update

```clojure
(import-documents settings
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

Delete multiple documents on filter, more info can be found [here.](https://typesense.org/docs/0.21.0/api/documents.html#delete-documents)

```clojure
(delete-documents settings
                  "companies"
                  {:filter_by "num_employees:>=100"})
```

## Export documents

Export multiple documents more, more info can be found [here.](https://typesense.org/docs/0.21.0/api/documents.html#export-documents)

```clojure
(delete-documents settings
                  "companies"
                  {:filter_by "num_employees:>=100"})
```
