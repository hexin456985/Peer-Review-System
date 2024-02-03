FROM openjdk:11-jre-slim

WORKDIR /app

EXPOSE 10004

COPY target/conferences-serve-0.0.1-SNAPSHOT.jar /app/conference.jar
#RUN bash -c 'touch /thesis.jar'

CMD ["java", "-jar", "conference.jar"]