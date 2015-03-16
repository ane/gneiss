# Gneiss [![Build Status](https://travis-ci.org/ane/gneiss.svg?branch=master)](https://travis-ci.org/ane/gneiss) [![Coverage Status](https://coveralls.io/repos/ane/gneiss/badge.svg?branch=master)](https://coveralls.io/r/ane/gneiss?branch=master)  [![License: AGPL v3](https://img.shields.io/badge/license-AGPL_3-green.svg)](http://www.gnu.org/licenses/agpl-3.0.html)

<img src="https://raw.githubusercontent.com/ane/gneiss/master/gneiss.png" alt="Gneiss!"
title="Gneiss, isn't it?" align="right" />


*Gneiss* is an IRC log statistics server. It differs from traditional
IRC statistics software by implementing a functional web server that can
provide results over HTTP instead of generating static HTML files.

The plan is to eventually support real-time updates by watching log files in the background
and updating any changes at regular intervals. This lets you fire and forget the
Gneiss process, without worrying about Clojure startup times too much.

The API is meant to be used with any HTTP client. Currently, the idea is to
build a simple web application that talks to Gneiss over HTTP.

## Current Status

See the [wiki](https://github.com/ane/gneiss/wiki/Roadmap).

## Installation

Not yet. Clone the repo and run the tests, or something.

## Usage

    $ java -jar gneiss-0.1.0-standalone.jar [args]

## License

Copyright © 2015 Antoine Kalmbach <ane@iki.fi>

Distributed under the [GNU Affero General Public License v3](http://www.gnu.org/licenses/agpl-3.0.html).

