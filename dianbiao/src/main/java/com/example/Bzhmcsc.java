package com.example;

public class Bzhmcsc {

    public static String bzhmcsc(String bzhname,String bzhmc,String bzhmchz) {
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
