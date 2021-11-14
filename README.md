[![Build Status](https://github.com/CapOfCave/json-parser/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CapOfCave/json-parser/actions?query=workflow%3A"Java+CI+with+maven"+branch%3Amaster)
[![Codecov Coverage Status](https://codecov.io/gh/CapOfCave/json-parser/branch/master/graph/badge.svg)](https://codecov.io/gh/CapOfCave/json-parser)

# json-parser
> (nearly/soon) RFC 8259 compliant JSON parser for Java - implemented using strict TDD 

My take on writing a JSON parser - used as an experiment for [Test Driven Development](https://en.wikipedia.org/wiki/Test-driven_development) .

Tested with test data provide by https://github.com/nst/JSONTestSuite. 

The parser currently passes all test cases for JSONs to accept (and those labeled as indifferent). Consequencely, it can parse all JSONs which are valid according to RFC 8259. (It does, however, still accept some inputs which are invalid according to RFC 8259)
