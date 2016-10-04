FROM java:8

ENV JAR_FILE lite-notification-service-1.0-SNAPSHOT.jar
ENV CONFIG_FILE /conf/notification-service-config.yaml

WORKDIR /opt/lite-notification-service

COPY build/libs/$JAR_FILE /opt/lite-notification-service

CMD java "-jar" $JAR_FILE "server" $CONFIG_FILE