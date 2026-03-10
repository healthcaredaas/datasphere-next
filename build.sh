#!/bin/bash

# DataSphere Next 构建脚本
# 用法: ./build.sh [模块名]

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  DataSphere Next 构建脚本${NC}"
echo -e "${GREEN}========================================${NC}"

# 获取脚本所在目录
SCRIPT_DIR=$(cd "$(dirname "$0")"; pwd)
cd "$SCRIPT_DIR"

# Maven构建
build_maven() {
    echo -e "${YELLOW}开始Maven构建...${NC}"
    mvn clean install -DskipTests -q
    echo -e "${GREEN}Maven构建完成!${NC}"
}

# 构建Docker镜像
build_docker() {
    module=$1
    service_name=$(basename "$module")
    port=$2

    echo -e "${YELLOW}构建Docker镜像: ${service_name}${NC}"

    # 创建临时Dockerfile
    cat > "$module/Dockerfile" << EOF
FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="chenpan.ai@qq.com"
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE ${port}
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

    # 构建镜像
    docker build -t "datasphere/${service_name}:latest" "$module"

    echo -e "${GREEN}Docker镜像构建完成: datasphere/${service_name}:latest${NC}"
}

# 构建指定模块或全部
if [ -z "$1" ]; then
    # 构建全部
    build_maven

    # 构建所有服务的Docker镜像
    SERVICES=(
        "datasphere-service/datasphere-svc-datasource:8081"
        "datasphere-service/datasphere-svc-integration:8082"
        "datasphere-service/datasphere-svc-standard:8083"
        "datasphere-service/datasphere-svc-master:8084"
        "datasphere-service/datasphere-svc-metadata:8085"
        "datasphere-service/datasphere-svc-quality:8086"
        "datasphere-service/datasphere-svc-asset:8087"
        "datasphere-service/datasphere-svc-security:8088"
    )

    for service in "${SERVICES[@]}"; do
        IFS=':' read -r module port <<< "$service"
        build_docker "$module" "$port"
    done

else
    # 构建指定模块
    MODULE=$1
    echo -e "${YELLOW}构建模块: ${MODULE}${NC}"
    mvn clean install -pl "${MODULE}" -am -DskipTests -q
fi

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  构建完成!${NC}"
echo -e "${GREEN}========================================${NC}"
