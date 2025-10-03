# ===== deps =====
FROM eclipse-temurin:17-jdk-jammy as deps
WORKDIR /build
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/
# cache de dependencias
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw -q -DskipTests dependency:go-offline

# ===== package =====
FROM deps as package
WORKDIR /build
COPY ./src src/
# Empaqueta + repackage de Spring Boot (gracias al plugin)
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw -q -DskipTests clean package && \
    ls -lh target && \
    # Copia el JAR reempaquetado (excluye el .original)
    cp "$(ls target/*.jar | grep -v '\.original$')" /build/app.jar

# ===== final =====
FROM eclipse-temurin:17-jre-jammy AS final
WORKDIR /app
# usuario no-root (puedes mantener el tuyo si quieres)
ARG UID=10001
RUN adduser --disabled-password --gecos "" --home "/nonexistent" --shell "/sbin/nologin" --no-create-home --uid "${UID}" appuser
USER appuser

# Copia el ejecutable correcto
COPY --from=package /build/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
