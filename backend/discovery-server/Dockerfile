# Utilise une image de base OpenJDK 17 légère
FROM openjdk:17-jdk-slim

# Définition de l'argument JAR_FILE qui sera le chemin vers le JAR compilé
# Par défaut, on suppose qu'il sera dans target/
ARG JAR_FILE=target/*.jar

# Copie le fichier JAR de votre application dans l'image Docker sous le nom app.jar
COPY ${JAR_FILE} app.jar

# Expose le port par défaut de Spring Boot (sera surchargé par le port 0 configuré)
EXPOSE 8080

# Commande à exécuter lorsque le conteneur démarre
# Utilise java -jar pour exécuter l'application Spring Boot
ENTRYPOINT ["java","-jar","/app.jar"]