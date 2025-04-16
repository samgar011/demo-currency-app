FROM openjdk:17-oracle

WORKDIR edemo

COPY ./target/*.jar demo-app-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "demo-app-0.0.1-SNAPSHOT.jar"]