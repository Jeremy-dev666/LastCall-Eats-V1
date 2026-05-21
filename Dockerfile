FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY lastcall-common/pom.xml lastcall-common/pom.xml
COPY lastcall-identity/pom.xml lastcall-identity/pom.xml
COPY lastcall-marketplace/pom.xml lastcall-marketplace/pom.xml
COPY lastcall-community/pom.xml lastcall-community/pom.xml
COPY lastcall-intelligence/pom.xml lastcall-intelligence/pom.xml
COPY lastcall-api/pom.xml lastcall-api/pom.xml
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY . .
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/lastcall-api/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
