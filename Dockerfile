FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-maven:2.0.1 AS java
WORKDIR /build

ADD pom.xml ./
RUN mvn package -DskipTests -q  --fail-never

ADD src src
RUN mvn clean package -DskipTests -q  &&    mv target/app.jar /home/app.jar && rm -rf *


FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-node:2.0.1 AS web
WORKDIR /build

ADD web/package.json ./
RUN pnpm install

ADD web/ ./
RUN pnpm run build


FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-jdk
WORKDIR /home
COPY --from=java /home/ ./
COPY --from=web /build/dist/ ./static/
EXPOSE 80

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=Asia/Shanghai","-jar","/home/app.jar"]
