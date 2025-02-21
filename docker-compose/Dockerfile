FROM maven:3.9.5-eclipse-temurin-21-alpine AS exception-handler-starter-build
WORKDIR /app
COPY /exception-handler-starter/pom.xml /app/
COPY /exception-handler-starter/src /app/src
RUN mvn clean install -DskipTests

FROM maven:3.9.5-eclipse-temurin-21-alpine AS build
ARG SERVICE_NAME
WORKDIR /app
COPY --from=exception-handler-starter-build /root/.m2 /root/.m2
COPY /${SERVICE_NAME}/pom.xml /app/
COPY /${SERVICE_NAME}/src /app/src/
RUN mvn --file /app/pom.xml clean package -DskipTests

FROM openjdk:21-slim AS extract
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/target/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar ./${SERVICE_NAME}.jar
RUN java -Djarmode=layertools -jar ${SERVICE_NAME}.jar extract

FROM openjdk:21-slim
RUN apt-get update && apt-get install --yes curl
WORKDIR /app
COPY --from=extract app/dependencies/ ./
COPY --from=extract app/spring-boot-loader/ ./
COPY --from=extract app/snapshot-dependencies/ ./
COPY --from=extract app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
