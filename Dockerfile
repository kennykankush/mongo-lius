FROM maven:3.9.9-eclipse-temurin-23 AS builder

WORKDIR /app

COPY . .

RUN mvn package -Dmaven.test.skip=true

FROM maven:3.9.9-eclipse-temurin-23

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar /app/app.jar -Dserver.port=${PORT}