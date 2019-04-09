#!/bin/bash
# 初始化数据库

HOSTNAME="localhost"

PORT="3306"

USERNAME="root"

PASSWORD="docker@1302"

DBNAME="tiangong_draft"


# 创建数据库SQL
create_db_sql="create database IF NOT EXISTS ${DBNAME}"

# 导入SQL文件
source_db_sql="/var/lib/tomcat8/webapps/database/init_tiangong_draft.sql"

use_db_sql="use ${DBNAME}"

# 执行SQL
echo "开始执行SQL命令..."
echo "1.CREATE DATABASE"
mysql -h${HOSTNAME} -P${PORT} -u${USERNAME} -p${PASSWORD} -e "${create_db_sql}"

if [ $? -ne 0 ]
then
 echo "CREATE DATABASES ${DBNAME} FAILED ..."
 exit 1
fi

echo "2.USE DATABASE"
mysql -h${HOSTNAME} -P${PORT} -u${USERNAME} -p${PASSWORD} -e "${use_db_sql}"

echo "3.SOURCE DB.SQL"
mysql -h${HOSTNAME} -P${PORT} -u${USERNAME} -p${PASSWORD} ${DBNAME} < ${source_db_sql}

if [ $? -ne 0 ]
then
echo "SOURCE  DB.SQL FAILED ..."
 exit 1
fi

echo "数据库初始化完成!"
