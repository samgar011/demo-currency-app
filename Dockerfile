FROM openjdk:17-oracle

WORKDIR demo-app

COPY ./target/*.jar demo-app-0.0.1-SNAPSHOT.jar

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "demo-app-0.0.1-SNAPSHOT.jar"]