FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/Gateway-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} Gateway-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/Gateway-0.0.1-SNAPSHOT.jar"]