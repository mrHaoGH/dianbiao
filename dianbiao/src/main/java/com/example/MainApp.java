package com.example;


import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.print.DocFlavor;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class MainApp {
    static FileWriter logWriter;
    static String homepath = "";

    public static void main(String[] args) throws IOException, URISyntaxException {
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

        int lastzx = jarPath.lastIndexOf("/");
        homepath = jarPath.substring(0, lastzx + 1);
        homepath = "D:/test2/";

//        viscadaStart();
        yongyouStart();
    }

    /**
     * 用友表处理
     */
    public static void yongyouStart() throws IOException {

        //读取对照表
        Workbook workbook = ExcelUtil.getWorkbook(homepath + "yongyouSetExcel.xlsx");
        Sheet sheet = workbook.getSheetAt(0);
        List<String> mobanList = new ArrayList<>();

        Map<String, String> pkmap=getMap(homepath+"pk.txt");
        Map<String, String> dbgbmap=getMap(homepath+"dbgb.txt");

        List<String> zzkp=new ArrayList<>();
        zzkp.add("黑龙江省建兴勘察工程有限公司");
        zzkp.add("黑龙江省第一水文地质工程地质勘察院有限公司");
        zzkp.add("黑龙江省水文地质工程地质勘察院有限公司");
        zzkp.add("黑龙江迅恒地质环境工程咨询有限公司");
        zzkp.add("黑龙江省宏泰矿业开发有限公司");
        Map<Integer,String> mobanteshucl_gd = new HashMap<>();

        Map<Integer,CellType> mobanteshucl_gd_cellType = new HashMap<>();
        Map<String,Integer> jsyn= new HashMap<>();

        Map<String,Integer> mapXy2Index = new HashMap<>();
        Map<Integer,String> mapXy2Gs = new HashMap<>();
        Row mbrow = sheet.getRow(0);
        Row xyrow = sheet.getRow(1);
        Row gsrow = sheet.getRow(2);
        int index=0;
        while(mbrow.getCell(index)!=null) {
            String mbStr = mbrow.getCell(index).getStringCellValue();
            Cell xycell = xyrow.getCell(index);
            if (xycell != null) {
                String xyStr = xyrow.getCell(index).getStringCellValue();
                mapXy2Index.put(xyStr, index);

            }

            if(gsrow.getCell(index)!=null) {
                if(gsrow.getCell(index).getCellType()==CellType.FORMULA){
                Cell gsCall=gsrow.getCell(index);
                String formula = gsCall.getCellFormula();

                    mapXy2Gs.put(index,formula);
                }else if(gsrow.getCell(index).getCellType()!=CellType.BLANK){
                    Cell fkCall=gsrow.getCell(index);
                    String cellStr= String.valueOf(fkCall);
                    mobanteshucl_gd.put(index,cellStr);
                    mobanteshucl_gd_cellType.put(index,gsrow.getCell(index).getCellType());
                }
            }

            mobanList.add(mbStr);


            index++;
        }
        int size=mobanList.size();
        String basePath = homepath+"dcl";
        File dir = new File(basePath);

        List<File> allFileList = new ArrayList<>();

        // 判断文件夹是否存在
        if (!dir.exists()) {
            System.out.println("目录不存在");
            return;
        }
        ExcelUtil.getAllFile(dir, allFileList);
        for (File file : allFileList) {
            Workbook workbook2 = ExcelUtil.getWorkbook(file.getPath());
            // 拿到计算公式
            FormulaEvaluator formulaEvaluator = workbook2.getCreationHelper().createFormulaEvaluator();
            Sheet sheet2 = workbook2.getSheetAt(0);
            String zzmc=ExcelUtil.getMergedRegionValue(sheet2,1,5);


            List<Object[]> data = new ArrayList<>();//新建excel使用
            List<CellType[]> celltypeList = new ArrayList<>();
            List<Integer> order = new ArrayList<>();
            Integer[] dbgbArr=new Integer[3];

            Row firstRow=sheet2.getRow(3);
            for(Cell cell:firstRow){
                String xyStr2=cell.getStringCellValue();
                Integer num=mapXy2Index.get(xyStr2);
                if(xyStr2.equals("地标")){
                    dbgbArr[0]=cell.getColumnIndex();
                }
                if(xyStr2.equals("国标")){
                    dbgbArr[1]=cell.getColumnIndex();
                }
                if(xyStr2.equals("卡片编码")){
                    dbgbArr[2]=cell.getColumnIndex();
                }
                order.add(num);
            }

            for (Row row : sheet2) {
                if (row.getRowNum() < 4) {
                    continue;
                }
                if(Objects.equals(String.valueOf(row.getCell(0)), "合计")){
                    break;
                }

                Object[] obj = new Object[size];
                CellType[] cellTypes = new CellType[size];




                for (Integer key : mapXy2Gs.keySet()) {
                    int dosomecellnum=row.getLastCellNum()+1;
                    int rowNum=row.getRowNum();
                    Cell fCell=row.createCell(dosomecellnum);
                    String re=ExcelUtil.replacePatternNumbers(mapXy2Gs.get(key),rowNum+1);
                    fCell.setCellFormula(re);
                    // 进行计算并拿到值
                    CellValue value = formulaEvaluator.evaluate(fCell);
                    // 将值转化成字符串
                    String format = value.formatAsString();
                    format=format.replace("\"","");
                    row.removeCell(fCell);
                    obj[key] = format;
                    cellTypes[key] = CellType.NUMERIC;
                }

                for (Cell cell : row) {
                    if (order.size()-1<cell.getColumnIndex()||order.get(cell.getColumnIndex()) == null) {
                        continue;
                    }

                    String cellStr = String.valueOf(row.getCell(cell.getColumnIndex()));


                    for (Integer key : mobanteshucl_gd.keySet()) {
                        obj[key] = mobanteshucl_gd.get(key);
                        cellTypes[key] = mobanteshucl_gd_cellType.get(key);
                    }
                    if(zzkp.contains(zzmc)){
                        String keyStr="";
                        keyStr=zzmc+"-"+row.getCell(dbgbArr[2]);
                        String val=dbgbmap.get(keyStr);
                        if(val!=null){
                            String[] valarr=val.split("-");

                            if(dbgbArr[0]==cell.getColumnIndex()){
                                cellStr=valarr[0];
                            }
                            if(dbgbArr[1]==cell.getColumnIndex()){
                                cellStr=valarr[1];
                            }
                        }

                    }


                    int mbi=order.get(cell.getColumnIndex());


                    obj[mbi] = cellStr;
                    cellTypes[order.get(cell.getColumnIndex())] = row.getCell(cell.getColumnIndex()).getCellType();

                }

                data.add(obj);
                celltypeList.add(cellTypes);
            }
            File dbfile = new File(homepath + "dc/" + file.getName());
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
            if (!data.isEmpty()) {
                ExcelUtil.createExcelFile(dbfile.getPath(), data, "yy",mobanList,celltypeList,pkmap,mobanteshucl_gd);
            }
        }
    }

    /**
     * viscada 表处理
     *
     * @return
     * @throws IOException
     */
    public static void viscadaStart() throws IOException {


        String zb = homepath + "zb.xlsx";
        String logFile = homepath + "log.txt";
        String bzhmcConfig = homepath + "configPid.txt";
        String ipysConfig = homepath + "configIp.txt";
//        String config=homepath+"config.txt";

        Path path = Paths.get(logFile);
        if (Files.exists(path)) {
            Files.delete(path);
        }

        Map<String, String> bzhmcConfigMap = getMap(bzhmcConfig);
        Map<String, String> ipysConfigMap = getMap(ipysConfig);
//        Map<String,String> configMap = getMap(config);


        logWriter = new FileWriter(logFile);

        viscadaSBSBL(zb, bzhmcConfigMap, ipysConfigMap);
        viscadaDB(zb, bzhmcConfigMap, ipysConfigMap);
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

    public static Map<String, String> getMap(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        String configStr = readAllCharactersOneByOne(fileReader);
        String[] strArr = configStr.split("\n");
        Map<String, String> bzhmcConfigMap = new HashMap<>();
        for (String s : strArr) {
            bzhmcConfigMap.put(s.split(":")[0], s.split(":")[1]);
        }
        fileReader.close();
        return bzhmcConfigMap;
    }

    /**
     * 设备和设备类
     *
     * @param bzhmcConfigMap
     * @param ipysConfigMap
     * @throws IOException
     */
    public static void viscadaSBSBL(String filePath, Map<String, String> bzhmcConfigMap, Map<String, String> ipysConfigMap) throws IOException {
        Workbook workbook = ExcelUtil.getWorkbook(filePath);
        Sheet sheet = workbook.getSheetAt(0);
        logWriter.write("---------------生成设备和设备类导入表-------------------");
        List<Object[]> sbldata = new ArrayList<>();//新建excel使用
        List<Object[]> sbdata = new ArrayList<>();//新建excel使用
        String sblbsqz = "lLbSAp76Zp";
        Map<String, String> quchongMap = new HashMap<>();
        Set<String> ipquchong = new HashSet<>();
        for (Row row : sheet) {
            if (row.getRowNum() < 1) {
                continue;
            }

            String xh = String.valueOf(row.getCell(0)).replace(".0", "");//序号(设备标准化名称)

            String zm = String.valueOf(row.getCell(1));//站名（设备名）
            String txxy = String.valueOf(row.getCell(2));//通讯协议
            String ipdz = String.valueOf(row.getCell(3));//IP地址
            String sblmc = String.valueOf(row.getCell(10));//设备类名称（点表名称）
            String txlx = "1";//直连
            String sblbzhmc = "substation";//标准化名称
            String txxy2 = "";
            if (txxy.contains("S7")) {
                txxy2 = "5";
            } else if (txxy.contains("Modbus")) {
                txxy2 = "3";
            } else {
                logWriter.write("\n异常 通讯协议不是S7或modbus：总表 " + row.getRowNum() + " 行 " + txxy);
                continue;
            }
            String sblbsqz2 = sblbsqz + row.getRowNum();
            if (quchongMap.get(sblmc) == null) {
                quchongMap.put(sblmc, sblbsqz2);
                Object[] objects1 = new Object[20];
                objects1[1] = sblbsqz2;
                objects1[2] = sblmc;
                objects1[3] = txxy2;
                objects1[4] = txlx;
                objects1[14] = sblbzhmc;
                sbldata.add(objects1);
            }

            //设备
            if (ipquchong.contains(ipdz)) {
                logWriter.write("\n异常 设备IP地址重复已跳过：总表 " + row.getRowNum() + " 行 " + ipdz);
                continue;
            }
            ipquchong.add(ipdz);
            Object[] objects2 = new Object[14];
            objects2[1] = xh;
            objects2[2] = zm;
            objects2[6] = quchongMap.get(sblmc);
            String sbbzhmc = "";
            if (bzhmcConfigMap.get(zm.replace(" ", "")) != null) {
                sbbzhmc = bzhmcConfigMap.get(zm);
            }
            objects2[11] = sbbzhmc;
            String jscs = "";

            String ipdzhdkh = "";
            if (ipysConfigMap.get(ipdz) != null) {
                ipdzhdkh = ipysConfigMap.get(ipdz).replace("-", ":");
            }

            if (txxy.contains("S7")) {
                jscs = "server:(" + ipdzhdkh + ");rack:0;slot:1;type:" + txxy;

            } else if (txxy.contains("Modbus")) {
                jscs = "slaveUrl:(tcp://" + ipdzhdkh + ");slaveId:1;proxy:modbus";
            }

            objects2[12] = jscs;
            sbdata.add(objects2);

        }
        ExcelUtil.createExcelFile(homepath + "设备类导入表.xlsx", sbldata, "sbl",null,null, null, null);
        ExcelUtil.createExcelFile(homepath + "设备导入表.xlsx", sbdata, "sb",null,null, null, null);
    }

    /**
     * viscada 点表
     *
     * @param filePath
     * @param bzhmcConfigMap
     * @param ipysConfigMap
     * @throws IOException
     */
    public static void viscadaDB(String filePath, Map<String, String> bzhmcConfigMap, Map<String, String> ipysConfigMap) throws IOException {

        Workbook workbook = ExcelUtil.getWorkbook(filePath);

        logWriter.write("\n---------------生成点表-------------------");
        List<Object[]> bzhmclist = new ArrayList<>();//测试标准化名称判断情况
        for (int i = 1; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表

            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();

            List<Object[]> data = new ArrayList<>();//新建excel使用
            Set<String> biaoshiSet = new HashSet<>();//属性标识重复跳过
            Map<String, Integer> bzhmcMap = new HashMap<>();//标准化名称重复改名
            logWriter.write("\n生成点表:" + sheetName);
            int flag = 0;

            Object[] bzhmctest = new Object[38];
            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                if (rowNum < 2) {
                    continue;
                }

                String cell1 = String.valueOf(row.getCell(1));//点名称
                String cell3 = String.valueOf(row.getCell(3));//modbus地址
                String cell4 = String.valueOf(row.getCell(4));//读写
                String cell5 = String.valueOf(row.getCell(5));//数据类型
                String cell6 = String.valueOf(row.getCell(6));//系数
                String cell7 = String.valueOf(row.getCell(7));//单位


                if (!cell3.contains("Holding_register") && !cell3.contains("DB") && !cell3.contains("V") &&
                        !cell3.contains("I") && !cell3.contains("Q") && !cell3.contains("M")) {
                    flag = 1;
                    continue;
                }
                //重复的modbus地址跳过并记录
                if (biaoshiSet.contains(cell3)) {
                    logWriter.write("\n异常:" + sheetName + " " + rowNum + " 行modbus地址重复已跳过：" + cell3);
                    continue;
                }
                biaoshiSet.add(cell3);
                //属性标识

                String result = DbUtil.sxbs(cell1, cell3, cell5, sheetName, rowNum);
                if ("-1".equals(result)) {
                    logWriter.write("\n异常:" + sheetName + " " + rowNum + " 行 mudbus地址异常跳过：" + cell3);
                    continue;
                }
                //属性名称
                String attrName = cell1;

                //标准化名称
                String bzhname = cell1;
                String bzhmc = "";
                String bzhmchz = "";//后缀

                bzhmc = DbUtil.bzhmcsc(bzhname, bzhmc, bzhmchz, bzhmctest);

                //读写模式
                String dxms = cell4.equals("只读") ? "1" : "2";
                //单位
                String danwei = cell7;
                //数据类型
                String sjlx = "";
                if (cell5.contains("整")) {
                    sjlx = "1";
                } else if (cell5.contains("浮点")) {
                    sjlx = "3";
                } else if (cell5.contains("字符")) {
                    sjlx = "4";
                } else if (cell5.contains("日期")) {
                    sjlx = "5";
                } else if (cell5.contains("布尔")) {
                    sjlx = "6";
                }


                Object[] objects = new Object[15];
                objects[1] = result;
                objects[2] = attrName;
                if (bzhmcMap.get(bzhmc) != null) {
                    int num = bzhmcMap.get(bzhmc);
                    String bzhmc2 = bzhmc;
                    bzhmc = bzhmc + "_" + num;
                    num++;
                    bzhmcMap.put(bzhmc2, num);
                    logWriter.write("\n异常:" + sheetName + " " + row.getRowNum() + " 行 标准化名称重复 +1处理：" + bzhmc + " " + attrName);
                } else {
                    bzhmcMap.put(bzhmc, 2);
                }

                //计算公式
                objects[3] = bzhmc;
                String jsgs = null;
                if (!cell6.equals("1") && !cell6.equals("1.0")) {
                    jsgs = "${" + bzhmc + "}*" + cell6;
                }
                objects[4] = dxms;
                objects[5] = danwei;
                objects[7] = sjlx;
                //上报公式
                objects[6] = jsgs;
                //操作模式
//                    objects[12]="D";
                //下发公式
                if ("2".equals(dxms)) {
                    objects[14] = jsgs;
                }
                data.add(objects);
            }


            bzhmclist.add(bzhmctest);

            if (flag == 1) {
                logWriter.write("\n异常:" + sheetName + " 内有需要人工查询的物模型属性");
            }
            File dbfile = new File(homepath + "dbs/" + sheetName + ".xlsx");
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
            if (!data.isEmpty()) {
                ExcelUtil.createExcelFile(dbfile.getPath(), data, "db",null,null, null, null);
            }
        }
        ExcelUtil.createExcelFile(homepath + "test.xlsx", bzhmclist, "test",null,null, null, null);

        workbook.close();

    }
}