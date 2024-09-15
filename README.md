To test the MySQL with Docker Desktop Applicaiton:

1st: 
docker run --detach --env MYSQL_ROOT_PASSWORD=252646 --env MYSQL_USER=root --env MYSQL_PASSWORD=252646 --env MYSQL_DATABASE=equal_bangladesh --name mysql --publish 3306:3306 mysql:8-oracle

2nd:
docker run --detach --env MYSQL_ROOT_PASSWORD=252646 --env MYSQL_PASSWORD=252646 --env MYSQL_DATABASE=equal_bangladeshl --publish 3306:3306 mysql:8-oracle


3rd:
mysqlsh
\connect root@localhost:3306
\sql
use equal_bangladesh
