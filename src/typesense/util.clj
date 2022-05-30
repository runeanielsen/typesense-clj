(ns typesense.util
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [java.net URLEncoder]))

(defn map->url-parameter-string
  "Convert param pairs into a query string.
  If query-params are empty returns empty string."
  [query-params]
  (if (seq query-params)
    (->> (walk/stringify-keys query-params)
         (map #(str (key %) "=" (URLEncoder/encode (str (val %)))))
         (str/join \&)
         (str "?"))
    ""))

(defn maps->json-lines
  "Take a vector of maps and returns json-line format.
  Returns an empty string if the vector is empty."
  [ms]
  (->> ms
       (map #(str (json/write-str %) \newline))
       str/join))

(defn json->map
  "Transforms json to a map.
  Return nil if the json string is nil or blank."
  [json]
  (when-not (str/blank? json)
    (json/read-str json :key-fn keyword)))

(defn json-lines->maps
  "Transforms json-lines to vector of maps.
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
