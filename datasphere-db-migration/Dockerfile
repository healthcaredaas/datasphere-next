# DataSphere Database Migration Service
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="chenpan <chenpan.ai@qq.com>"
LABEL description="DataSphere Database Migration Service"
LABEL version="2.0.0"

# 设置工作目录
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# 创建非root用户
RUN addgroup -S datasphere && adduser -S datasphere -G datasphere
USER datasphere:datasphere

# 暴露端口
EXPOSE 8099

# JVM 参数
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]