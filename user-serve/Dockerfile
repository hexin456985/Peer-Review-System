FROM openjdk:11-jre-slim

WORKDIR /app

EXPOSE 10002

COPY target/user-serve-0.0.1-SNAPSHOT.jar /app/user.jar
#RUN bash -c 'touch /thesis.jar'

CMD ["java", "-jar", "user.jar"]