FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src
COPY Resources ./Resources
COPY config ./config

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/Resources ./Resources
COPY --from=build /app/config ./config

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]