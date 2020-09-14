#!/usr/bin/env groovy

def call(String namePrefix) {
  println("Start deleting Openstack VMs...")
  fetchCloudsConf()
	sh """
		openstack server list --os-cloud taco-prod | grep ${namePrefix} | awk '{print \$2}' | xargs openstack server delete --os-cloud taco-prod --wait
    VOL_CNT=\$(openstack volume list --os-cloud taco-prod -f value | grep ${namePrefix} | wc -l)
    SNAP_CNT=\$(openstack volume snapshot list --os-cloud taco-prod -f value --volume ${namePrefix} | wc -l)
    if [ \$VOL_CNT -ne 0 ] && [ \$SNAP_CNT -eq 0 ]; then
      openstack volume list --os-cloud taco-prod | grep ${namePrefix} | awk '{print \$2}' | xargs openstack volume delete --os-cloud taco-prod --force || true
    fi
  """
  println("Deleting Openstack VM has been finished!")
}
