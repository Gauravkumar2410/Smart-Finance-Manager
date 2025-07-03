# Stage 1: Build the application
# Use a base image with Java and Maven installed
FROM openjdk:17-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files (pom.xml) first to leverage Docker cache
# This step only needs to be re-run if pom.xml changes
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the Spring Boot application using Maven
# -Dmaven.test.skip=true skips running tests during the build
# This will produce a JAR file in the 'target' directory
RUN ./mvnw clean install -Dmaven.test.skip=true

# Stage 2: Create the final lightweight runtime image
# Use a smaller JRE image for the final application to reduce image size
FROM openjdk:17-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
# The JAR file is typically named 'your-application-name-version.jar'
# You might need to adjust 'expensetracker-0.0.1-SNAPSHOT.jar'
# based on your actual project's artifactId and version in pom.xml
COPY --from=build /app/target/expensetracker-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that your Spring Boot application listens on (default is 8080)
EXPOSE 8080

# Define the command to run the application
# This will execute the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add environment variables for the database connection if needed
# It's generally better to pass these at runtime using -e or a .env file
# ENV SPRING_DATASOURCE_URL="jdbc:mysql://mysql.railway.internal:3306/railway"
# ENV SPRING_DATASOURCE_USERNAME="root"
# ENV SPRING_DATASOURCE_PASSWORD="your_password"
