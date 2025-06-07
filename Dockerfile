# Stage 1: Build the app
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM eclipse-temurin:21-jdk
COPY --from=build /target/bank-0.0.1-SNAPSHOT.jar bank.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "bank.jar"]
