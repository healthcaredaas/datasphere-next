#!/bin/bash

# DataSphere Next 停止脚本

echo "========================================"
echo "  DataSphere Next 停止脚本"
echo "========================================"

# 停止所有DataSphere服务
PIDS=$(ps aux | grep "datasphere" | grep -v grep | awk '{print $2}')

if [ -n "$PIDS" ]; then
    echo "停止DataSphere服务进程: $PIDS"
    echo "$PIDS" | xargs kill -9 2>/dev/null || true
fi

# 清理PID文件
rm -f /tmp/datasphere-*.pid

echo "所有服务已停止"
echo "========================================"
