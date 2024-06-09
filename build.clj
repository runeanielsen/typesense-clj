(ns build
  (:require [clojure.string :as str]
            [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as d]
            [clojure.tools.deps :as t]))

(def lib 'io.github.runeanielsen/typesense-clj)
(def version (format "0.1.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def jar-file (format "target/%s-%s.jar" (name lib) version))

;; delay to defer side effects (artifact downloads)
(def basis (delay (b/create-basis {:project "deps.edn"})))

(def pom-template
  [[:description "Clojure HTTP client for Typesense "]
   [:url "https://github.com/runeanielsen/typesense-clj/"]
   [:licenses
    [:license
     [:name "MIT"]
     [:url "https://github.com/runeanielsen/typesense-clj/blob/master/LICENSE"]]]
   [:developers
    [:developer
     [:name "Rune Andreas Nielsen"]]]])

(defn- run-task [aliases]
  (println "\nRunning task for" (str/join "," (map name aliases)))
  (let [basis    (b/create-basis {:aliases aliases})
        combined (t/combine-aliases basis aliases)
        cmds     (b/java-command
                  {:basis basis
                   :main 'clojure.main
                   :main-args (:main-opts combined)})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Task failed" {})))))

(defn clean []
  (b/delete {:path "target"}))

(defn build [_]
  (clean)
  (b/write-pom {:pom-data pom-template
                :class-dir class-dir
                :lib lib
                :version version
                :basis @basis
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn tests [_]
  (clean)
  (run-task [:test :runner]))

(defn deploy [_]
  (clean)
  (build)
  (d/deploy {:installer :remote :artifact (b/resolve-path jar-file)
             :pom-file (b/pom-path {:class-dir class-dir :lib lib})}))
