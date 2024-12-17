FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/jobs-dict-api-0.0.1-SNAPSHOT-standalone.jar /jobs-dict-api/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/jobs-dict-api/app.jar"]
