# Gneiss [![Build Status](https://travis-ci.org/ane/gneiss.svg?branch=master)](https://travis-ci.org/ane/gneiss) [![License: AGPL v3](https://img.shields.io/badge/license-AGPL_3-green.svg)](http://www.gnu.org/licenses/agpl-3.0.html) [![Coverage Status](https://coveralls.io/repos/ane/gneiss/badge.svg?branch=master)](https://coveralls.io/r/ane/gneiss?branch=master)

<img src="https://raw.githubusercontent.com/ane/gneiss/master/gneiss.png" alt="Gneiss!"
title="Gneiss." align="right" />

Gneiss is an IRC log statistics server. It differs from traditional
IRC statistics software by implementing a functional web server that can
provide results over HTTP instead of generating static HTML files.

The API is meant to be used with any HTTP client. Currently, the idea is to
build a simple web application that talks to Gneiss over HTTP.

## Roadmap

- [ ] analysis
  - [ ] user interactions
	- [x] line count
	- [x] word count
	- [ ] kick count 
  - [ ] time of day analysis
  - [ ] date statistics
- [ ] http API
- [ ] web client (another project?)

## Installation

Currently to be determined. 

## Usage


    $ java -jar gneiss-0.1.0-standalone.jar [args]

## License

Copyright © 2015 Antoine Kalmbach <ane@iki.fi>

Distributed under the [GNU Affero General Public License v3](http://www.gnu.org/licenses/agpl-3.0.html).

