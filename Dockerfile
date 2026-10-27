FROM eclipse-temurin:25-jre-alpine
VOLUME /tmp
COPY build/libs/lab6-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]