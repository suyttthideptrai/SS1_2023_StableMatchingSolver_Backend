#Dockerfile for backend

FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -PwithFront


FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build target/*.jar app.jar
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build frontend/build/* /usr/share/nginx/html
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
