(ns leiningen.write-version)

(defn write-version
  "Write the current project version to 'outfile'.
   Default outfile is 'version.properties'."
  ([project] (write-version project "version.properties"))
  ([project outfile]
   (spit outfile (str "PROJECT_VERSION=" (:version project) "\n"))))