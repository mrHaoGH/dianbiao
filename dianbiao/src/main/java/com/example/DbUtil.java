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


        String result="";
        if (cell3.substring(0,2).contains("DB")) {
            String sss=cell3;
            String thf="";
            if(cell5.contains("布")){
                thf="X";
            } else if (cell5.contains("浮")) {
                thf="R";
            }else{
                thf="I";
            }
            if (sss.contains("DBD")){
                result=sss.replace("DBD",thf);
            }else if(sss.contains("DBW")){
                result=sss.replace("DBW",thf);
            }else {
                return "-1";
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
     * @param bzhname 原名称
     * @param bzhmc 结果
     * @param bzhmchz 后缀
     * @return
     */
    public static String bzhmcsc(String bzhname,String bzhmc,String bzhmchz) {


        Pattern jh = Pattern.compile("\\d+#");
        Matcher matcherJh = jh.matcher(bzhname);
        String numJh="";
        if(matcherJh.find()){
            numJh=matcherJh.group();

            String[] strarr=bzhname.split(numJh);
            bzhmchz="_"+numJh.substring(0,numJh.length()-1);

            String qz=bzhmchz+strarr[1];
            String zw=bzhmchz+strarr[2];

//            Pattern rgkb = Pattern.compile("\\s+");
//            Matcher matcherrgkb = rgkb.matcher(qz);
//
//            if(matcherJh.find()){
//                numJh=matcherrgkb.group();
//            }

            qz=ChineseToPinyin.getPinyinInitial(qz.replace(" ",""));
            bzhmchz="_"+qz+bzhmchz;
        }



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




        if(bzhname.contains("泵")){
            if (bzhname.contains("频率")){
                if (bzhname.contains("二次")||bzhname.contains("二网")||bzhname.contains("循环")||bzhname.contains("二环")||bzhname.contains("二段")){
                    if (bzhname.contains("给定")||bzhname.contains("设定")){
                        if(bzhname.contains("补水")){
                            bzhmc="m_005f"+"_bs"+bzhmchz;
                        }else{
                            bzhmc="m_005f"+bzhmchz;
                        }
                    }else if(bzhname.contains("实际")||bzhname.contains("反馈")){
                        if(bzhname.contains("补水")){
                            bzhmc="m_007f"+"_bs"+bzhmchz;
                        }else{
                            bzhmc="m_007f"+bzhmchz;
                        }
                    }
                } else {
                    if (bzhname.contains("给定")||bzhname.contains("设定")){
                        if(bzhname.contains("补水")){
                            bzhmc="m_001f"+"_bs"+bzhmchz;
                        }else{
                            bzhmc="m_001f"+bzhmchz;
                        }
                    }else if(bzhname.contains("实际")||bzhname.contains("反馈")){
                        if(bzhname.contains("补水")){
                            bzhmc="m_003f"+"_bs"+bzhmchz;
                        }else{
                            bzhmc="m_003f"+bzhmchz;
                        }
                    }
                }
            }else if (bzhname.contains("电流")) {
                if (bzhname.contains("二次") || bzhname.contains("二网") || bzhname.contains("循环")||bzhname.contains("二环")||bzhname.contains("二段")) {
                    if (bzhname.contains("补水")) {
                        bzhmc = "m_005fi" + "_bs" + bzhmchz;
                    } else {
                        bzhmc = "m_005fi" + bzhmchz;
                    }
                } else if (bzhname.contains("一次") || bzhname.contains("一网") || bzhname.contains("加压")||bzhname.contains("一环")) {
                    if (bzhname.contains("补水")) {
                        bzhmc = "m_003fi" + "_bs" + bzhmchz;
                    } else {
                        bzhmc = "m_003fi" + bzhmchz;
                    }
                }
            }
        }
        if(bzhname.contains("电量")){
            if (bzhname.contains("二次")||bzhname.contains("二网")||bzhname.contains("二环")||bzhname.contains("二段")){
                bzhmc="m_002elec"+bzhmchz;
            } else {
                bzhmc="m_001elec"+bzhmchz;
            }
        }
        if(bzhname.contains("流量")){
            if (bzhname.contains("二次")||bzhname.contains("二网")||bzhname.contains("二环")||bzhname.contains("二段")){
                if(bzhname.contains("累计")||bzhname.contains("累积")){
                    bzhmc="m_078q"+bzhmchz;
                }else{
                    bzhmc="m_039q"+bzhmchz;
                }
            } else if(bzhname.contains("补水")){
                if(bzhname.contains("累计")||bzhname.contains("累积")){
                    bzhmc="m_079q"+bzhmchz;
                }else{
                    bzhmc="m_040q"+bzhmchz;
                }
            }else{
                if(bzhname.contains("累计")||bzhname.contains("累积")){
                    if(bzhname.contains("供")){
                        bzhmc="m_004q"+bzhmchz;
                    } else if (bzhname.contains("回")) {
                        bzhmc="m_005q"+bzhmchz;
                    }else{
                        bzhmc="m_006q"+bzhmchz;
                    }
                }else{
                    if(bzhname.contains("供")){
                        bzhmc="m_001q"+bzhmchz;
                    } else if (bzhname.contains("回")) {
                        bzhmc="m_002q"+bzhmchz;
                    }else{
                        bzhmc="m_003q"+bzhmchz;
                    }
                }
            }
        }
        if(bzhname.contains("热量")){
            if (bzhname.contains("二次")||bzhname.contains("二网")||bzhname.contains("二环")||bzhname.contains("二段")){
                if(bzhname.contains("累计")||bzhname.contains("累积")){
                    bzhmc="m_078qc"+bzhmchz;
                }else{
                    bzhmc="m_039qc"+bzhmchz;
                }
            }else{
                if(bzhname.contains("累计")||bzhname.contains("累积")){
                    if(bzhname.contains("供")){
                        bzhmc="m_004qc"+bzhmchz;
                    } else if (bzhname.contains("回")) {
                        bzhmc="m_005qc"+bzhmchz;
                    }else{
                        bzhmc="m_006qc"+bzhmchz;
                    }
                }else{
                    if(bzhname.contains("供")){
                        bzhmc="m_001qc"+bzhmchz;
                    } else if (bzhname.contains("回")) {
                        bzhmc="m_002qc"+bzhmchz;
                    }else{
                        bzhmc="m_003qc"+bzhmchz;
                    }
                }
            }
        }
        if(bzhname.contains("液位")){
            bzhmc="m_001waterlevel"+bzhmchz;
        }

        if(bzhname.contains("压力")||bzhname.contains("压")){
            if (bzhname.contains("二次")||bzhname.contains("二网")||bzhname.contains("二环")||bzhname.contains("二段")){
                if(bzhname.contains("差")){
                    bzhmc="m_008p"+bzhmchz;
                } else if (bzhname.contains("回")) {
                    bzhmc="m_005p"+bzhmchz;
                } else if (bzhname.contains("供")) {
                    bzhmc="m_004p"+bzhmchz;
                }
            }else if(bzhname.contains("一次")||bzhname.contains("一网")||bzhname.contains("一段")||bzhname.contains("一环")){
                if(bzhname.contains("差")){
                    bzhmc="m_003p"+bzhmchz;
                } else if (bzhname.contains("回")) {
                    bzhmc="m_002p"+bzhmchz;
                } else if (bzhname.contains("供")) {
                    bzhmc="m_001p"+bzhmchz;
                }
            }else if(bzhname.contains("改造")){
                if(bzhname.contains("差")){
                    bzhmc="m_001deltap"+bzhmchz;
                } else if (bzhname.contains("回")) {
                    bzhmc="m_018p"+bzhmchz;
                } else if (bzhname.contains("供")) {
                    bzhmc="m_017p"+bzhmchz;
                }
            }
        }
        if(bzhname.contains("温度")||bzhname.contains("温")){
            if (bzhname.contains("二次")||bzhname.contains("二网")||bzhname.contains("二环")||bzhname.contains("二段")){
                if(bzhname.contains("设定")||bzhname.contains("给定")){
                    bzhmc="m_004sett"+bzhmchz;
                } else if (bzhname.contains("回")) {
                    bzhmc="m_005t"+bzhmchz;
                } else if (bzhname.contains("供")) {
                    bzhmc="m_004t"+bzhmchz;
                }
            }else if(bzhname.contains("一次")||bzhname.contains("一网")||bzhname.contains("一段")||bzhname.contains("一环")){
                if(bzhname.contains("供")){
                    bzhmc="m_001t"+bzhmchz;
                } else if (bzhname.contains("回")) {
                    bzhmc="m_002t"+bzhmchz;
                }
            }
        }
        if(bzhname.contains("阀")&&bzhname.contains("开度")){
            if (bzhname.contains("二次")||bzhname.contains("二网")||bzhname.contains("二环")||bzhname.contains("二段")){
                if(bzhname.contains("设定")||bzhname.contains("给定")){
                    bzhmc="m_005opening"+bzhmchz;
                }else if(bzhname.contains("实际")||bzhname.contains("反馈")){
                    bzhmc="m_007opening"+bzhmchz;
                }
            }else if(bzhname.contains("一次")||bzhname.contains("一网")||bzhname.contains("一段")||bzhname.contains("一环")){
                if(bzhname.contains("设定")||bzhname.contains("给定")){
                    bzhmc="m_001opening"+bzhmchz;
                }else if(bzhname.contains("实际")||bzhname.contains("反馈")){
                    bzhmc="m_003opening"+bzhmchz;
                }
            }
        }

        if(bzhname.contains("上限")||bzhname.contains("下限")||bzhname.contains("进水电磁")||bzhname.contains("控制")){
            bzhmc="";
        }
        if (bzhmc.isEmpty()){
            bzhmc=ChineseToPinyin.getPinyinInitial(bzhname)+bzhmchz;
        }
        return bzhmc;
    }
}
