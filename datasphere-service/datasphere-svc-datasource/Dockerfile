# 使用多阶段构建
FROM eclipse-temurin:21-jdk-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制Maven配置和源码
COPY pom.xml .
COPY src ./src

# 安装Maven并构建
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建工作目录
WORKDIR /app

# 从构建阶段复制jar包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8081

# JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
