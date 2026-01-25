FROM --platform=linux/amd64 eclipse-temurin:17-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --chown=appuser:appgroup build/libs/*.jar app.jar

EXPOSE 8080

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]