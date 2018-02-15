FROM   openjdk:8-jre-alpine
ADD    build/libs/maintenance.jar /app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms64m -Xmx256m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]