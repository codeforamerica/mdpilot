FROM eclipse-temurin:17-jdk-alpine

RUN mkdir /opt/app /opt/pdf-fonts
COPY . /opt/app
COPY src/main/resources/pdf-fonts/* /opt/pdf-fonts/
WORKDIR /opt/app

RUN ./gradlew assemble && \
    cp /opt/app/build/libs/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
