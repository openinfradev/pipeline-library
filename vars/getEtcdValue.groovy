#!/usr/bin/env groovy

def call(String dir, String key) {
  //value = sh(returnStdout: true, script: "curl -s ${env.ETCD_URL}/${dir}/${key} | jq -r .node.value").trim()
  value = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL} get ${key}").trim()
  return value 
}
