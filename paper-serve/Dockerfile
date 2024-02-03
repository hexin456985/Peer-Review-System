FROM openjdk:11-jre-slim

WORKDIR /app

EXPOSE 10005

COPY target/paper-serve-0.0.1-SNAPSHOT.jar /app/paper.jar
#RUN bash -c 'touch /thesis.jar'

CMD ["java", "-jar", "paper.jar"]