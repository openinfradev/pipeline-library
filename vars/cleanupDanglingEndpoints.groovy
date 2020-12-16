#!/usr/bin/env groovy

def call(String prefix, String provider) {
    // Get VM list from openstack
    servers = sh(returnStdout: true, script: "openstack server list --os-cloud ${provider} -f json | jq --raw-output 'sort_by(.Name) | .[] | select(.Name|test(\"gate-centos-lb-ceph-online\")) | .Name'").trim().split()
    println("Servers: ${servers}")

    // Get k8s endpoint list from etcd
    output = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL} get --prefix ${prefix} -w json").trim()

    def result = readJSON text: output

    result.kvs.each{
      vmKey = new String(it.key.decodeBase64())
      vmVal = new String(it.value.decodeBase64())

      println("Checking entry '${vmVal}'...")
      if ( !servers.contains(vmVal) ) {
        println("Confirmed as dangling endpoint. Deleting it...")
        ret = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL} del ${vmKey}").trim()
        if(ret.toString() == 0) {
          error("Something went wrong.. No data deleted..")
        } else {
          println("${ret.toString()} key(s) have been deleted..")
        }

      } else {
        println("Found in server list.")
      }
    }
}
