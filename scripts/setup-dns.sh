 #!/bin/bash -l
THIS_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
source $THIS_DIR/variables.sh

echo ''
echo '-----------------SETTING-UP-DNS-----------------'

 services=($(kubectl get services -n ingress-nginx --no-headers --field-selector metadata.name=ingress-nginx-controller | awk '{print $1}'))
 ips=($(kubectl get services -n ingress-nginx --no-headers --field-selector metadata.name=ingress-nginx-controller | awk '{print $4}'))
 gcp_dns_project=black-scope-358204
 clusterDomain=$CLUSTER_DOMAIN
 sofaShopDomain=$EXTERNAL_HOSTNAME

 if ! [ -z "$clusterDomain" ]
 then
    extip=$ips
    isIP=`echo "$extip" | awk '/^([0-9]{1,3}[.]){3}([0-9]{1,3})$/{print $1}'`
    recordType="A"
    if [ -z $isIP ]; then
      recordType="CNAME"
    fi

    cluster_domain_exists=`gcloud dns --project="${gcp_dns_project}" record-sets list --name "${clusterDomain}" --zone="${DNS_ZONE}" --type=$recordType --format=yaml`
    if [ -z "$cluster_domain_exists" ] || [ "$cluster_domain_exists" == "" ]; then
       gcloud dns --project=$gcp_dns_project record-sets create $clusterDomain --zone=$DNS_ZONE --type=$recordType --rrdatas=$extip --ttl=10
    else
       gcloud dns --project=$gcp_dns_project record-sets update $clusterDomain --zone=$DNS_ZONE --type=$recordType --rrdatas=$extip --ttl=10
    fi

    api_domain_exists=`gcloud dns --project="${gcp_dns_project}" record-sets list --name "${sofaShopDomain}" --zone="${DNS_ZONE}" --type=$recordType --format=yaml`
    if [ -z "$api_domain_exists" ] || [ "$api_domain_exists" == "" ]; then
       gcloud dns --project=$gcp_dns_project record-sets create $sofaShopDomain --zone=$DNS_ZONE --type=$recordType --rrdatas=$extip --ttl=10
    else
       gcloud dns --project=$gcp_dns_project record-sets update $sofaShopDomain --zone=$DNS_ZONE --type=$recordType --rrdatas=$extip --ttl=10
    fi
 fi
