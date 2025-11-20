# 容器管理
- 多主机容器管理
- 持续集成
- 持续部署
- 支持跨网络

# 安装

## 准备工作
需要提前准备好一个数据库，例如 mysql

## docker-compose 安装
```
version: '3'

services:
  docker-admin:
    image: registry.cn-hangzhou.aliyuncs.com/jiangood/docker-admin
    ports:
      - "7001:7001" 
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock" 
    environment:
      db_ip: 127.0.0.1 
      db_port: 3306
      db_database: docker-admin
      db_password: 123456
```


## 使用
访问 http://127.0.0.1:7001 账号：superAdmin 密码打印在控制台
