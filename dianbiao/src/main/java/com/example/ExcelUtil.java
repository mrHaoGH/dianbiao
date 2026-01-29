package com.example;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtil {
    public static Workbook  getWorkbook(String filePath) throws IOException {
        FileInputStream inputStream = new FileInputStream(filePath);
        // 根据文件扩展名创建工作簿
        Workbook workbook;
        if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (filePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("不支持的文件格式");
        }
        inputStream.close();
        return workbook;
    }
    /**
     * 创建新的 Excel 文件 (.xlsx 格式)
     */
    public static void createExcelFile(String filePath, List<Object[]> data, String type, List<String> list, List<CellType[]> celltypeList, Map<String, String> pkmap, Map<Integer, String> mobanteshucl_gd) throws IOException {
        // 创建工作簿 (.xlsx)
        Workbook workbook = new XSSFWorkbook();
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        // 创建工作表
        Sheet sheet = null;
        if ("db".equals(type)) {
            sheet = workbook.createSheet("设备类属性信息");
        } else if ("sb".equals(type)) {
            sheet = workbook.createSheet("设备信息");
        }else if("sbl".equals(type)){
            sheet = workbook.createSheet("设备模板信息");
        }else if("test".equals(type)){
            sheet = workbook.createSheet("test");
        }else if("yy".equals(type)){
            sheet = workbook.createSheet("yy");
        }


        // 创建标题行样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = new String[0];
        if ("db".equals(type)) {
            headers = new String[]{"属性ID", "属性标识", "属性名称", "标准化名称", "读写模式", "单位", "计算公式", "数据类型", "排列顺序", "模式", "默认值", "时间格式", "操作标识", "数据上报", "控制计算公式"};
        } else if ("sb".equals(type)) {
            headers = new String[]{"设备ID","设备标识","设备名称","位置信息ID","部门信息ID","设备类ID","设备类标识","网关设备ID","网关设备标识","被组合设备ID","被组合设备标识","标准化名称","技术参数","操作标记"};
        }else if("sbl".equals(type)){
            headers = new String[]{"设备类ID","设备类标识","设备类名称","通讯协议","通讯类型","继承父模板ID","继承父模板标识","时间格式","jsonQuery","被组合设备类ID","被组合设备类标识","属性ID","属性标识","设备类分组ID","标准化名称","操作标记","认证方式","clientId","username","password"};
        }else if("test".equals(type)){
            headers = new String[]{"一次供温","一次回温","一次供压","一次回压","压差","一次瞬时供水流量","一次瞬时回水流量","一次瞬时流量",
                    "一次瞬时供水热量","一次瞬时回水热量","一次瞬时热量","累计供水热量","累计回水热量","累计热量","累计供水流量","累计回水流量","累计流量",
                    "二次1区供压","二次1区回压","二次1区供温","二次1区设定供温","二次1区回温","二次供回水压差","二次1区瞬时流量","二次1区瞬时热量","二次1区累计热量","二次1区累计流量",
                    "调节阀开度","调节阀开度给定","调节阀手自动","循环泵频率","循环泵频率给定","循环泵频率反馈","补水泵频率","补水泵频率给定",
                    "补水泵频率反馈","循环泵电流","补水泵电流"};
        }else if("yy".equals(type)){
            headers = list.toArray(new String[list.size()]);
        }
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        int m=0;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            CellType[] cellTypes= celltypeList.get(m);
            for (int j = 0; j < rowData.length; j++) {




//                    Pattern pattern1 = Pattern.compile("^[+-]?(\\d|([1-9]\\d+))(\\.\\d+)?$");
//                    Matcher matcher1 = pattern1.matcher(numStr);
//
//                    if(matcher1.find()) {
//                        String str=matcher1.group();
//
//                        BigDecimal bigDecimal = new BigDecimal(str);
//                        double ans_2 = bigDecimal.setScale(5, RoundingMode.HALF_UP).doubleValue();
//
//                        float f= Float.parseFloat(str);
//                        row.createCell(j).setCellValue(ans_2);
//                    }else{
//                        row.createCell(j).setCellValue(numStr);
//                    }
//                    row.createCell(j,cellTypes[j]==null?CellType.STRING:cellTypes[j]).setCellValue(numStr);

                if (rowData[j] != null) {
                    String numStr= rowData[j].toString();
                    row.createCell(j,cellTypes[j]==null?CellType.STRING:cellTypes[j]).setCellValue(numStr);
                }
            }
            m++;
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 写入文件
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }

        // 关闭工作簿
        workbook.close();

        System.out.println("Excel 文件创建成功: " + filePath);
    }
    /**
     * 读取计算公式
     */
    public void readExcelTest(String filePath) throws Exception {
        Workbook workbook=getWorkbook(filePath);

        // 拿到计算公式
        FormulaEvaluator formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);

        if (workbook != null) {
            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet != null) {
                // 拿到第二行第一列，把值从100改为200
                Row row2 = sheet.getRow(1);
                Cell cell21 = row2.getCell(0);
                cell21.setCellValue(200);

                // 获取第五行
                Row row = sheet.getRow(4);

                if (row != null) {
                    // 获取第一列
                    Cell cell = row.getCell(0);

                    // 拿到数据类型
                    CellType type = cell.getCellType();
                    switch (type) {
                        case CellType.FORMULA:
                            String formula = cell.getCellFormula();
                            System.out.println("计算公式为：" + formula);

                            // 进行计算并拿到值
                            CellValue value = formulaEvaluator.evaluate(cell);
                            // 将值转化成字符串
                            String format = value.formatAsString();
                            System.out.println("值为：" + format);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 获取文件列表
     * @param fileInput
     * @param allFileList
     */
    public static void getAllFile(File fileInput, List<File> allFileList) {
        // 获取文件列表
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                // 递归处理文件夹
                // 如果不想统计子文件夹则可以将下一行注释掉
                getAllFile(file, allFileList);
            } else {
                // 如果是文件则将其加入到文件数组中
                allFileList.add(file);
            }
        }
    }
    /**
     * 替换字符串中符合"一个或两个大写字母+整数"模式的数字部分
     * @param input 原始字符串
     * @param replacement 要替换成的整数
     * @return 替换后的字符串
     */
    public static String replacePatternNumbers(String input, int replacement) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 正则表达式：匹配1-2个大写字母后跟一个或多个数字
        // ([A-Z]{1,2}) 匹配1-2个大写字母（第1组）
        // (\d+) 匹配一个或多个数字（第2组）
        Pattern pattern = Pattern.compile("([A-Z]{1,2})(\\d+)");
        Matcher matcher = pattern.matcher(input);

        // 使用StringBuilder构建结果
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            // 添加匹配前的部分
            result.append(input, lastEnd, matcher.start());

            // 获取匹配到的字母部分
            String letters = matcher.group(1);

            // 构建替换后的字符串：字母 + 新的数字
            result.append(letters).append(replacement);

            // 更新最后处理的位置
            lastEnd = matcher.end();
        }

        // 添加剩余部分
        if (lastEnd < input.length()) {
            result.append(input, lastEnd, input.length());
        }

        return result.toString();
    }
    /**
     * 获取合并单元格的值
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static String getMergedRegionValue(Sheet sheet ,int row , int column){
        int sheetMergeCount = sheet.getNumMergedRegions();

        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if(row >= firstRow && row <= lastRow){

                if(column >= firstColumn && column <= lastColumn){
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell) ;
                }
            }
        }

        return null ;
    }
    /**
     * 获取单元格的值
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell){

        if(cell == null) return "";

        if(cell.getCellType() == CellType.STRING){

            return cell.getStringCellValue();

        }else if(cell.getCellType() == CellType.BOOLEAN){

            return String.valueOf(cell.getBooleanCellValue());

        }else if(cell.getCellType() == CellType.FORMULA){

            return cell.getCellFormula() ;

        }else if(cell.getCellType() == CellType.NUMERIC){

            return String.valueOf(cell.getNumericCellValue());

        }
        return "";
    }
}
