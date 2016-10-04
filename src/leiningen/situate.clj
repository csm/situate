(ns leiningen.situate
  (:require [cemerick.pomegranate.aether :as aether]
            [leiningen.deploy :as deploy]
            [leiningen.pom :as pom]
            [leiningen.core.main :as main]))

(def sanitize-repo-name #'leiningen.deploy/sanitize-repo-name)
(def fail-on-empty-project #'leiningen.deploy/fail-on-empty-project)
(def in-branches #'leiningen.deploy/in-branches)
(def abort-message #'leiningen.deploy/abort-message)

(defn repo-for
  [project name]
  (let [settings (merge (get (into {} (:repositories project)) name)
                        (get (into {} (:deploy-repositories project)) name))]
    [(sanitize-repo-name name) (or settings {:url name})]))

(defn situate
  "Similar in spirit to deploy, but does not require any
   explicit authentication, or even any authentication in
   your ~/.lein/profile.clj or ~/.lein/profile.clj.gpg.

   The intended usage of this plugin is to deploy to, e.g.,
   a private S3 repository, but using implicit authentication,
   such as a ~/.aws/credentials file, or an IAM role in EC2."
  ([project] (situate project
                      (if (pom/snapshot? project)
                        "snapshots"
                        "releases")))
  ([project repository]
   (fail-on-empty-project project)
   (let [branches (set (:deploy-branches project))]
     (when (and (seq branches)
                (in-branches branches))
       (apply main/abort "Can only deploy from branches listed in"
              ":deploy-branches:" branches)))
   (deploy/warn-missing-metadata project)
   (let [repo (repo-for project repository)
         files (deploy/files-for project repo)]
     (try
       (main/debug "Situating" files "to" repo)
       (aether/deploy
         :coordinates [(symbol (:group project) (:name project))
                       (:version project)]
         :artifact-map files
         :transfer-listener :stdout
         :repository [repo])
       (catch org.sonatype.aether.deployment.DeploymentException e
         (when main/*debug* (.printStackTrace e))
         (main/abort (abort-message (.getMessage e))))))))
