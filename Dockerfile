FROM amazoncorretto:25-alpine

WORKDIR /app

COPY  target/totp.jar totp.jar

CMD ["java", "-Djdk.virtualThreadScheduler.parallelism=32", "-jar", "/app/totp.jar"]