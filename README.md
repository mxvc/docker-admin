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
    image: mxvc/docker-admin:latest
    ports:
      - "7001:7001" # 主机端口:容器端口
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock" 
    environment:
      db_ip: 127.0.0.1 
      db_port: 3306
      db_database: docker-admin
      db_password: 123456 # 数据库密码
```

国内可将镜像替换为：registry.cn-hangzhou.aliyuncs.com/crec/docker-admin:latest

## 使用
访问 http://127.0.0.1:7001 账号：superAdmin 密码：123456
