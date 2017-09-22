package com.haokan.screen.util;

import android.content.Context;
import android.content.Intent;

import com.haokan.screen.service.KillSelfService;
//import com.taobao.sophix.SophixManager;

/**
 * Created by xiefeng on 2017/6/29.
 */

public class RestartAPPTool {
//    /**
//     * 重启整个APP
//     *
//     * @param context
//     * @param Delayed 延迟多少毫秒
//     */
//    public static void restartAPP(Context context, long Delayed) {
//
//        /**开启一个新的服务，用来重启本APP*/
//        Intent intent1 = new Intent(context, KillSelfService.class);
//        intent1.putExtra("PackageName", context.getPackageName());
//        intent1.putExtra("Delayed", Delayed);
//        context.startService(intent1);
//
//        /**杀死整个进程**/
////        android.os.Process.killProcess(android.os.Process.myPid());
//        SophixManager.getInstance().killProcessSafely();// 使用阿里的kill进程
//    }
//
//    /***重启整个APP*/
//    public static void restartAPP(Context context) {
//        restartAPP(context, 2000);
//    }
}
