#!/bin/bash

# DataSphere Next 快速启动脚本

set -e

echo "========================================"
echo "  DataSphere Next 快速启动脚本"
echo "========================================"

# 检查Java版本
java -version 2>&1 | head -n 1

# 启动数据源服务
echo "启动数据源服务 (端口: 8081)..."
cd datasphere-service/datasphere-svc-datasource
mvn spring-boot:run > /tmp/datasource.log 2>&1 &
echo $! > /tmp/datasphere-datasource.pid
cd ../..

echo "等待服务启动..."
sleep 5

echo ""
echo "服务启动状态:"
echo "  - 数据源服务: http://localhost:8081"
echo ""
echo "查看日志: tail -f /tmp/datasource.log"
echo "停止服务: ./stop.sh"
echo "========================================"
