FROM openjdk:21-oracle as build

WORKDIR /app

COPY . .

RUN ./mvnw package -DskipTests

FROM openjdk:21-oracle as runtime

WORKDIR /app

COPY --from=build /app/target/*.jar ./app.jar

EXPOSE 80

CMD java -jar ./app.jar -Dserver.port=80
