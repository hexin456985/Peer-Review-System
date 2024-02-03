FROM openjdk:11-jre-slim

WORKDIR /app

EXPOSE 10001

COPY target/role-serve-0.0.1-SNAPSHOT.jar /app/role.jar
#RUN bash -c 'touch /thesis.jar'

CMD ["java", "-jar", "role.jar"]