package cn.net.yzl.base.config;

import cn.net.yzl.base.constant.Constant;

public class NetConfig {
    public final static String temp_url ="http://192.168.32.143:38080";

    public static class Host{
        //服务器地址
        interface ClockinHostUrl {
//            final static String SERVER_DEVELOP = "http://192.168.33.138:38080";
//            final static String SERVER_DEVELOP = "http://192.168.33.204:38080";
//             final static String SERVER_DEVELOP = "http://192.168.33.140:38080";
//                final static String SERVER_DEVELOP = "http://192.168.33.132:38080";
            final static String SERVER_DEVELOP = "http://server.staff.yuzhilin.net.cn/oa";
               final static String SERVER_TEST ="http://servertest.staff.yuzhilin.net.cn/oa";
            final static String SERVER_PRODUCT ="https://oa.yuzhilin.net.cn:19080/oa";
        }





        interface  LoginHostUrl{
            final static String SERVER_DEVELOP ="http://server.staff.yuzhilin.net.cn";
            final static String SERVER_TEST ="http://servertest.staff.yuzhilin.net.cn";
            final static String SERVER_PRODUCT ="https://oa.yuzhilin.net.cn:19080";
        }
    }



    public static String getLoginHost(){
//        return Host.LoginHostUrl.SERVER_DEVELOP;
//        return Host.LoginHostUrl.SERVER_TEST;
        if(Constant.SERVER_TYPE ==  Constant.ServerType.SERVER_PRODUCTION) {
            return Host.LoginHostUrl.SERVER_PRODUCT;
        }else{
            return Host.LoginHostUrl.SERVER_TEST;
        }
    }


    /**
     * 返回服务器基础地址
     */
    public static String getClockinHost() {
//        return Host.ClockinHostUrl.SERVER_DEVELOP;
//        return Host.ClockinHostUrl.SERVER_TEST;
        if(Constant.SERVER_TYPE == Constant.ServerType.SERVER_PRODUCTION){
            return Host.ClockinHostUrl.SERVER_PRODUCT;
        }else{
            return Host.ClockinHostUrl.SERVER_TEST;
        }

    }
}
