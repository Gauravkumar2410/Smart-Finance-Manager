# Stage 1: Build the application
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

RUN chmod +x ./mvnw
RUN ./mvnw clean install -Dmaven.test.skip=true

# Stage 2: Create the final lightweight runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/expensetracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
