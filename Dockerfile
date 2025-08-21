FROM node:20 AS web
WORKDIR /build

RUN npm config set fund false

ADD web/package.json ./
RUN npm install --force
ADD web/ ./
RUN npm run build


FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-java AS java
ADD pom.xml ./
RUN mvn dependency:go-offline --fail-never
ADD . .
RUN mvn package -DskipTests  &&  mv target/*.jar /app.jar && rm -rf *


FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-jdk
WORKDIR /home
COPY --from=java /app.jar ./
COPY --from=web /build/dist/ ./static/
EXPOSE 80

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=Asia/Shanghai","-jar","app.jar"]
