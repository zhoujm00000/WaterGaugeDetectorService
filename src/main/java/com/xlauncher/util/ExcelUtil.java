package com.xlauncher.util;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 日志导出Excel工具类
 * @date 2018-05-15
 * @author baishuailei
 */
public class ExcelUtil {

    private final static String XLS = "xls";
    private final static String XLSX = "xlsx";
    private static Logger logger  = Logger.getLogger(ExcelUtil.class);

    /**
     * 读excel文件，解析后返回
     *
     * @param file file
     */
    public static List<String[]> readExcel(MultipartFile file) throws Exception {
        //检查文件
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
        }
        return list;
    }


    /**
     * 检查文件
     *
     * @param file file
     */
    private static void checkFile(MultipartFile file) throws IOException{
        //判断文件是否存在
        if(null == file){
            logger.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if(!fileName.endsWith(XLS) && !fileName.endsWith(XLSX)){
            logger.error(fileName + "不是excel文件");
            throw new IOException(fileName + "不是excel文件");
        }
    }

    /**
     * 格式化
     *
     * @param str str
     * @return String
     */

    public static String getFormat(String str) throws Exception {
        if("null".equals(str)) {
            throw new Exception("Err.数据格式有误! 有" + str + "值!");
        } else if (" ".equals(str)){
            throw new Exception("Err.数据格式有误! 有" + str + "值!");
        } else if ("NULL".equals(str)) {
            throw new Exception("Err.数据格式有误! 有" + str + "值!");
        }
        return str;
    }

    /**
     * 获取对象
     *
     * @param file file
     * @return Workbook
     */
    private static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith(XLS)){
                try {
                    //2003
                    workbook = new HSSFWorkbook(is);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(" 1_Err." + e);
                }
            }else if(fileName.endsWith(XLSX)){
                try {
                    //2007 及2007以上
                    workbook = WorkbookFactory.create(is);
//                    workbook = new XSSFWorkbook(is);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(" 2_Err." + e);
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.error(" 3_Err." + e);
        }
        return workbook;
    }


    /**
     * 获取列值
     * @param cell cell
     * @return String
     */
    private static String getCellValue(Cell cell) throws Exception {

        String cellValue;
        if(cell == null){
            throw new Exception("Err.数据格式有误! 有" + null + "值!");
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()){
            //数字
            case Cell.CELL_TYPE_NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            //字符串
            case Cell.CELL_TYPE_STRING:
                cellValue = String.valueOf(cell.getStringCellValue());
                for (int i = 0; i < cellValue.length(); i++) {
                    if (!Character.isDigit(cellValue.charAt(i))) {
                        throw new Exception("Err-有误数据值:" + cell.getStringCellValue());
                    }
                }
                break;
            //Boolean
            case Cell.CELL_TYPE_BOOLEAN:
                throw new Exception("Err-有误数据值:" + cell.getBooleanCellValue());
            //公式
            case Cell.CELL_TYPE_FORMULA:
                throw new Exception("Err-非法字符, 有误数据值:" + cell);
            //空值
            case Cell.CELL_TYPE_BLANK:
                throw new Exception("Err-存在空值, 有误数据值:" + cell);
            //故障
            case Cell.CELL_TYPE_ERROR:
                throw new Exception("Err-非法字符, 有误数据值:" + cell.getErrorCellValue());
            default:
                throw new Exception("Err-未知类型, 有误数据值:" + cell);
        }
        return cellValue;
    }

    /**
     * 导出Excel
     *
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param wb HSSFWorkbook对象
     * @return 导出Excel
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName,String []title,String [][]values, HSSFWorkbook wb){
        // 第一步，创建一个Workbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        // 第二步，在Workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(year + "年"+ sheetName);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        // 创建一个居中格式
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        HSSFCell cell = null;
        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        //创建内容
        for(int i=0;i<values.length;i++){
            row = sheet.createRow(i + 1);
            for(int j=0;j<values[i].length;j++){
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }

}
