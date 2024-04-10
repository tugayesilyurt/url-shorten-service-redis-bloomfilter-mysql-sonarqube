
FROM openjdk:21-jdk
COPY target/shorten-url-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","--enable-preview", "-jar", "app.jar"]
