package com.xlauncher.web;

import com.xlauncher.entity.Length;
import com.xlauncher.service.LengthService;
import com.xlauncher.util.DatetimeUtil;
import com.xlauncher.util.ExcelUtil;
import com.xlauncher.util.PropertiesUtil;
import com.xlauncher.util.userlogin.ActiveUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/25 0025
 * @Desc :
 **/
@RestController
@RequestMapping(value = "/length")
public class LengthController {
    private final LengthService lengthService;
    private final PropertiesUtil propertiesUtil;
    private final ActiveUtil activeUtil;
    private final static String XLS = "xls";
    private final static String XLSX = "xlsx";
    private static Logger logger = Logger.getLogger(LengthController.class);

    @Autowired
    public LengthController(LengthService lengthService, PropertiesUtil propertiesUtil, ActiveUtil activeUtil) {
        this.lengthService = lengthService;
        this.propertiesUtil = propertiesUtil;
        this.activeUtil = activeUtil;
    }

    /**
     * 手工录入
     *
     * @param length 长度信息
     * @return int
     */
    @PostMapping("")
    public int insertLength(@RequestBody @Param("length") Length length
            , @RequestHeader("token") String token){
        logger.info("Excel导入或手工录入." + length);
        return lengthService.insertLength(length);
    }

    /**
     * 删除配置
     *
     * @param id 编号
     * @return int
     */
    @DeleteMapping("/{id}")
    public int deleteLength(@PathVariable @Param("id") int id
            , @RequestHeader("token") String token){
        return lengthService.deleteLength(id);
    }

    /**
     * 可修改水尺长度
     *
     * @param length 长度信息
     * @return int
     */
    @PutMapping("")
    public Map<String, Object> updateLength(@RequestBody @Param("length") Length length
            , @RequestHeader("token") String token, HttpServletRequest request, HttpServletResponse response){
//        activeUtil.check(request, response);
        logger.info("可修改水尺长度." + length);
        Map<String, Object> map = new HashMap<>(1);
        int result = lengthService.updateLength(length);
        if (result == 1) {
            map.put("result", result);
            map.put("code", 200);
        } else {
            map.put("result", result);
            map.put("code", 400);
        }
        return map;
    }

    /**
     * 得到水尺总长
     *
     * @param sid sid
     * @param channel
     * @return int
     */
    @GetMapping("/{page}")
    public Map<String, Object> getLengthList(@RequestParam("sid") String sid, @RequestParam("channel") int channel, @PathVariable("page") int page
            , @RequestHeader("token") String token, HttpServletRequest request,HttpServletResponse response){
//        activeUtil.check(request, response);
        Map<String, Object> map = new HashMap<>(1);
        List<Length> lengthList = lengthService.getLengthList(sid,channel, (page-1)*10);
        map.put("lengthList", lengthList);
        return map;
    }

    /**
     * count
     *
     * @param sid
     * @return int
     */
    @GetMapping("/count")
    public Map<String, Object> getCount(@RequestParam("sid") String sid, @RequestParam("channel") int channel){
        Map<String, Object> map = new HashMap<>(1);
        int count = lengthService.count(sid,channel);
        map.put("count", count);
        return map;
    }

