FROM maven:3.9.5-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY . /app/.
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

FROM openjdk:21-slim as extract
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/target/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar ./${SERVICE_NAME}.jar
RUN java -Djarmode=layertools -jar ${SERVICE_NAME}.jar extract

FROM openjdk:21-slim
WORKDIR /app
COPY --from=extract app/dependencies/ ./
COPY --from=extract app/spring-boot-loader/ ./
COPY --from=extract app/snapshot-dependencies/ ./
COPY --from=extract app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]