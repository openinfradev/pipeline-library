#!/usr/bin/env groovy
import groovy.json.JsonSlurperClassic

def call(String prefix) {
  output = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL} get --prefix ${prefix} -w json").trim()

  def slurper = new JsonSlurperClassic()
  def result = slurper.parseText(output)

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
