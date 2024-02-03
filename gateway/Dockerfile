FROM openjdk:11-jre-slim

WORKDIR /app

EXPOSE 9999

COPY target/gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar
#RUN bash -c 'touch /thesis.jar'

CMD ["java", "-jar", "gateway.jar"]