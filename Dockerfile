# --- сборка фронтенда: Vite-статика ---
FROM node:26-alpine AS frontend-build
WORKDIR /app
COPY frontend/package.json frontend/pnpm-lock.yaml ./
RUN npm install -g pnpm@12 --silent && pnpm install --frozen-lockfile
COPY frontend/ ./
RUN pnpm build

# --- сборка бекенда: bootJar со статикой фронтенда внутри ---
FROM eclipse-temurin:25-jdk AS backend-build
WORKDIR /app/backend
COPY backend/ ./
COPY --from=frontend-build /app/dist src/main/resources/static
RUN ./gradlew bootJar --no-daemon --console=plain -q

# --- рантайм: только JRE, приложение стартует само и слушает PORT ---
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=backend-build /app/backend/build/libs/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
