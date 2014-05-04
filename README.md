Sotju Project
===

## Intro

Search engine based on Elastsearch, aiming to index and search resources within TJU, while providing:

* full functionality and extensibility
* cluster-oriented stablity
* easy deployment
* flexibility to index across multiple sites or schemas

## Packages

* so-core: core package, including models, schema definations and search adapters.
* so-crawler: crawler package, including rules, scheduler, worker, fethcers, parsers and extractors.
* so-node: node package, a wrapper for elastsearch data node.
* so-dispatcher: dispatcher package, provides a RESTful API for accessing search engine.

## TODOs

* Schema specified ranking
* Sorting results from multiple sites and schemas
* A human interface for searching (say homepage), and its optimization for mobile devices
* RESTful APIs for crawler (push, etc.)
* More schemas: Service, Organization, Software, Movie, Music, (Page, ) etc.
* More sites: Seeworld, Twt, TjuNewsCenter, etc.
* A human interface for manual operations
* ... To be discovered ...
