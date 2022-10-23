FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD target/GalaxyCloud-0.0.1-SNAPSHOT.jar app-cloud.jar
ENTRYPOINT ["java", "-jar", "/app-cloud.jar"]