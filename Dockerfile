FROM maven:3.9.11-eclipse-temurin-11 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests -Dproject.build.sourceEncoding=UTF-8 clean package

FROM eclipse-temurin:11-jre
WORKDIR /app
ENV LANG=ko_KR.UTF-8
ENV LC_ALL=ko_KR.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "/app/app.jar"]
