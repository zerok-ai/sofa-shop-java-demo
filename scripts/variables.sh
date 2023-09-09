export DB_PRINT_SQL=false
export SETUP_NGINX_INGRESS_WAIT_TIME=45
export SETUP_SECRETS_WAIT_TIME=55
export SETUP_CLUSTERISSUER_WAIT_TIME=25
export SETUP_CERT_MANAGER_WAIT_TIME=25

if [[ -z $DNS_ZONE ]]; then
    export DNS_ZONE=zerok-dev
    # export DNS_ZONE=anton
fi

if [ "$DNS_ZONE" == "zerok-dev" ]
then
    export DOMAIN=zerok.dev
else
    export DOMAIN=getanton.com
fi

export DEFAULT_NAMESPACE='sofa-shop'
#random_string=$(cat /dev/urandom | tr -dc 'a-z0-9' | fold -w 4 | head -n 1)
random_string=$(openssl rand -hex 4)
export DEFAULT_EXTERNAL_HOSTNAME="sofa-shop.$random_string.$DOMAIN"
export APPLY_COMMAND='apply'
export DELETE_COMMAND='delete'
export MODE_MYSQL='mysql'
export MODE_POSTGRES='ps'

if [[ -z "$MYSQL_USERNAME_SECRET" ]]; then
  export MYSQL_USERNAME_SECRET=c2VydmljZV91c2Vy
fi
if [[ -z "$MYSQL_PASSWORD_SECRET" ]]; then
  export MYSQL_PASSWORD_SECRET=cGFzc3RmOQ==
fi
export MYSQL_DRIVER_DIALECT=org.hibernate.dialect.MySQL5Dialect
export MYSQL_DRIVER_CLASS=com.mysql.cj.jdbc.Driver
export MYSQL_DRIVER_TYPE=mysql
export MYSQL_URL_PARAMS="?createDatabaseIfNotExist=true"
export MYSQL_HOST=mysql-svc.mysql.svc.cluster.local:3306
export MYSQL_HOST_LOCAL=localhost:3306


if [[ -z "$PS_USERNAME_SECRET" ]]; then
  export PS_USERNAME_SECRET=cG9zdGdyZXM=
fi
if [[ -z "$PS_PASSWORD_SECRET" ]]; then
  export PS_PASSWORD_SECRET=VWo5dUZqWVkxWA==
fi
export PS_DRIVER_DIALECT=org.hibernate.dialect.PostgreSQLDialect
export PS_DRIVER_CLASS=org.postgresql.Driver
export PS_DRIVER_TYPE=postgresql
export PS_URL_PARAMS=
export PS_HOST=postgres-postgresql.postgres.svc.cluster.local:5432
export PS_HOST_LOCAL=localhost:5432
