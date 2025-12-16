package com.example;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class MainApp {
    static FileWriter logWriter;
    static String homepath="";
    public static void main(String[] args) throws IOException, URISyntaxException {
        // 创建Scanner对象，System.in表示标准输入流
//        Scanner scanner = new Scanner(System.in);

//        System.out.print("输入：做电表：db，生成设备和设备类导入表：sb，生成设备类导入表：sbl ");
//        String select = scanner.nextLine();
//        // 关闭Scanner
//        scanner.close();
//
//        if(select.equals("sbl")){
//
//        }else if(select.equals("sb")){
//
//        }else {
//
//        }


        // 获取当前类所在的JAR包路径
        String jarPath = MainApp.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();

        // 如果是Windows系统，可能需要处理路径开头的斜杠
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            if (jarPath.startsWith("/")) {
                jarPath = jarPath.substring(1);
            }
        }

        // 获取JAR包所在的目录
        File jarFile = new File(jarPath);
        String jarDirectory = jarFile.getParent();

        int lastzx=jarPath.lastIndexOf("/");
        homepath=jarPath.substring(0,lastzx+1);
        System.out.println(homepath);

        String zb=homepath+"zb.xlsx";
        String logFile=homepath+"log.txt";
        String bzhmcConfig=homepath+"configPid.txt";
        String ipysConfig=homepath+"configIp.txt";
//        String config=homepath+"config.txt";

        Path path = Paths.get(logFile);
        if (Files.exists(path)) {
            Files.delete(path);
        }

        Map<String,String> bzhmcConfigMap= getMap(bzhmcConfig);
        Map<String,String> ipysConfigMap= getMap(ipysConfig);
//        Map<String,String> configMap = getMap(config);




        logWriter = new FileWriter(logFile);
        readExcelFile(zb,0,bzhmcConfigMap, ipysConfigMap);
        logWriter.close();

    }

    public static String readAllCharactersOneByOne(Reader reader) throws IOException {
        StringBuilder content = new StringBuilder();
        int nextChar;
        while ((nextChar = reader.read()) != -1) {
            content.append((char) nextChar);
        }
        return String.valueOf(content);
    }
    public static Map<String,String> getMap(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        String configStr=readAllCharactersOneByOne(fileReader);
        String[] strArr=configStr.split("\n");
        Map<String,String> bzhmcConfigMap=new HashMap<>();
        for (String s : strArr) {
            bzhmcConfigMap.put(s.split(":")[0],s.split(":")[1]);
        }
        fileReader.close();
        return bzhmcConfigMap;
    }

    /**
     * 读取 Excel 文件
     */
    public static void readExcelFile(String filePath,int type, Map<String, String> bzhmcConfigMap, Map<String, String> ipysConfigMap) throws IOException {
        FileInputStream inputStream = new FileInputStream(filePath);
        Set<Integer> modbusSet = new HashSet<>();
        // 根据文件扩展名创建工作簿
        Workbook workbook;
        if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (filePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("不支持的文件格式");
        }

        if (type==0) {
            Sheet sheet = workbook.getSheetAt(0);
            logWriter.write("---------------生成设备和设备类导入表-------------------");
            List<Object[]> sbldata  = new ArrayList<>();//新建excel使用
            List<Object[]> sbdata  = new ArrayList<>();//新建excel使用
            String sblbsqz="lLbSAp76Zp";
            Map<String,String> quchongMap= new HashMap<>();
            Set<String> ipquchong=new HashSet<>();
            for (Row row : sheet) {
                if (row.getRowNum()<1){
                    continue;
                }

                String xh= String.valueOf(row.getCell(0)).replace(".0","");//序号(设备标准化名称)

                String zm= String.valueOf(row.getCell(1));//站名（设备名）
                String txxy= String.valueOf(row.getCell(2));//通讯协议
                String ipdz= String.valueOf(row.getCell(3));//IP地址
                String sblmc= String.valueOf(row.getCell(10));//设备类名称（点表名称）
                String txlx= "1";//直连
                String sblbzhmc= "substation";//标准化名称
                String txxy2="";
                if(txxy.contains("S7")){
                    txxy2="5";
                }else if(txxy.contains("Modbus")){
                    txxy2="3";
                }else {
                    logWriter.write("\n异常 通讯协议不是S7或modbus：总表 "+row.getRowNum()+" 行 "+txxy);
                    continue;
                }
                String sblbsqz2=sblbsqz+row.getRowNum();
                if (quchongMap.get(sblmc)==null) {
                    quchongMap.put(sblmc,sblbsqz2);
                    Object[] objects1= new Object[20];
                    objects1[1]=sblbsqz2;
                    objects1[2]=sblmc;
                    objects1[3]=txxy2;
                    objects1[4]=txlx;
                    objects1[14]=sblbzhmc;
                    sbldata.add(objects1);
                }

                //设备
                if (ipquchong.contains(ipdz)) {
                    logWriter.write("\n异常 设备IP地址重复已跳过：总表 "+row.getRowNum()+" 行 "+ipdz);
                    continue;
                }
                ipquchong.add(ipdz);
                Object[] objects2= new Object[14];
                objects2[1]=xh;
                objects2[2]=zm;
                objects2[6]=quchongMap.get(sblmc);
                String sbbzhmc="";
                if (bzhmcConfigMap.get(zm.replace(" ",""))!=null) {
                    sbbzhmc=bzhmcConfigMap.get(zm);
                }
                objects2[11]=sbbzhmc;
                String jscs="";

                String ipdzhdkh="";
                if (ipysConfigMap.get(ipdz)!=null) {
                    ipdzhdkh=ipysConfigMap.get(ipdz).replace("-",":");
                }

                if(txxy.contains("S7")){
                    jscs="server:("+ipdzhdkh+");rack:0;slot:1;type:"+txxy;

                }else if(txxy.contains("Modbus")){
                    jscs="slaveUrl:(tcp://"+ipdzhdkh+");slaveId:1;proxy:modbus";
                }

                objects2[12]=jscs;
                sbdata.add(objects2);

            }
            createExcelFile(homepath+"设备类导入表.xlsx",sbldata,"sbl");
            createExcelFile(homepath+"设备导入表.xlsx",sbdata,"sb");
            readExcelFile(filePath, 1, bzhmcConfigMap, ipysConfigMap);
        }else{

            logWriter.write("\n---------------生成点表-------------------");
            for  (int i = 1; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表

                Sheet sheet = workbook.getSheetAt(i);
                String sheetName=sheet.getSheetName();

                List<Object[]> data  = new ArrayList<>();//新建excel使用
                Set<String> biaoshiSet = new HashSet<>();//属性标识重复跳过
                Map<String,Integer> bzhmcMap=new HashMap<>();//标准化名称重复改名
                logWriter.write("\n生成点表:"+sheetName);
                int flag=0;
                for (Row row : sheet) {
                    if (row.getRowNum()<2){
                        continue;
                    }

                    String cell1= String.valueOf(row.getCell(1));//点名称
                    String cell3= String.valueOf(row.getCell(3));//modbus地址
                    String cell4= String.valueOf(row.getCell(4));//读写
                    String cell5= String.valueOf(row.getCell(5));//数据类型
                    String cell6= String.valueOf(row.getCell(6));//系数
                    String cell7= String.valueOf(row.getCell(7));//单位

                    if (!cell3.contains("Holding_register")&&!cell3.contains("DB")) {
                        flag=1;
                        continue;
                    }


                    //重复的modbus地址跳过并记录
                    if(biaoshiSet.contains(cell3)){
                        logWriter.write("\n异常:"+sheetName+" "+row.getRowNum()+" 行modbus地址重复已跳过："+cell3);
                        continue;
                    }
                    biaoshiSet.add(cell3);
                    //属性标识
                    String result="";
                    if (cell3.substring(0,2).contains("DB")) {
                        String sss=cell3;
                        String thf="";
                        if(row.getCell(5).toString().contains("布")){
                            thf="X";
                        } else if (row.getCell(5).toString().contains("浮")) {
                            thf="R";
                        }else{
                            thf="I";
                        }
                        if (sss.contains("DBD")){
                            result=sss.replace("DBD",thf);
                        }else if(sss.contains("DBW")){
                            result=sss.replace("DBW",thf);
                        }else {

                            logWriter.write("\n异常:"+sheetName+" "+row.getRowNum()+" 行 mudbus地址异常跳过："+cell3);
                            continue;
                        }


                    }else{
                        Pattern pattern1 = Pattern.compile("\\d+(\\.\\d+)?$");
                        Matcher matcher1 = pattern1.matcher(cell3);
                        if(matcher1.find()) {
                            result = matcher1.group();
                            String resulthz = "";
                            if (result.contains(".")) {
                                String[] resultArr = result.split("\\.");
                                result = resultArr[0];
                                resulthz = "#" + resultArr[1];
                            }
                            if (4 - result.length() == 1) {
                                result = "0" + result;
                            } else if (4 - result.length() == 2) {
                                result = "00" + result;
                            } else if (4 - result.length() == 3) {
                                result = "000" + result;
                            }

                            if (row.getCell(5).toString().contains("整") || row.getCell(5).toString().contains("浮点")) {
                                if (row.getCell(5).toString().contains("整形")) {
                                    result = result + ":UINT";
                                } else {
                                    result = result + ":REAL";
                                }
                                result = "4x" + result;
                            } else if (row.getCell(5).toString().contains("布尔")) {
                                result = result + ":BOOL";
                                Pattern pattern2 = Pattern.compile("(启动|停止|启|停|启停|停启)$");
                                Matcher matcher2 = pattern2.matcher(cell1);
                                if (matcher2.find()) {
                                    result = "0x" + result;
                                } else {
                                    result = "1x" + result;
                                }
                            }
                            result = result + resulthz;
                        }else{

                            logWriter.write("\n异常:"+sheetName+" "+row.getRowNum()+" 行 mudbus地址异常跳过："+cell3);
                            continue;
                        }
                    }



                        //属性名称
                        String attrName=cell1;

                        //标准化名称
                        String bzhname=cell1;
                        String bzhmc="";
                        String bzhmchz="";
                        if(bzhname.contains("#")){
                            int starNUm=2;
                            if(!bzhname.contains("机组")){
                                starNUm=0;
                            }
                            bzhmchz="_"+bzhname.substring(starNUm,bzhname.indexOf("#"));
                            bzhname=bzhname.substring(bzhname.indexOf("#")+1,bzhname.length());
                        } else if (bzhname.contains("_")) {
                            int starNUm=2;
                            if(!bzhname.contains("机组")){
                                starNUm=0;
                            }
                            bzhmchz="_"+bzhname.substring(starNUm,bzhname.indexOf("_"));
                            bzhname=bzhname.substring(bzhname.indexOf("_")+1,bzhname.length());
                        }else if (bzhname.contains("机组")) {
                            Pattern pattern2 = Pattern.compile("机组\\d+");
                            Matcher matcher2 = pattern2.matcher(bzhname);
                            String bzhnamejznum="";
                            if(matcher2.find()){
                                bzhnamejznum=matcher2.group();
                            }
                            bzhmchz="_"+bzhnamejznum.substring(2,bzhnamejznum.length());
                            bzhname=bzhname.replace(bzhnamejznum,"");
                        }
                        if(bzhname.contains("：")){
                            bzhname=bzhname.substring(0,bzhname.indexOf("："));
                        }


                        bzhmc=Bzhmcsc.bzhmcsc(bzhname,bzhmc,bzhmchz);

                        //读写模式
                        String dxms=cell4.equals("只读")?"1":"2";
                        //单位
                        String danwei = cell7;
                        //数据类型
                        String sjlx="";
                        if(cell5.contains("整")){
                            sjlx="1";
                        }else if(cell5.contains("浮点")){
                            sjlx="3";
                        }else if(cell5.contains("字符")){
                            sjlx="4";
                        }else if(cell5.contains("日期")){
                            sjlx="5";
                        }else if(cell5.contains("布尔")){
                            sjlx="6";
                        }



                        Object[] objects= new Object[15];
                        objects[1]=result;
                        objects[2]=attrName;
                        if (bzhmcMap.get(bzhmc) != null) {
                            int num=bzhmcMap.get(bzhmc);
                            String bzhmc2=bzhmc;
                            bzhmc=bzhmc+"_"+num;
                            num++;
                            bzhmcMap.put(bzhmc2, num);
                            logWriter.write("\n异常:"+sheetName+" "+row.getRowNum()+" 行 标准化名称重复 +1处理："+bzhmc+" "+attrName);
                        }else {
                            bzhmcMap.put(bzhmc, 2);
                        }

                    //计算公式
                        objects[3]=bzhmc;
                        String jsgs=null;
                        if (!cell6.equals("1")&&!cell6.equals("1.0")){
                            jsgs="${"+bzhmc+"}*"+cell6;
                        }

                        objects[4]=dxms;
                        objects[5]=danwei;
                        objects[7]=sjlx;
                    //上报公式
                        objects[6]=jsgs;
                    //下发公式
                        objects[12]="U";
                        if ("2".equals(dxms)) {
                            objects[14]=jsgs;
                        }
                        data.add(objects);
                    }
                if (flag==1){
                    logWriter.write("\n异常:"+sheetName+" 内有需要人工查询的物模型属性");
                }
                File dbfile=new File(homepath+"modbusdbs/"+sheetName+".xlsx");
                if (!dbfile.exists()) {
                    try {
                        if (!dbfile.getParentFile().exists()) {
                            dbfile.getParentFile().mkdirs();
                        }
                        dbfile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!data.isEmpty()){
                    createExcelFile(dbfile.getPath(),data,"db");
                }
            }
        }
        workbook.close();
        inputStream.close();
    }


    /**
     * 创建新的 Excel 文件 (.xlsx 格式)
     */
    public static void createExcelFile(String filePath,List<Object[]> data,String type) throws IOException {
        // 创建工作簿 (.xlsx)
        Workbook workbook = new XSSFWorkbook();

        // 创建工作表
        Sheet sheet;
        if ("db".equals(type)) {
            sheet = workbook.createSheet("设备类属性信息");
        } else if ("sb".equals(type)) {
            sheet = workbook.createSheet("设备信息");
        }else {
            sheet = workbook.createSheet("设备模板信息");
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
        }else{
            headers = new String[]{"设备类ID","设备类标识","设备类名称","通讯协议","通讯类型","继承父模板ID","继承父模板标识","时间格式","jsonQuery","被组合设备类ID","被组合设备类标识","属性ID","属性标识","设备类分组ID","标准化名称","操作标记","认证方式","clientId","username","password"};
        }
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

//        List<Object[]> data = Arrays.asList(
//                new Object[]{1, "张三", 28, "技术部", new Date(), 15000.50},
//                new Object[]{2, "李四", 32, "市场部", new Date(), 12000.75},
//                new Object[]{3, "王五", 25, "人事部", new Date(), 10000.00}
//        );

        int rowNum = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            for (int j = 0; j < rowData.length; j++) {
                if (rowData[j] != null) {
                    row.createCell(j).setCellValue((String) rowData[j]);
                }
            }
//            row.createCell(1).setCellValue((String) rowData[1]);
//            row.createCell(2).setCellValue((String) rowData[2]);
//            row.createCell(3).setCellValue((String) rowData[3]);
//            row.createCell(4).setCellValue((String) rowData[4]);
//            row.createCell(5).setCellValue((String) rowData[5]);
//            row.createCell(7).setCellValue((String) rowData[7]);
//            row.createCell(14).setCellValue((String) rowData[14]);

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
     * 更新 Excel 文件
     */
    public static void updateExcelFile(String filePath) throws IOException {
        FileInputStream inputStream = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(inputStream);

        // 获取工作表
        Sheet sheet = workbook.getSheetAt(0);

        // 在末尾添加新行
        int lastRowNum = sheet.getLastRowNum();
        Row newRow = sheet.createRow(lastRowNum + 1);
        newRow.createCell(0).setCellValue(4);
        newRow.createCell(1).setCellValue("赵六");
        newRow.createCell(2).setCellValue(30);
        newRow.createCell(3).setCellValue("财务部");
        newRow.createCell(4).setCellValue(new Date());
        newRow.createCell(5).setCellValue(13500.00);

        // 更新现有单元格
        Row row = sheet.getRow(2); // 第三行
        if (row != null) {
            Cell salaryCell = row.getCell(5);
            if (salaryCell != null) {
                salaryCell.setCellValue(11000.50);
            }
        }

        // 写入文件
        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);

        workbook.close();
        inputStream.close();
        outputStream.close();

        System.out.println("\nExcel 文件更新成功!");
    }

    /**
     * 从 Excel 中提取数据到 List
     */
    public static List<Map<String, Object>> extractDataFromExcel(String filePath) throws IOException {
        List<Map<String, Object>> dataList = new ArrayList<>();

        FileInputStream inputStream = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // 获取标题行
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }

        // 读取数据行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Map<String, Object> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                rowData.put(headers.get(j), cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    rowData.put(headers.get(j), cell.getDateCellValue());
                                } else {
                                    rowData.put(headers.get(j), cell.getNumericCellValue());
                                }
                                break;
                            case BOOLEAN:
                                rowData.put(headers.get(j), cell.getBooleanCellValue());
                                break;
                            default:
                                rowData.put(headers.get(j), "");
                        }
                    } else {
                        rowData.put(headers.get(j), "");
                    }
                }
                dataList.add(rowData);
            }
        }

        workbook.close();
        inputStream.close();

        return dataList;
    }

    /**
     * 创建带有公式的 Excel
     */
    public static void createExcelWithFormula(String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("销售数据");

        // 创建数据
        Object[][] data = {
                {"产品", "一月", "二月", "三月", "合计"},
                {"产品A", 1000, 1500, 1200, 0},
                {"产品B", 800, 900, 950, 0},
                {"产品C", 1200, 1100, 1300, 0},
                {"总计", 0, 0, 0, 0}
        };

        // 填充数据
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                if (data[i][j] instanceof String) {
                    cell.setCellValue((String) data[i][j]);
                } else {
                    cell.setCellValue((Double) data[i][j]);
                }
            }
        }

        // 添加公式
        for (int i = 1; i <= 3; i++) {
            Row row = sheet.getRow(i);
            Cell totalCell = row.createCell(4);
            totalCell.setCellFormula(String.format("SUM(B%d:D%d)", i + 1, i + 1));
        }

        // 添加总计行公式
        Row totalRow = sheet.getRow(4);
        for (int i = 1; i <= 4; i++) {
            Cell cell = totalRow.createCell(i);
            if (i == 4) {
                cell.setCellFormula("SUM(E2:E4)");
            } else {
                cell.setCellFormula(String.format("SUM(%c2:%c4)", 'A' + i, 'A' + i));
            }
        }

        // 写入文件
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }

        workbook.close();
        System.out.println("带公式的 Excel 文件创建成功!");
    }
}