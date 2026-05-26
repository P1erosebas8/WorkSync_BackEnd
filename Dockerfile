# Paso 1: Compilar la aplicación usando Maven y Java 21 (o cambia a 17 si usas esa versión)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Paso 2: Ejecutar el archivo compilado .jar en un entorno ligero
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]