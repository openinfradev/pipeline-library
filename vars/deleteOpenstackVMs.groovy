#!/usr/bin/env groovy

def call(String namePrefix, String nameKey, String provider) {
  println("Start deleting Openstack VMs...")
	sh """
		openstack server list --os-cloud ${provider} | grep ${namePrefix} | awk '{print \$2}' | xargs openstack server delete --os-cloud ${provider} --wait
    VOL_CNT=\$(openstack volume list --os-cloud ${provider} -f value | grep ${namePrefix} | wc -l)
    SNAP_CNT=\$(openstack volume snapshot list --os-cloud ${provider} -f value --volume ${namePrefix} | wc -l)
    if [ \$VOL_CNT -ne 0 ] && [ \$SNAP_CNT -eq 0 ]; then
      openstack volume list --os-cloud ${provider} | grep ${namePrefix} | awk '{print \$2}' | xargs openstack volume delete --os-cloud ${provider} --force || true
    fi
  """
  println("Deleting Openstack VM has been finished!")
  
  println("debugging nameKey:${nameKey}.")
  println("debugging nameKey:${nameKey.trim()}.")

  // Delete vmName from etcd //
  if (nameKey.trim()) {
    values = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL_PANGYO} get ${nameKey}").trim().split('\n')
    if (values[0].contains("vmName") && values[1]) {
      print("Retrieved vmName: ${values[1]} from key: ${values[0]}")
      ret = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL_PANGYO} del ${nameKey}").trim()
    } else {
      error("Error deleting k8s VM name from etcd. Failed to get specified key.")
    }
  } else {
    // If nameKey is not specified, find nameKey by searching vmName
    output = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL_PANGYO} get --prefix k8s_endpoint -w json").trim()
    def result = readJSON text: output
    if (!result.kvs) {
      println("No k8s endpoints registered in etcd.")
    } else {
      foundNameKey = ""
      result.kvs.each { item ->
        vmKey = new String(item.key.decodeBase64())
        vmVal = new String(item.value.decodeBase64())
        if (vmVal == namePrefix) {
          println("Found vmName: ${vmVal}")
          foundNameKey = vmKey
          println("Found vmKey from vmName: ${vmKey}")

          ret = sh(returnStdout: true, script: "etcdctl --endpoints ${env.ETCD_URL_PANGYO} del ${foundNameKey}").trim()
        }
      }
      if (!foundNameKey) {
        println("Failed to delete namePrefix from etcd.. VM name not found")
      }
    }
  }
}

