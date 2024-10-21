FROM openjdk:17-jdk-alpine

# Add a spring user to run our application so that it doesn't need to run as root
RUN addgroup -S spring && adduser -S spring -G spring

# Set the user to use when running this image
USER spring:spring

# Set the working directory to /app
WORKDIR /app

# Copy the jar file from the build stage to the working directory
COPY target/*.jar app.jar

EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]