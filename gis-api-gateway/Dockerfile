FROM hub.c.163.com/library/java:8-alpine

MAINTAINER XXX XXX@nci.gis.com

ADD target/*.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "/app.jar"]