    /**
     * 提供下载的模板
     *
     * @param response
     * @throws UnsupportedEncodingException
     */
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response) throws UnsupportedEncodingException {
        String filename = "template.xlsx";
        String path = propertiesUtil.getPath();

        try {
            InputStream is = new FileInputStream(path + filename);
            // 设置response参数
            response.reset();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                            + new String(("水尺模板" + ".xlsx").getBytes(), "iso-8859-1"));
            // 循环取出流中的数据
            byte[] b = new byte[100];
            int len;
            ServletOutputStream out = response.getOutputStream();
            while ((len = is.read(b)) > 0) {
                out.write(b, 0, len);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Excel导出
     *
     * @param sid
     * @param channel
     * @return int
     */
    @GetMapping(value = "/export", produces = {"application/octet-stream;charset=UTF-8"})
    public void exportExcel(@RequestParam("sid") String sid, @RequestParam("channel") int channel
            , HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
//        activeUtil.check(request, response);
        //文件名
        String fileName = DatetimeUtil.getDate(DatetimeUtil.getDate(System.currentTimeMillis())) + "length";
        //sheet名
        String sheetName = "水尺总长度录入";
        //标题
        String[] title = new String[]{"站点编号SID", "摄像头编号Channel", "水尺总长度Height(单位:cm)"};
        // 获取数据
        List<Length> lists = this.lengthService.getLengthListForExcel(sid,channel);
        String[][] values = new String[lists.size()][];
        for (int i=0; i<lists.size(); i++) {
            values[i] = new String[title.length];
            Length length = lists.get(i);
            values[i][0] = length.getSid();
            values[i][1] = String.valueOf(length.getChannel());
            values[i][2] = String.valueOf(length.getHeight());
        }
        //创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, values, null);
        downSetResponse(wb, response, fileName);
    }

    /**
     * 导入Excel
     *
     * @param excelFile
     * @param response
     * @return
     */
    @PostMapping(value = "/import")
    public Map<String, Object> importExcel(@RequestBody MultipartFile excelFile
            , HttpServletRequest request,HttpServletResponse response) {
//        activeUtil.check(request, response);
        Map<String, Object> map = new HashMap<>(1);
        List<String[]> lists;
        if (excelFile != null) {
            // 文件原名称
            String fileName = excelFile.getOriginalFilename();
            // 文件类型
            String type= fileName.contains(".") ?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
            if (type != null) {
                if (XLS.equals(type.toLowerCase()) || XLSX.equals(type.toLowerCase())) {
                    try {
                        lists = ExcelUtil.readExcel(excelFile);
                        if (lists == null) {
                            map.put("Err.", "文件有误,重新上传!");
                            response.setStatus(415);
                        } else {
                            logger.info("___length lists." + lists.size());
                            int status = 0;
                            for (String[] list : lists) {
                                Length length = new Length();
                                try {
                                    length.setSid(ExcelUtil.getFormat(list[0]));
                                    length.setChannel(Integer.valueOf(ExcelUtil.getFormat(list[1])));
                                    length.setHeight(Integer.valueOf(ExcelUtil.getFormat(list[2])));
                                    status = lengthService.insertLength(length);
                                    logger.info("___status." + status);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    logger.info("Err." + e);
                                    map.put("code", 405);
                                    map.put("msg", "数据格式有误!" + e.getMessage());
                                }
                            }
                            if (status != 0) {
                                map.put("code", 200);
                                map.put("msg", fileName + "上传成功!");
                            } else {
                                map.put("code", 400);
                                map.put("msg", "保存数据失败! status:" + status);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("Err." + e);
                        map.put("code", 405);
                        map.put("msg", "数据格式有误!"+ e.toString().split("-")[1]);
                    }
                } else {
                    map.put("Err.", "文件类型有误,重新上传!");
                    response.setStatus(415);
                }
            } else {
                map.put("Err.", "无法识别的文件类型,重新上传!");
                response.setStatus(415);
            }
        } else {
            map.put("Err", "无法解析的文件!");
            response.setStatus(415);
        }
        return map;
    }


    /**
     * 提供下载的接口
     *
     * @param wb wb
     * @param response 设置返回的类型
     * @param fileName 文件名
     */
    private void downSetResponse(HSSFWorkbook wb, HttpServletResponse response, String fileName)
            throws UnsupportedEncodingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数
        response.reset();
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename="
                .concat(String.valueOf(URLEncoder.encode(fileName + ".xlsx", "UTF-8"))));
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            if (out != null) {
                bos = new BufferedOutputStream(out);
            }
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                if (bos != null) {
                    bos.write(buff, 0, bytesRead);
                }
            }
        } catch (final IOException e) {
            try {
                throw e;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
