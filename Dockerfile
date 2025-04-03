FROM openjdk:17-slim as builder
WORKDIR /workspace/app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw install -DskipTests

FROM openjdk:17-slim
WORKDIR /app

# Instala curl para healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Timezone
ENV TZ=America/Sao_Paulo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Diretórios e volume para logs
RUN mkdir -p /app/logs
VOLUME /app/logs

# Copia do JAR final
COPY --from=builder /workspace/app/target/*.jar app.jar

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Configurações do Java
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Entrypoint com exec
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
