FROM   openjdk:8-jre-alpine
ADD    build/libs/zipkin.jar /zipkin.jar
EXPOSE 8080
CMD    java -Djava.security.egd=file:/dev/./urandom -jar /zipkin.jar
