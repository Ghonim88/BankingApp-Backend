FROM maven:3.9.5-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
COPY --from=build /target/bank-0.0.1-SNAPSHOT.jar bank.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "bank.jar"]