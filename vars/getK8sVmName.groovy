#!/usr/bin/env groovy

def call(String prefix) {
  values = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL} get --prefix ${prefix} | head -n 2").trim().split('\n')
  // check first line & second line
  if (values[0].startsWith(prefix) && values[1] ) {
    print("Retrieved vmName: ${values[1]} from key: ${values[0]}")
  } else {
    error("Error getting k8s cluster endpoint from etcd")
  }

  return values
}
