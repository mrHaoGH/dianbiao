package com.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbUtil {
    /**
     * 属性标识
     * @param cell1
     * @param cell3
     * @param cell5
     * @param sheetName
     * @param rowNum
     * @return
     */
    public static String sxbs(String cell1,String cell3,String cell5,String sheetName,int rowNum) {

        String thf="";
        if(cell5.contains("布")){
            thf="X";
        } else if (cell5.contains("浮")) {
            thf="R";
        }else{
            thf="I";
        }
        String result="";
        if (cell3.substring(0,2).contains("DB")) {
            String plcArr=cell3;

            if (plcArr.contains("DBD")){
                result=plcArr.replace("DBD",thf);
            }else if(plcArr.contains("DBW")){
                result=plcArr.replace("DBW",thf);
            }else {
                return "-1";
            }
        }else if(cell3.substring(0,2).contains("V")){
            String sxbs="DB1.";
            sxbs+=thf;

            Pattern pattern1 = Pattern.compile("\\d*\\.?\\d+");
            Matcher matcher1 = pattern1.matcher(cell3);
            if(matcher1.find()) {
                sxbs+=matcher1.group();
            }else{
                return "-1";
            }
            result=sxbs;
        }else if(cell3.substring(0,2).contains("I")){
            String sxbs="PE0."+thf;
            Pattern pattern1 = Pattern.compile("\\d*\\.?\\d+");
            Matcher matcher1 = pattern1.matcher(cell3);
            if(matcher1.find()) {
                sxbs+=matcher1.group();
            }else{
                return "-1";
            }
            result=sxbs;
        }else if(cell3.substring(0,2).contains("Q")){
            String sxbs="PA0."+thf;
            Pattern pattern1 = Pattern.compile("\\d*\\.?\\d+");
            Matcher matcher1 = pattern1.matcher(cell3);
            if(matcher1.find()) {
                sxbs+=matcher1.group();
            }else{
                return "-1";
            }
            result=sxbs;
        }else if(cell3.substring(0,2).contains("M")){
            String sxbs="MK0."+thf;
            Pattern pattern1 = Pattern.compile("\\d*\\.?\\d+");
            Matcher matcher1 = pattern1.matcher(cell3);
            if(matcher1.find()) {
                sxbs+=matcher1.group();
            }else{
                return "-1";
            }
            result=sxbs;
        }
        else{
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

                if (cell5.contains("整") || cell5.contains("浮点")) {
                    if (cell5.contains("整形")) {
                        result = result + ":UINT";
                    } else {
                        result = result + ":REAL";
                    }
                    result = "4x" + result;
                } else if (cell5.contains("布尔")) {
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
                return "-1";
            }
        }


        return result;

    }
    /**
     * 生成标准化名称
     *
     * @param bzhname   原名称
     * @param bzhmc     结果
     * @param bzhmchz   后缀
     * @param bzhmctest
     * @return
     */
    public static String bzhmcsc(String bzhname, String bzhmc, String bzhmchz, Object[] bzhmctest) {

        //预处理
        int num=7;

        Pattern jh = Pattern.compile("[0-9]");
        Matcher matcherJh = jh.matcher(bzhname.substring(0, Math.min(bzhname.length(), num)));
        while (matcherJh.find()) {
            bzhmchz=bzhmchz+"_"+matcherJh.group();
        }

        Pattern pattern1 = Pattern.compile("\\（[^\\）]*\\）|\\([^)]*\\)");
        Matcher matcher1 = pattern1.matcher(bzhname);
        if(matcher1.find()) {
            String str=matcher1.group();
            bzhname=bzhname.replace(str,"");
        }

        if(bzhname.lastIndexOf("_")>6){
            bzhname=bzhname.substring(0,bzhname.lastIndexOf("_"));
        }

        if(bzhname.contains("#")){
//            int starNUm=2;
//            if(!bzhname.contains("机组")){
//                starNUm=0;
//            }
//            bzhmchz="_"+bzhname.substring(starNUm,bzhname.indexOf("#"));
            bzhname=bzhname.substring(bzhname.indexOf("#")+1,bzhname.length());
        } else if (bzhname.contains("_")) {
//            int starNUm=2;
//            if(!bzhname.contains("机组")){
//                starNUm=0;
//            }
//            bzhmchz="_"+bzhname.substring(starNUm,bzhname.indexOf("_"));
            bzhname=bzhname.substring(bzhname.indexOf("_")+1,bzhname.length());
        }else if (bzhname.contains("机组")) {
            Pattern pattern2 = Pattern.compile("机组\\d+");
            Matcher matcher2 = pattern2.matcher(bzhname);
            String bzhnamejznum="";
            if(matcher2.find()){
                bzhnamejznum=matcher2.group();
            }
//            bzhmchz="_"+bzhnamejznum.substring(2,bzhnamejznum.length());
            bzhname=bzhname.replace(bzhnamejznum,"");
        }
        if(bzhname.contains("：")){
            bzhname=bzhname.substring(0,bzhname.indexOf("："));
        }

        if(bzhname.contains("最")||bzhname.contains("计算")||bzhname.contains("曲线")||bzhname.contains("温差")||bzhname.contains("分支")||bzhname.contains("上限")||bzhname.contains("下限")||bzhname.contains("进水电磁")){
//            bzhmc="";
            bzhmc=ChineseToPinyin.getPinyinInitial(bzhname)+bzhmchz;
            return bzhmc;
        }

        if (bzhname.contains("流量")) {
            if (bzhname.contains("瞬时")) {
                bzhmctest[7]+=bzhname;
                bzhmc="m_003q"+bzhmchz;
            } else if (bzhname.contains("累")) {
                bzhmctest[16]+=bzhname;
                bzhmc="m_006q"+bzhmchz;
            }
        }
        if (bzhname.contains("热量")) {
            if (bzhname.contains("瞬时")) {
                bzhmctest[10]+=bzhname;
                bzhmc="m_003qc"+bzhmchz;
            } else if (bzhname.contains("累")) {
                bzhmctest[13]+=bzhname;
                bzhmc="m_006qc"+bzhmchz;
            }
        }
        if(bzhname.contains("一")){

            if(bzhname.contains("流")){
                bzhmctest[7]+=bzhname;
                bzhmc="m_003q"+bzhmchz;
                if(bzhname.contains("累")){
                    bzhmctest[16]+=bzhname;
                    bzhmc="m_006q"+bzhmchz;
                }
            } else if (bzhname.contains("热")) {
                bzhmctest[10]+=bzhname;
                bzhmc="m_003qc"+bzhmchz;
                if(bzhname.contains("累")){
                    bzhmctest[13]+=bzhname;
                    bzhmc="m_006qc"+bzhmchz;
                }
            }

            if(bzhname.contains("差")){
                bzhmctest[4]+=bzhname;
                bzhmc="m_003p"+bzhmchz;
            }

            if(bzhname.contains("供")||bzhname.contains("入")){
                if(bzhname.contains("压")){
                    bzhmctest[2]+=bzhname;
                    bzhmc="m_001p"+bzhmchz;
                } else if (bzhname.contains("温")) {
                    bzhmctest[0]+=bzhname;
                    bzhmc="m_001t"+bzhmchz;
                } else if (bzhname.contains("流")) {
                    bzhmctest[5]+=bzhname;
                    bzhmc="m_001q"+bzhmchz;
                    if(bzhname.contains("累")){
                        bzhmctest[14]+=bzhname;
                        bzhmc="m_004q"+bzhmchz;
                    }
                } else if (bzhname.contains("热")) {
                    bzhmctest[8]+=bzhname;
                    bzhmc="m_001qc"+bzhmchz;
                    if(bzhname.contains("累")){
                        bzhmctest[11]+=bzhname;
                        bzhmc="m_004qc"+bzhmchz;
                    }
                }

            } else if (bzhname.contains("回")) {
                if(bzhname.contains("压")){
                    bzhmctest[3]+=bzhname;
                    bzhmc="m_002p"+bzhmchz;
                } else if (bzhname.contains("温")) {
                    bzhmctest[1]+=bzhname;
                    bzhmc="m_002t"+bzhmchz;
                } else if (bzhname.contains("流")) {
                    bzhmctest[6]+=bzhname;
                    bzhmc="m_002q"+bzhmchz;
                    if(bzhname.contains("累")){
                        bzhmctest[15]+=bzhname;
                        bzhmc="m_005q"+bzhmchz;
                    }
                } else if (bzhname.contains("热")) {
                    bzhmctest[9]+=bzhname;
                    bzhmc="m_002qc"+bzhmchz;
                    if(bzhname.contains("累")){
                        bzhmctest[12]+=bzhname;
                        bzhmc="m_005qc"+bzhmchz;
                    }
                }
            }
        } else if(bzhname.contains("二")){

            if(bzhname.contains("流")){
                bzhmctest[23]+=bzhname;
                bzhmc="m_039q_1"+bzhmchz;
                if(bzhname.contains("累")){
                    bzhmctest[26]+=bzhname;
                    bzhmc="m_078q_1"+bzhmchz;
                }
            } else if (bzhname.contains("热")) {
                bzhmctest[24]+=bzhname;
                bzhmc="m_039qc_1"+bzhmchz;
                if(bzhname.contains("累")){
                    bzhmctest[25]+=bzhname;
                    bzhmc="m_078qc_1"+bzhmchz;
                }
            }

            if(bzhname.contains("差")){
                bzhmctest[22]+=bzhname;
                bzhmc="008p_1"+bzhmchz;
            }

            if(bzhname.contains("供")||bzhname.contains("出")){
                if(bzhname.contains("压")){
                    bzhmctest[17]+=bzhname;
                    bzhmc="m_004p_1"+bzhmchz;
                } else if (bzhname.contains("温")) {
                    bzhmctest[19]+=bzhname;
                    bzhmc="m_004t_1"+bzhmchz;
                    if(bzhname.contains("给")||bzhname.contains("设")||bzhname.contains("定")||bzhname.contains("控制")){
                        bzhmctest[20]+=bzhname;
                        bzhmc="m_004sett_1"+bzhmchz;
                    }
                }

            } else if (bzhname.contains("回")) {
                if(bzhname.contains("压")){
                    bzhmctest[18]+=bzhname;
                    bzhmc="m_005p_1"+bzhmchz;
                } else if (bzhname.contains("温")) {
                    bzhmctest[21]+=bzhname;
                    bzhmc="m_005t_1"+bzhmchz;
                }
            }
        }

        if(bzhname.contains("阀")){
            if(bzhname.contains("调")){
                if(bzhname.contains("开度")){

                    bzhmctest[27]+=bzhname;
                    bzhmc="m_004opening"+bzhmchz;
                    if(bzhname.contains("给")||bzhname.contains("设")||bzhname.contains("定")){
                        bzhmctest[28]+=bzhname;
                        bzhmc="m_002opening"+bzhmchz;
                    }
                }else if(bzhname.contains("实际")||bzhname.contains("反馈")){
                    bzhmctest[27]+=bzhname;
                    bzhmc="m_004opening"+bzhmchz;
                }
                if(bzhname.contains("动")){
                    bzhmctest[29]+=bzhname;
                    bzhmc="m_valve_controltype"+bzhmchz;
                }
            }
        }

        if(bzhname.contains("泵")){
            if (bzhname.contains("频率")){
                if (bzhname.contains("循环")){
                    bzhmctest[30]+=bzhname;
                    bzhmc="m_007f"+bzhmchz;
                    if (bzhname.contains("给")||bzhname.contains("设")||bzhname.contains("定")){
                        bzhmctest[31]+=bzhname;
                        bzhmc="m_005f"+bzhmchz;

                    }else if(bzhname.contains("实际")||bzhname.contains("反馈")){
                        bzhmctest[32]+=bzhname;
                        bzhmc="m_007f"+bzhmchz;
                    }
                } else {
                    bzhmctest[33]+=bzhname;
                    bzhmc="m_007f"+"_bs"+bzhmchz;
                    if (bzhname.contains("给")||bzhname.contains("设")||bzhname.contains("定")){
                        bzhmctest[34]+=bzhname;
                        bzhmc="m_005f"+"_bs"+bzhmchz;
                    }else if(bzhname.contains("实际")||bzhname.contains("反馈")){
                        bzhmctest[35]+=bzhname;
                        bzhmc="m_007f"+"_bs"+bzhmchz;
                    }
                }
            }else if (bzhname.contains("电流")) {
                if (bzhname.contains("循环")) {
                    bzhmctest[36]+=bzhname;
                    bzhmc = "m_005fi" + bzhmchz;
                } else if (bzhname.contains("一次") || bzhname.contains("一网") || bzhname.contains("加压")||bzhname.contains("一环")) {
                    bzhmctest[37]+=bzhname;
                    bzhmc = "m_005fi" + "_bs" + bzhmchz;
                }
            }
        }

        if(bzhname.contains("电量")){
            if (bzhname.contains("二")){
                bzhmc="m_002elec"+bzhmchz;
            } else {
                bzhmc="m_001elec"+bzhmchz;
            }
        }

        if(bzhname.contains("液位")){
            bzhmc="m_001waterlevel"+bzhmchz;
        }


        if (bzhmc.isEmpty()){
            bzhmc=ChineseToPinyin.getPinyinInitial(bzhname)+bzhmchz;
        }
        return bzhmc;
    }
}
