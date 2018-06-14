# situate

[![Clojars Project](https://img.shields.io/clojars/v/situate.svg)](https://clojars.org/situate)

Very much like `lein deploy`, but with fewer features.

## Usage

Put `[situate "0.1.2"]` into your `:plugins`. You'll probably
also want to put `[s3-wagon-private "1.3.2"]` in there,
too, and put some `:deploy-repositories` pointing at some
private S3 buckets.

Then,

    lein situate

Or

    lein situate <repository>
    
This plugin will do exactly what `deploy` does, but it will
never ask you for a username or password, and will not look for
these in any standard places for passwords. Instead, it is
meant for using the AWS standard ways of authenticating, e.g.
via environment variables, `~/.aws/credentials`, or via IAM instance
profiles in EC2.

Also useful is the `write-version` command, which will emit:

    PROJECT_VERSION=x.y.z
    
By default writing to `version.properties`, but just pass a
different destination as an argument.