#
# Gradle image for the build stage.
#
FROM gradle:jdk11 as builder

ENV APP_HOME=/integration-demo

WORKDIR $APP_HOME

#
# Copy the Gradle config, source code, and static analysis config
# into the build container.
#
COPY --chown=gradle:gradle build.gradle settings.gradle $APP_HOME/
COPY --chown=gradle:gradle src $APP_HOME/src
COPY --chown=gradle:gradle config $APP_HOME/config

#
# Build the application.
#
RUN gradle build

#
# Java image for the application to run in.
#
FROM openjdk:12-alpine

# I found this wait script here...
# https://www.datanovia.com/en/lessons/docker-compose-wait-for-container-using-wait-tool/docker-compose-wait-for-mysql-container-to-be-ready/
# It allows for the job to wait until MySQL is up before kicking off.

ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait

#
# Copy the jar file in and name it app.jar.
#
COPY --from=builder /integration-demo/build/libs/integration-demo-0.1.0.jar app.jar