#!/usr/bin/env groovy

def call(String prefix) {
  output = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL} get --prefix ${prefix} -w json").trim()

  def result = readJSON text: output
  if (!result.kvs) {
    error("No k8s endpoints registered in etcd. Exiting job..")
  }

  vmKey = new String(result.kvs[0].key.decodeBase64())
  vmVal = new String(result.kvs[0].value.decodeBase64())

  // check if retrieved key is correct
  if (vmKey.startsWith(prefix) && vmVal ) {
    println("Retrieved vmName: ${vmVal} from key: ${vmKey}")
  } else {
    error("Error getting k8s VM name from etcd. Retrieved key: ${vmKey}")
  }

  return vmVal
}
