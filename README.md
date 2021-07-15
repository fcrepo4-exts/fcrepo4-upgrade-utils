fcrepo-upgrade-utils [![Build Status](https://github.com/fcrepo-exts/fcrepo-upgrade-utils/workflows/Build/badge.svg)](https://github.com/fcrepo-exts/fcrepo-upgrade-utils/actions)
==================

Utility for upgrading the [Fedora Commons repository](http://github.com/fcrepo/fcrepo).

Usage
-----

General usage is as follows:

```sh
java -jar target/fcrepo-upgrade-utils-<version>.jar [cli options | help]
```

The following CLI options are available:

```
usage: java -jar fcrepo-upgrade-util-<version>.jar
 -d,--digest-algorithm <arg>         The digest algorithm to use in OCFL.
                                     Default: sha512
 -h,--help                           Print these options
 -i,--input-dir <arg>                The path to the directory containing
                                     a Fedora 4.7.x or Fedora 5.x export
    --migration-user <arg>           The user to attribute OCFL versions
                                     to. Default: fedoraAdmin
    --migration-user-address <arg>   The address of the user OCFL versions
                                     are attributed to. Default:
                                     info:fedora/fedoraAdmin
 -o,--output-dir <arg>               The path to the directory where
                                     upgraded resources will be written.
                                     Default value:
                                     output_<yyyyMMdd-HHmmss>. For
                                     example: output_20200101-075901
 -p,--threads <arg>                  The number of threads to use.
                                     Default: the number of available
                                     cores
 -r,--source-rdf <arg>               The RDF language used in the Fedora
                                     export. Default: Turtle
 -R,--resource-info-file <arg>       The path the file that contains a
                                     list of resources to be processed
 -s,--source-version <arg>           The version of Fedora that was the
                                     source of the export. Valid values:
                                     5+,4.7.5
 -t,--target-version <arg>           The version of Fedora to which you
                                     are upgrading. Valid values: 6+,5+
 -u,--base-uri <arg>                 Fedora's base URI. For example,
                                     http://localhost:8080/rest
```

### Resuming Fedora 6 Migration

If a Fedora 6 migration is interrupted or a subset of resources fail to migrate, then a log file named
`remaining_TIMESTAMP.log` is created that contains information about the resources that were not migrated. The
migration can be resumed by passing this file back to the utility on a subsequent run. For example:

```shell
java -jar fcrepo-upgrade-utils.jar \
  --input-dir my-5.1.1-export \
  --source-version 5+ \
  --target-version 6+ \
  --base-uri http://localhost:8080/rest \
  --resource-info-file remaining_TIMESTAMP.log
```

Building
--------

To build the JAR file

``` sh
mvn package
```
