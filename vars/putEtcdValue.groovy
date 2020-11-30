#!/usr/bin/env groovy

def call(String dir, String key, String value) {
  //sh "curl -s ${env.ETCD_URL}/${dir}/${key} -XPUT -d value=${value}"
  sh "etcdctl --endpoints ${env.ETCD_URL} put ${dir}/${key} ${value}"
}
