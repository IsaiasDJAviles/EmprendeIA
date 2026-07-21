# ---- Etapa de build ----
FROM eclipse-temurin:24-jdk AS build
WORKDIR /app

# Copiar primero solo lo necesario para resolver dependencias -> mejor cache de capas.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# ---- Etapa de runtime ----
FROM eclipse-temurin:24-jre
WORKDIR /app

COPY --from=build /app/target/emprendeia-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
