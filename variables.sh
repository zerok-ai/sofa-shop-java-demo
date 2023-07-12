# DB_DRIVER_TYPE = postgres / mysql
# DB_DRIVER_CLASS = org.postgresql.Driver / com.mysql.cj.jdbc.Driver
# DB_PRINT_SQL = true / false
# DB_URL_PARAMS =  / ?createDatabaseIfNotExist=true
# DB_HIBERNATE_DIALECT = org.hibernate.dialect.PostgreSQLDialect / org.hibernate.dialect.MySQL5Dialect
# DB_USERNAME =
# DB_PASSWORD =
# DB_HOST =

export DB_PRINT_SQL=false

export DEFAULT_NAMESPACE='sofa-shop'
random_string=$(cat /dev/urandom | tr -dc 'a-z0-9' | fold -w 4 | head -n 1)
export DEFAULT_EXTERNAL_HOSTNAME="sofa-shop.$random_string.getanton.com"
export APPLY_COMMAND='apply'
export DELETE_COMMAND='delete'
export MODE_MYSQL='mysql'
export MODE_POSTGRES='ps'

export MYSQL_USERNAME_SECRET=c2VydmljZV91c2Vy
export MYSQL_PASSWORD_SECRET=cGFzc3RmOQ==
export MYSQL_DRIVER_DIALECT=org.hibernate.dialect.MySQL5Dialect
export MYSQL_DRIVER_CLASS=com.mysql.cj.jdbc.Driver
export MYSQL_DRIVER_TYPE=mysql
export MYSQL_URL_PARAMS="?createDatabaseIfNotExist=true"
export MYSQL_HOST=mysql-svc.mysql.svc.cluster.local:3306
export MYSQL_HOST_LOCAL=localhost:3306

export PS_USERNAME_SECRET=cG9zdGdyZXM=
#export PS_PASSWORD_SECRET=UHkzd2p6NXdJUw==
export PS_PASSWORD_SECRET=STVOa243MzNQZg==
export PS_DRIVER_DIALECT=org.hibernate.dialect.PostgreSQLDialect
export PS_DRIVER_CLASS=org.postgresql.Driver
export PS_DRIVER_TYPE=postgresql
export PS_URL_PARAMS=
export PS_HOST=postgres-postgresql.postgres.svc.cluster.local:5432
export PS_HOST_LOCAL=localhost:5432
