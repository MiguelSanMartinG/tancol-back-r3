# Usa una imagen base de Java
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo pom.xml y los archivos de configuración de Maven
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

# Usa una imagen más ligera para el runtime
FROM eclipse-temurin:17-jre-alpine

# curl: Coolify/Docker healthcheck. Alpine JRE no lo trae; sin él el check falla y reinicia.
RUN apk add --no-cache curl

# Establece el directorio de trabajo para la aplicación
WORKDIR /app

# Copia el archivo .jar generado en el paso anterior
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

# Heap = 50% de la RAM del contenedor (Coolify Maximum Memory Limit).
# Con 1g en Coolify → ~512m heap y margen para metaspace/nativo.
# No usar -Xmx768m: con tope de 1g Linux mata el proceso (SIGKILL, sin stacktrace).
# Profile test: es el que ya usa este servidor (Railway). prod exige DATABASE_*.
ENTRYPOINT ["java","-Duser.timezone=America/Mexico_City","-XX:+UseG1GC","-XX:MaxRAMPercentage=50.0","-XX:InitialRAMPercentage=20.0","-XX:MaxMetaspaceSize=192m","-XX:+ExitOnOutOfMemoryError","-jar","app.jar","--spring.profiles.active=prod"]

HEALTHCHECK --interval=30s --timeout=5s --start-period=120s --retries=5 \
    CMD curl -fsS http://localhost:8080/api/v1/health || exit 1
