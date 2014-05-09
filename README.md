Sotju Project
===

## Intro

Search engine based on Elastsearch, aiming to index and search resources within TJU, while providing:

* full functionality and extensibility
* cluster-oriented stablity
* easy deployment
* flexibility to index across multiple sites or schemas

## Packages

* so: sotju projects
* so/so-core: core package, including models, schema definations and search adapters.
* so/so-crawler: crawler package, including rules, scheduler, worker, fetchers, parsers and extractors.
* so/so-node: node package, a wrapper for elastsearch data node.
* so/so-dispatcher: dispatcher package, provides a RESTful API for accessing search engine.
* analysis-ik: a modified version of elasticsearch-analysis-ik

## TODOs

* A human interface for searching (say homepage), and its optimization for mobile devices
* Query log analyzers: hot query, related query, etc.
* More schemas: Service, Organization, Software, Music, (Page, ) etc.
* More sites: Twt, etc.
* A human interface for manual operations
* ... To be discovered ...

## Services

### Redis

Deploy one redis instance (or cluster) serving the whole cluster.

### so-node

So-node is a wrapper of Elasticsearch, provideing basic searching functionality.

You can always run multiple so-node on different machines.

### so-crawler: scheduler

Scheduler periodicly schedule crawler tasks for worker according to site configs.

### so-crawler: worker

Worker keep executing scheduled crawler tasks with rules.

### so-dispatcher

Provides RESTful API for searching, notification and management. Also a part of cluster.

## Deployment

* Deploy redis and alter redis.host in filter.properties @ so-core
* Package so-node with so-node/bin/build.sh and transfer it, or just install with so-node/bin/install.sh
* Package so-crawler with so-crawler/bin/build.sh and transfer it, or just install with so-crawler/bin/install.sh
* Package so-dispatcher with maven, and deploy it with either jetty or tomcat

## Startup

* Start redis first
* Start so-node:
`cd ~/install-so-node && sh node.sh start`
* If you are running sotju for first time, execute the following command:

```
# cd ~/install-so-node
# java -cp so-node.jar org.tju.so.crawler.SchemaImporter
# java -cp so-node.jar org.tju.so.crawler.RuleImporter
```
* Start so-crawler scheduler:
`cd ~/install-so-crawler && sh scheduler.sh start`
* Start so-node:
`cd ~/install-so-crawler && sh worker.sh start`
* Deploy so-dispatcher with either jetty or tomcat and start it:

```
# cp so-dispatcher.war ~/jetty/webapps/
# sh ~/jetty/bin/jetty.sh start
```
