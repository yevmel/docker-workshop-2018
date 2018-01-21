FROM   openjdk:8-jre-alpine
ADD    build/libs/eureka.jar /eureka.jar
EXPOSE 8080
CMD    java -Djava.security.egd=file:/dev/./urandom -jar /eureka.jar