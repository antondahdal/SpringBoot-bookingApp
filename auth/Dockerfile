FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/event-booking-auth-0.0.1-SNAPSHOT.jar app.jar
RUN useradd -r -u 1001 app \
    && printf '%s\n' \
      '#!/bin/sh' \
      'if [ -z "$JAVA_OPTS" ]; then JAVA_OPTS="-XX:MaxRAMPercentage=75.0"; fi' \
      'if [ -n "$PORT" ]; then export SERVER_PORT="$PORT"; fi' \
      'exec java $JAVA_OPTS -Dserver.address=0.0.0.0 ${SERVER_PORT:+-Dserver.port=$SERVER_PORT} -jar app.jar' \
      > /entrypoint.sh && chmod 755 /entrypoint.sh
USER app
EXPOSE 8082
ENTRYPOINT ["/entrypoint.sh"]
