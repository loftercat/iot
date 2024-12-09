FROM openjdk:8-jre
VOLUME /tmp
ARG JAR_NAME
ENV J_ENV_NAME=${JAR_NAME}
RUN echo ${JAR_NAME}
RUN echo $J_ENV_NAME

ARG ENV_NAME
ENV S_ENV_NAME=${ENV_NAME}
RUN echo ${ENV_NAME}
RUN echo $S_ENV_NAME

COPY target/*.jar ${JAR_NAME}.jar
RUN touch /${JAR_NAME}.jar
RUN echo 'Asia/Shanghai'>/timezone
EXPOSE 32033
#ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+08 -Dspring.config.location=optional:classpath:/ -jar $J_ENV_NAME.jar
#ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+08 -jar $J_ENV_NAME.jar
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+08 -Dspring.profiles.active=$S_ENV_NAME -jar $J_ENV_NAME.jar
