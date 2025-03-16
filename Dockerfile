FROM maven:3.6.3-jdk-14

WORKDIR /usr/src/axreng

COPY . .

RUN mvn clean package

EXPOSE 4567

CMD ["java", "-jar", "target/crawler-axreng.jar"]
