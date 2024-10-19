FROM maven:3.8.3-openjdk-17

WORKDIR /app

COPY . .

RUN mvn clean install

EXPOSE 8080

WORKDIR /app/target/

CMD ["java", "-jar","/app/target/cwms-0.0.1.jar", "com.bh.cwms.CwmsApplicationKt"]