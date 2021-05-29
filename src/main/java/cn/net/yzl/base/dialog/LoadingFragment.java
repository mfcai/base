package cn.net.yzl.base.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatDialog;

import cn.net.yzl.base.R;
import cn.net.yzl.base.utils.AndroidLifecycleUtils;


public class LoadingFragment {
    private static WeakReference<Context> contextWeakReference;
    private String content;
    private static AppCompatDialog loadingDialog;

    private LoadingFragment() {
    }

    static Handler mHandler = new Handler(Looper.getMainLooper());

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Context mContext = contextWeakReference.get();
            if (loadingDialog != null ) {
                if(loadingDialog.isShowing() &&  AndroidLifecycleUtils.isActivityDestroy((Activity) mContext)){
                    loadingDialog.dismiss();
                }

                loadingDialog = null;
            }
        }
    };

    public static void showLodingDialog(Context context) {

        contextWeakReference = new WeakReference<>(context);
        Context mContext = contextWeakReference.get();
        showLodingDialog(mContext, false);
    }




    private static Dialog showLodingDialog(Context context, boolean mIsCancel) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progress_loading, null);// 得到加载view
        if(loadingDialog != null){
            if(loadingDialog.isShowing() && AndroidLifecycleUtils.isActivityDestroy(context)) {
                loadingDialog.dismiss();
            }
            loadingDialog = null;
        }

        loadingDialog = new AppCompatDialog(context, R.style.progress_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
        loadingDialog.setCanceledOnTouchOutside(false); // 点击加载框以外的区域
        loadingDialog.setContentView(v);// 设置布局

        if (mIsCancel)//点击返回键不取消
            loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });


        //将显示Dialog的方法封装在这里面
        Window window = loadingDialog.getWindow();
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.CENTER);
            window.getAttributes().gravity = Gravity.CENTER;
            window.setAttributes(lp);
        }
        //    window.setWindowAnimations(R.style.PopWindowAnimStyle);
        if ( !((Activity) context).isFinishing() ) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != loadingDialog) {
                        loadingDialog.show();
                    }
                }
            },1000);

        } else {
            dismiss();
        }

        return loadingDialog;
    }



    /**
     * 关闭dialog
     */


    public static void dismiss() {
//        if (null != loadingDialog && loadingDialog.isShowing()) {
//            //延迟200ms再结束弹窗，简化连续调用接口的进度条管理
//            mHandler.postDelayed(runnable, 200);
//            return;
//        }
//        // 1.移除Handler中可能存在的runnable 2.立即结束弹窗
//        // 通常用在Activity Destroy中，防止窗体泄漏
//        mHandler.removeCallbacks(runnable);
        if (null != loadingDialog) {
            Context context = contextWeakReference.get();
            if(loadingDialog.isShowing() && AndroidLifecycleUtils.isActivityDestroy(context)) {
                loadingDialog.dismiss();
            }
            loadingDialog = null;
        }
    }
}





