#Dockerfile for backend

FROM maven:3.9.9-eclipse-temurin-17 AS build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD pom.xml $HOME
RUN mvn verify --fail-never
ADD . $HOME
RUN mvn clean package -DskipTests -PwithFront


FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
RUN mkdir be
COPY --from=build /usr/app/target/*.jar app.jar
RUN rm -rf /usr/share/nginx/html/*
RUN mkdir -p /usr/share/nginx/html
COPY --from=build /usr/app/frontend/build/* /usr/share/nginx/html
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

