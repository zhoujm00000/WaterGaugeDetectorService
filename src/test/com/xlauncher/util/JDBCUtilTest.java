package com.xlauncher.util;

import com.xlauncher.entity.Predict;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/11/6 0006
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class JDBCUtilTest {
    @Autowired
    JDBCUtil jdbcUtil;
    @Autowired
    PropertiesUtil propertiesUtil;

    @Test
    public void getConnection() throws Exception {
        jdbcUtil.getConnection();
    }
    @Test
    public void server() throws Exception {
//        jdbcUtil.server(1);
        jdbcUtil.getImg();
    }

    @Test
    public void freeMaker() throws Exception {
        // 第一步：创建一个configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 第二部：设置模板文件所在路径
        configuration.setDirectoryForTemplateLoading(new File("D:\\Launcher\\MyRepository\\tiangong\\WaterGaugeDetectorService\\src\\main\\webapp\\WEB-INF\\ftl"));
        // 第三步：设置模板文件使用的字符集。一般为UTF-8
        configuration.setDefaultEncoding("UTF-8");
        // 第四步：加载一个模板，创建一个模板对象
        Template template = configuration.getTemplate("hello.ftl");
        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是map
        Map dataModel = new HashMap();
        // 向数据集中添加数据
        dataModel.put("hello","this is a freemarker test!");
        // 第六步：创建一个writer对象，一般创建FileWriter对象，指定生成的文件名
        Writer out = new FileWriter(new File("D:\\Launcher\\MyRepository\\tiangong\\WaterGaugeDetectorService\\src\\main\\webapp\\WEB-INF\\ftl\\hello.html"));
        // 第七步：调用模板对象的process方法输出文件
        template.process(dataModel,out);
        // 第八步：关闭流
        out.close();
    }

    @Test
    public void backServer() throws Exception {
        Predict predict = new Predict();
        predict.setCollectTime("2018-11-17 03:32:41");
        predict.setSid("4861665156");
        predict.setChannel(2);
        predict.setIdentify((float) 0);
        predict.setStatus(1);
        jdbcUtil.backServer(predict);


    }
    @Test
    public void confirm() throws Exception {
        Map<String, Object> map = new HashMap<>(1);
        map.put("ip", "db.dcxxsoft.xyz");
        map.put("port","14336");
        map.put("name", "WaterResource");
        map.put("password", "WaterResource");
        map.put("database", "WaterResource");
        System.out.println("confirm:" + jdbcUtil.confirm(map));
    }

}