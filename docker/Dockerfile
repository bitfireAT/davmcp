# Stage 1: Cache Gradle dependencies
FROM gradle:latest AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
# Copy only build descriptor files (no source) so this layer is only
# invalidated when dependencies change, not on every source change.
COPY *.gradle.kts gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle
COPY server/build.gradle.kts /home/gradle/app/server/
WORKDIR /home/gradle/app
# Pre-warm the Gradle cache so Stage 2 can resolve dependencies without
# downloading them again from the network.
RUN gradle :server:dependencies --no-daemon

# Stage 2: Build Application
FROM gradle:latest AS build
# Import the pre-warmed Gradle dependency cache from Stage 1.
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Build the fat JAR, Gradle also supports shadow
# and boot JAR by default.
RUN gradle :server:fatJar --no-daemon

# Stage 3: Create the Runtime Image
FROM amazoncorretto:21-alpine AS runtime
EXPOSE 3000
RUN mkdir /app
WORKDIR /app
COPY --from=build /home/gradle/src/server/build/libs/*.jar /app/server.jar
ENTRYPOINT ["java","-jar","/app/server.jar"]
