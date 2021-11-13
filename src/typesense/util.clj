(ns typesense.util
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [java.net URLEncoder]))

(defn build-query
  "Convert param pairs into a query string.
  If query-params are empty returns empty string."
  [query-params]
  (if (seq query-params)
    (->> (walk/stringify-keys query-params)
         (map #(str (key %) "=" (URLEncoder/encode (str (val %)))))
         (str/join \&)
         (str "?"))
    ""))

(defn hash-maps->json-lines
  "Take a vector of maps and returns json-line format.
  Returns an empty string if the vector is empty."
  [ms]
  (reduce #(str %1 (json/write-str %2) "\n") "" ms))

(defn json->hash-map
  "Transforms json to hash map.
  Return nil if the json string is nil or blank."
  [json]
  (when-not (str/blank? json)
    (json/read-str json :key-fn keyword)))

(defn json-lines->hash-maps
  "Transforms json-lines to vector of maps.
  Returns empty vector if json-lines are nil or blank."
  [json-lines]
  (if (str/blank? json-lines)
    []
    (into [] (map json->hash-map) (str/split-lines json-lines))))

(defn http-response-json->hash-map
  "Transforms HTTP response json to hash-map.
  Returns nil if HTTP response body is nil or a blank string."
  [{:keys [body]}]
  (json->hash-map body))

(defn http-response-jsonline->hash-maps
  "Transforms HTTP response jsonline to vector of hash-maps.
  Returns empty vector if HTTP response body is nil or a blank string."
  [{:keys [body]}]
  (json-lines->hash-maps body))
