# sofa-shop-java-demo
Backend and Frontend codebase

## Prerequisites

- Ensure you have the following dependencies installed in your local system:
    - [kubectl](https://kubernetes.io/docs/tasks/tools/)
    - [gcloud](https://cloud.google.com/sdk/gcloud)
- Ensure you have the following dependencies installed in the target cluster (depending upon the mode you want):
    - mysql OR
    - postgres

## Configuration
Certain variables are defined in `variables.sh` and used by the scripts in this directory. Please refer to it for more information.

## Options

- `-e <external_hostname>`: Specify the external hostname for the deployment.
- `-c <command>`: Specify the command (`apply` or `delete`). `apply` is the default value.
- `-n <namespace>`: Specify the namespace for the deployment.
- `-m <mode>`: Specify the mode (`ps` or `mysql`). `ps` is the default value.
- `-h <help>`: Prints down the help menu

### -e <external_hostname>
If you do not specify an external hostname, the default value would be created in the following format:
`sofa-shop.$MODE.$CLUSTER_NAME.getanton.com`  
where `$MODE` is the mode specified by `-m` and `$CLUSTER_NAME` is the name of the cluster obtained by running kubectl command `kubectl config current-context`.

### -m <mode>
The mode can be either `ps` or `mysql`. `ps` is the default value. If you specify `mysql`, the deployment will use MySQL as the database instead of PostgreSQL.

### -n <namespace>
If you do not specify a namespace, the default value would be created in the following format:
`sofa-shop-$MODE`  
where `$MODE` is the mode specified by `-m`.

## Examples

```bash
# Install the deployment using the default settings
./setup.sh -e example.com -c apply -n mynamespace -m ps