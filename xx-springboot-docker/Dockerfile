FROM eclipse-temurin:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ./target/xx-springboot-docker-2.5.8.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]