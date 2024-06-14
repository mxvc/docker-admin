# 容器管理面板
- 多主机容器管理
- 持续集成
- 持续部署
- 支持跨网络

# 安装
## docker 命令行安装

需准备一个mysql数据库

```
docker run -d -p 7001:7001  -e dbip=127.0.0.1 -dbport=3306 -e dbpwd=123456  -v /var/run/docker.sock:/var/run/docker.sock mooncn/docker-admin
```


## docker-compose 安装， 包含mysql数据库
```
version: '3'

services:
  admin:
    image: mooncn/docker-admin:latest
    restart: always
    ports:
      - "7001:7001" # 主机端口:容器端口
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock" 
    environment:
      dbip: 127.0.0.1 
      dbport: 3305
      dbpwd: 2zbdWEs6vRHe8wc # 密码
  mysql:
    image: mysql:5.7.35
    privileged: true
    environment:
      MYSQL_ROOT_PASSWORD: 2zbdWEs6vRHe8wc
      MYSQL_DATABASE: docker_admin
      TZ: Asia/Shanghai
    command:
      --lower_case_table_names=0
      --max_connections=1000
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_general_ci
      --wait_timeout=31536000
      --interactive_timeout=31536000
      --default-authentication-plugin=mysql_native_password
      --max_allowed_packet=100M
      --transaction-isolation=READ-COMMITTED
    ports:
      - "3305:3306"
    volumes:
      - ./mysql_data:/var/lib/mysql
```


## 使用
访问 http://127.0.0.1:7001 账号：admin 密码：123456




