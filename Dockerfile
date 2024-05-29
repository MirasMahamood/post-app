FROM openjdk:17-jdk-alpine
VOLUME /tmp
EXPOSE 8080
COPY build/libs/post-0.0.1.jar post.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /post.jar"]
