#!/usr/bin/env groovy

def call(String prefix) {
  values = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL} get --prefix ${prefix} | head -n 2").trim().split('\n')
  // check if retrieved key is correct
  if (values[0].startsWith(prefix) && values[0].contains("vmName") && values[1] ) {
    println("Retrieved vmName: ${values[1]} from key: ${values[0]}")
  } else {
    error("Error getting k8s VM name from etcd. Retrieved key: ${values[0]}")
  }

  return values
}
