# build web
FROM node AS web
WORKDIR /build

RUN npm install -g pnpm --registry https://registry.npmmirror.com/

ADD web/package.json ./
RUN pnpm install --registry https://registry.npmmirror.com/

ADD web/ ./
RUN pnpm run build

# build jar
FROM maven:3-openjdk-17 AS java
WORKDIR /build

ADD pom.xml ./
RUN mvn package -DskipTests -q  --fail-never

ADD src src
RUN mvn clean package -DskipTests -q

# merge web and jar
FROM amazoncorretto:17-alpine
WORKDIR /home

COPY --from=java /build/target/app.jar ./
COPY --from=web /build/dist/ ./static/

EXPOSE 80
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=Asia/Shanghai","-jar","/home/app.jar","--spring.profiles.active=default,prod"]
