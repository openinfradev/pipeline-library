#!/usr/bin/env groovy

def call() {
  println "Fetching cloud config from repository..."
  exists = fileExists('./clouds.yaml')
  if (! exists) {
    // TO-DO: add routine to fetch clolds.yaml


  }
}
