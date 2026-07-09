# Stage: Run the pre-compiled application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the compiled jar from host target folder
COPY target/jmp.jar jmp.jar

# Expose the microservice port
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "jmp.jar"]
