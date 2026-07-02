# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and dependency file first to cache maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and compile
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the compiled jar from build stage
COPY --from=build /app/target/jmp.jar jmp.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "jmp.jar"]
