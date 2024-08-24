# Base image with Maven installed already
FROM maven:3.9-eclipse-temurin-21 AS builder

# Copy the entire project into the Docker image
COPY . .

# Build project
RUN mvn clean package

# Base image containing OpenJDK 21
FROM eclipse-temurin:21-jre

# Copy the JAR file and pom.xml from the builder image to the working directory
COPY --from=builder target/*.jar /ward.jar
COPY --from=builder pom.xml /pom.xml

# Expose port 4000
EXPOSE 4000

# Run the JAR file as sudo user on entry point
ENTRYPOINT ["java", "-jar", "ward.jar"]
