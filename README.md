docker run -p 16379:6379 -d redis:7.2.4 redis-server --requirepass "password"
docker run -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=lock-example -d mysql:8.0.36 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
