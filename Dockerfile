FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-node AS web
ADD web/package.json ./
RUN npm install
ADD web/ ./
RUN npm run build


FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-java AS java
# 缓存， -T 1C 表示 Maven 将在每个可用内核上使用一个线程。
ADD pom.xml ./
RUN mvn dependency:go-offline -q -B -T 1C --fail-never
ADD . .
RUN mvn clean package -DskipTests -q -B -T 1C &&    mv target/*.jar /app.jar && rm -rf *


FROM registry.cn-hangzhou.aliyuncs.com/mxvc/tmgg-base-jdk
WORKDIR /home
COPY --from=java /app.jar ./
COPY --from=web /build-node/dist/ ./static/
EXPOSE 80

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=Asia/Shanghai","-jar","app.jar"]
