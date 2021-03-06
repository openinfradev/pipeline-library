#!/usr/bin/env groovy

def call(String dir, String key) {
  value = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL_PANGYO} get ${dir}/${key}").trim()
  return value
}
