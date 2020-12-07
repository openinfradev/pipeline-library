#!/usr/bin/env groovy

def call(String prefix) {
  values = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL} get --prefix ${prefix}").trim().split('\n')
  clusterSize = values.size()/2
  println("There are ${clusterSize} clusters available now")
  return clusterSize
}
