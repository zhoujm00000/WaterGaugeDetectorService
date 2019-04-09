#!/bin/bash
#安装脚本

clean_cmds=""
#安装失败后，清理已安装的环境
function clean_env {
        #echo ${param}
        IFS_OLD=${IFS}
        IFS=";"
        for cmd in ${p};do
                bash -c ${cmd}
        done
        IFS=${IFS_OLD}
}
	
# 安装nodejs
echo "1.安装nodejs..."
sudo apt-get install -y nodejs-legacy

clean_cmds="apt-get remove -y nodejs"
if [ $? -eq 0 ];then
  node -v
else
  echo "nodejs 安装失败，清理环境，退出安装"
  clean_env
  exit 1
fi
echo "nodejs安装成功!"


# 安装npm
echo "2.安装npm..."
sudo apt-get install -y npm 

clean_cmds="npm remove uninstall npm -g;${clean_cmds}"
if [ $? -eq 0 ];then
  npm -v
else
  echo "npm 安装失败，清理环境，退出安装"
  clean_env
  exit 1
fi
echo "npm安装成功!"


# 进入web源代码目录
echo "进入web源代码目录"
cd web


# 安装依赖
echo "3.安装npm依赖"
npm install
if [ $? -eq 0 ];then
  npm -v
else
  echo "npm 安装依赖失败，清理环境，退出安装"
  clean_env
  exit 1
fi
echo "安装npm依赖成功!"

# 获取本地IP
IP=`ifconfig |grep inet| sed -n '1p'|awk '{print $2}'|awk -F ':' '{print $2}'`


# 修改前端配置IP
echo "4.本地IP:$IP"
sed -i "s/127.0.0.1/$IP:80/g" ./src/common/js/ajax/baseURL.js

# 打包
echo "5.开始前端build打包..."
npm run build

clean_cmds="rm -rf node_modules/;${clean_cmds}"
if [ $? -eq 0 ];then
  echo "npm run build..."
else
  echo "npm build失败，清理环境，退出安装"
  clean_env
  exit 1
fi
echo "前端build成功!"

# 退出web目录，进入安装目录
cd ..

# 安装nginx
echo "6.安装nginx..."
sudo apt-get install -y nginx

clean_cmds="apt-get remove -y nginx;${clean_cmds}"
if [ $? -ne 0 ];then
  echo "安装nginx失败，清理环境，退出安装"
  clean_env
  exit 1
fi
echo "安装nginx成功!"

# 配置default文件
sudo cp ./default /etc/nginx/sites-available/default


# 创建前端代码文件夹
FRONT_PATH="/var/www/html/wgdw"
echo "7.创建前端代码文件夹"
if [ -d /var/www/html/wgdw ];then
	echo "创建前端代码文件夹失败，清理环境，退出安装"
	clean_env
	exit 1
fi
sudo mkdir -p ${FRONT_PATH}

# 部署web
sudo cp -r ./web/dist/*  ${FRONT_PATH}

# 安装tomcat
echo "8.安装tomcat..."
sudo apt-get install -y tomcat8
clean_cmds="apt-get remove -y tomcat8;${clean_cmds}"
if [ $? -ne 0 ];then
  echo "安装tomcat失败，清理环境，退出安装"
  clean_env
  exit 1
fi

# 安装MySQL
echo "8.安装MySQL组件，请注意安装程序需要输入MySQL root密码"
sudo apt-get install -y mysql-server mysql-client
clean_cmds="apt-get remove -y mysql-server mysql-client;${clean_cmds}"
if [ $? -ne 0 ];then
  echo "安装MySQL组件失败，清理环境，退出安装"
  clean_env
  exit 1
fi

# 创建数据仓库
echo "9.创建数据仓库"
sudo mkdir -p /var/lib/tomcat8/webapps/database

sudo mv init_tiangong_draft.sql /var/lib/tomcat8/webapps/database

# 赋权
chmod +x init_mysql.sql

# 执行SQL文件
echo "10.执行SQL文件，初始化数据库"
./init_mysql.sql


# 部署war
echo "11.部署war"
sudo mv WaterGaugeDetectorService.war /var/lib/tomcat8/webapps

# 依赖文件
echo "12.复制依赖文件"
sudo tar -xzvf libtensorflow_jni-gpu-linux-x86_64-1.9.0.tar.gz -C /usr/local/lib

# 重启服务
echo "13.重启相关服务"
systemctl restart tomcat8
systemctl restart nginx

# 结束
echo "系统环境部署完成!"
