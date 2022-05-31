(ns typesense.util
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [java.net URLEncoder]))

(defn- pair->url-parameter
  "Takes a pair and converts it into a url parameter."
  [[name value]]
  (str name "=" (URLEncoder/encode (str value))))

(defn map->url-parameter-string
  "Convert param pairs into a query string.
  If query-params are empty returns empty string."
  [query-params]
  (if (seq query-params)
    (->> (walk/stringify-keys query-params)
         (map pair->url-parameter)
         (str/join "&")
         (str "?"))
    ""))

(defn- map->json-newline
  "Takes a map and return a json-newline."
  [m]
  (str (json/write-str m) "\n"))

(defn maps->json-lines
  "Take a vector of maps and returns json-line format.
  Returns an empty string if the vector is empty."
  [ms]
  (str/join (map map->json-newline ms)))

(defn json->map
  "Transforms json to a map.
  Return nil if the json string is nil or blank."
  [json]
  (when-not (str/blank? json)
    (json/read-str json :key-fn keyword)))

(defn json-lines->maps
  "Transforms json-lines into a vector of maps.
  Returns empty vector if json-lines are nil or blank."
  [json-lines]
  (if (str/blank? json-lines)
    []
    (into [] (map json->map) (str/split-lines json-lines))))

(defn http-response-json->map
  "Transforms HTTP response json to a map.
  Returns nil if HTTP response body is nil or a blank string."
  [{:keys [body]}]
  (json->map body))

(defn http-response-jsonline->maps
  "Transforms HTTP response jsonline to vector of maps.
  Returns empty vector if HTTP response body is nil or a blank string."
  [{:keys [body]}]
  (json-lines->maps body))
