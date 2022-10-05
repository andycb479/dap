FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/Users-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} Users-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/Users-0.0.1-SNAPSHOT.jar"]