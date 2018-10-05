FROM andreptb/oracle-java:8

RUN apk add tzdata
RUN cp /usr/share/zoneinfo/America/Chicago /etc/localtime
RUN echo "America/Chicago" > /etc/timezone
RUN apk del tzdata

EXPOSE 8080

ARG JAR_FILE=target/activity-tracker-1.0-SNAPSHOT.jar

ADD ${JAR_FILE} activity-tracker.jar

ENTRYPOINT ["java","-jar","activity-tracker.jar"]
