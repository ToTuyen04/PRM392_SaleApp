# Use Maven with Java 21 to build the project
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy project files
COPY /SalesApp/pom.xml .
COPY /SalesApp/src src
#COPY /scorelens/uploads uploads
#COPY "ArialUnicodeMSBold.ttf" /app/

# Build the application without running tests
RUN mvn clean package -DskipTests

# Optional: Check contents
RUN ls -l /app
RUN ls -l /app/target

# Use a lightweight JDK 21 runtime image
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/SalesApp-0.0.1-SNAPSHOT.jar app.jar

# Optional: copy additional resources if needed
#COPY --from=build /app/uploads/lab /app/uploads/lab
#COPY --from=build /app/ArialUnicodeMSBold.ttf /app/

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]