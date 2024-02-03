FROM openjdk:11-jre-slim

WORKDIR /app

EXPOSE 7887

COPY target/eureka-0.0.1-SNAPSHOT.jar /app/eureka.jar
#RUN bash -c 'touch /thesis.jar'

CMD ["java", "-jar", "eureka.jar"]