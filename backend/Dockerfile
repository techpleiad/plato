FROM openjdk:8

EXPOSE 8080

WORKDIR /applications

COPY plato-deploy/target/plato-deploy-0.0.1-SNAPSHOT-exec.jar plato-application.jar

ENTRYPOINT ["java","-jar", "plato-application.jar"]