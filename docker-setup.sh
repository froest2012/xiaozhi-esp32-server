#!/bin/sh
# 本文件是用于一键自动下载本项目所需文件，自动创建好目录
# 所需条件（否则无法使用）：
# 1、请确保你的环境可以正常访问 GitHub 否则无法下载脚本
#
# 检测操作系统类型
case "$(uname -s)" in
    Linux*)     OS=Linux;;
    Darwin*)    OS=Mac;;
    CYGWIN*)    OS=Windows;;
    MINGW*)     OS=Windows;;
    MSYS*)      OS=Windows;;
    *)          OS=UNKNOWN;;
esac

# 设置颜色（Windows CMD 不支持，但不影响使用）
if [ "$OS" = "Windows" ]; then
    GREEN=""
    RED=""
    NC=""
else
    GREEN='\033[0;32m'
    RED='\033[0;31m'
    NC='\033[0m'
fi

echo "${GREEN}开始安装小智服务端...${NC}"

# 创建必要的目录
echo "创建目录结构..."
mkdir -p xiaozhi-server/data xiaozhi-server/models/SenseVoiceSmall
cd xiaozhi-server || exit

# 根据操作系统选择下载命令
if [ "$OS" = "Windows" ]; then
    DOWNLOAD_CMD="curl -L -o"
    if ! command -v curl >/dev/null 2>&1; then
        DOWNLOAD_CMD="powershell -Command Invoke-WebRequest -Uri"
        DOWNLOAD_CMD_SUFFIX="-OutFile"
    fi
else
    if command -v curl >/dev/null 2>&1; then
        DOWNLOAD_CMD="curl -L -o"
    elif command -v wget >/dev/null 2>&1; then
        DOWNLOAD_CMD="wget -O"
    else
        echo "${RED}错误: 需要安装 curl 或 wget${NC}"
        exit 1
    fi
fi

# 下载语音识别模型
echo "下载语音识别模型..."
if [ "$DOWNLOAD_CMD" = "powershell -Command Invoke-WebRequest -Uri" ]; then
    $DOWNLOAD_CMD "https://modelscope.cn/models/iic/SenseVoiceSmall/resolve/master/model.pt" $DOWNLOAD_CMD_SUFFIX "models/SenseVoiceSmall/model.pt"
else
    $DOWNLOAD_CMD "models/SenseVoiceSmall/model.pt" "https://modelscope.cn/models/iic/SenseVoiceSmall/resolve/master/model.pt"
fi

if [ $? -ne 0 ]; then
    echo "${RED}模型下载失败。请手动从以下地址下载：${NC}"
    echo "1. https://modelscope.cn/models/iic/SenseVoiceSmall/resolve/master/model.pt"
    echo "2. 百度网盘: https://pan.baidu.com/share/init?surl=QlgM58FHhYv1tFnUT_A8Sg (提取码: qvna)"
    echo "下载后请将文件放置在 models/SenseVoiceSmall/model.pt"
fi

# 下载配置文件
echo "下载配置文件..."
if [ "$DOWNLOAD_CMD" = "powershell -Command Invoke-WebRequest -Uri" ]; then
    $DOWNLOAD_CMD "https://raw.githubusercontent.com/xinnan-tech/xiaozhi-esp32-server/main/main/xiaozhi-server/docker-compose.yml" $DOWNLOAD_CMD_SUFFIX "docker-compose.yml"
    $DOWNLOAD_CMD "https://raw.githubusercontent.com/xinnan-tech/xiaozhi-esp32-server/main/main/xiaozhi-server/config.yaml" $DOWNLOAD_CMD_SUFFIX "data/.config.yaml"
else
    $DOWNLOAD_CMD "docker-compose.yml" "https://raw.githubusercontent.com/xinnan-tech/xiaozhi-esp32-server/main/main/xiaozhi-server/docker-compose.yml"
    $DOWNLOAD_CMD "data/.config.yaml" "https://raw.githubusercontent.com/xinnan-tech/xiaozhi-esp32-server/main/main/xiaozhi-server/config.yaml"
fi

# 检查文件是否存在
echo "检查文件完整性..."
FILES_TO_CHECK="docker-compose.yml data/.config.yaml models/SenseVoiceSmall/model.pt"
ALL_FILES_EXIST=true

for FILE in $FILES_TO_CHECK; do
    if [ ! -f "$FILE" ]; then
        echo "${RED}错误: $FILE 不存在${NC}"
        ALL_FILES_EXIST=false
    fi
done

if [ "$ALL_FILES_EXIST" = false ]; then
    echo "${RED}某些文件下载失败，请检查上述错误信息并手动下载缺失的文件。${NC}"
    exit 1
fi

echo "${GREEN}文件下载完成！${NC}"
echo "请编辑 data/.config.yaml 文件配置你的API密钥。"
echo "配置完成后，运行以下命令启动服务："
echo "${GREEN}docker-compose up -d${NC}"
echo "查看日志请运行："
echo "${GREEN}docker logs -f xiaozhi-esp32-server${NC}"

# 提示用户编辑配置文件
echo "\n${RED}重要提示：${NC}"
echo "1. 请确保编辑 data/.config.yaml 文件，配置必要的API密钥"
echo "2. 特别是 ChatGLM 和 mem0ai 的密钥必须配置"
echo "3. 配置完成后再启动 docker 服务"

# 最大重试次数
MAX_RETRIES=5
retry_count=0
build_success=false

echo "开始构建 Docker 镜像..."

while [ $retry_count -lt $MAX_RETRIES ] && [ "$build_success" != "true" ]; do
    echo "尝试构建 (尝试 $((retry_count+1))/$MAX_RETRIES)..."

    if docker-compose -f docker-compose.yml build --no-cache; then
        build_success=true
        echo "构建成功!"
    else
        retry_count=$((retry_count+1))
        if [ $retry_count -lt $MAX_RETRIES ]; then
            echo "构建失败，等待 30 秒后重试..."
            sleep 30
        else
            echo "达到最大重试次数，构建失败。"
        fi
    fi
done

if [ "$build_success" = "true" ]; then
    echo "启动容器..."
    docker-compose -f docker-compose.yml up -d
    echo "部署完成!"
else
    echo "构建失败，请检查网络连接或手动构建。"
    exit 1
fi
