# Clojure client for Typesense

Still under development - not ready for use.

# Settings

Two values are currently required for settings.

* `Uri` the base-uri for Typesense, an example is "https://localhost:8108".
* `Key` the api-key required for the header `X-TYPESENSE-API-KEY`.

Example of configs:

```clojure
(def settings {:uri "http://localhost:8108" :key "key"})
```

# Collection

This section describes how to use the collection part of Typesense-clj.

Further documentation regarding the query-parameters can be found on [Typesense documentation on collections](https://typesense.org/docs/0.21.0/api/collections.html#create-a-collection)

## Create collection


The different `types` for the schema can be found [here](https://typesense.org/docs/0.21.0/api/collections.html#create-a-collection).

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
