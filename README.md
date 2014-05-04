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
* so/so-crawler: crawler package, including rules, scheduler, worker, fethcers, parsers and extractors.
* so/so-node: node package, a wrapper for elastsearch data node.
* so/so-dispatcher: dispatcher package, provides a RESTful API for accessing search engine.
* analysis-ik: a modified version of elasticsearch-analysis-ik

## TODOs

* Schema specified ranking
* Sorting results from multiple sites and schemas
* A human interface for searching (say homepage), and its optimization for mobile devices
* Query completion suggester
* Query log analyzers: hot query, related query, etc.
* RESTful APIs for crawler (push, etc.)
* More schemas: Service, Organization, Software, Movie, Music, (Page, ) etc.
* More sites: Seeworld, Twt, TjuNewsCenter, etc.
* A human interface for manual operations
* ... To be discovered ...
