FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY build/libs/post-0.0.1.jar post.jar
ENTRYPOINT ["java","-jar","/post.jar"]
