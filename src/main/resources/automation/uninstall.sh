#!/bin/bash
# 卸载脚本
# 卸载tomcat8
echo "卸载tomcat8..."
sudo apt-get --purge remove -y tomcat8

sudo apt-get autoremove -y tomcat8

# 卸载MySQL组件
echo "卸载MySQL组件..."
sudo apt-get --purge remove -y mysql-server mysql-client

sudo apt-get autoremove -y mysql-server mysql-client

# 卸载nginx
echo "卸载nginx..."
sudo apt-get --purge remove -y nginx

sudo apt-get autoremove -y nginx

# 删除文件夹
echo "删除文件夹"
sudo rm -rf /var/www/html/wgdw

# 卸载vue
echo "卸载vue"
sudo npm uninstall vue -g

# 卸载npm
echo "卸载npm"
sudo npm uninstall npm -g

# 卸载nodejs
echo "卸载nodejs"
sudo apt-get remove -y nodejs

# 删除依赖文件
sudo rm -rf /usr/local/lib/libtensorflow*.so

echo "环境卸载完成!"
