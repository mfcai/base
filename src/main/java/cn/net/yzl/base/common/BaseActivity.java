package cn.net.yzl.base.common;

import cn.ycbjie.ycstatusbarlib.bar.StateAppBar;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import cn.net.yzl.base.R;
import cn.net.yzl.base.dialog.MyAlertDialog;

/**
 * Created by dell on 2018-11-11.
 */

public abstract class BaseActivity extends RxAppCompatActivity {
    private final static String TAG = BaseActivity.class.getSimpleName();
    private Context mContext;
    private MyAlertDialog loadingDialog;
//    private SystemBarTintManager tintManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateAppBar.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorTheme));
//        initCollapsingToolbar();
        mContext = this;

        /** 注意：setContentView 必需在 bind 之前 */
        setContentView(getLayoutId());

        initView(savedInstanceState);
        initData();
        initEvents();

    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration newConfig = new Configuration();
        newConfig.setToDefaults();//设置默认
        res.updateConfiguration(newConfig, res.getDisplayMetrics());
        return res;
    }
    //极光统计bug： View=DecorView@69cd4d[PublicWebViewActivity] not attached to window manager
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    protected boolean isActivityDestroy(){
        if(isFinishing() || (Build.VERSION.SDK_INT >= 17 && isDestroyed())){
            return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    public void showProgressDialog(String content) {
        if(isActivityDestroy()){
            return;
        }
        try{
            if (loadingDialog == null) {
                if(mContext!=null){
                    loadingDialog = createLoadingDialog(content);
                    loadingDialog.show();
                }
            } else {
                if(loadingDialog.getContext()!=null&&!loadingDialog.isShowing()){
                    loadingDialog.show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void hideProgressDialog() {
        if(isActivityDestroy()){
            return;
        }
        try{
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }catch (Exception e){
        }
    }
    //deleted by caimingfu 2019-07-04,没有被调用，删除
//    protected MyAlertDialog createLoadingDialog(){
//        return createLoadingDialog(getResources().getString(R.string.dialog_loading));
//    }

    public void showLocationLoading(String content) {
        if(isActivityDestroy()){
            return;
        }
        try{
            if (loadingDialog == null) {
                if(mContext!=null){
                    loadingDialog = createLoadingDialog(content,true);
                    loadingDialog.show();
                }
            } else {
                if(loadingDialog.getContext()!=null&&!loadingDialog.isShowing()){
                    loadingDialog.show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected MyAlertDialog createLoadingDialog(String content){

        return createLoadingDialog(content,false);
    }

    protected MyAlertDialog createLoadingDialog(String content,boolean isbackdismiss){
        MyAlertDialog dialog = new MyAlertDialog.Builder(this)
                .setContentView(R.layout.dialog_loading).setText(R.id.tv_content,content)
                .setWidthAndHeight( WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT)
                .formBottom(false).create();
        if(!isbackdismiss) {
            dialog.setCanceledOnTouchOutside(false);
        }else{
            dialog.setCancelable(false);//点返回键不消失
        }
//        dialog.getWindow().setBackgroundDrawableResource(R.color.oa_dialog_transparent_background);
        return dialog;
    }
    public MyAlertDialog createOneBtnDialog(String content) {
        if (!isActivityDestroy()){
            return new MyAlertDialog.Builder(this)
                    .setContentView(R.layout.dialog_prompt_one_button_scroll).setAutoAdapter()
                    .setText(R.id.tv_message, content).create();
        }
        return null;
    }
    public void showOneBtnDialog(final MyAlertDialog dialog){
        if(!isActivityDestroy() && dialog != null) {
            dialog.show();
            dialog.setOnclickListener(R.id.bn_dialog, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    public void showOneBtnDialog(final MyAlertDialog dialog,View.OnClickListener listener){
        if(!isActivityDestroy() && dialog != null) {
            dialog.show();
            dialog.setOnclickListener(R.id.bn_dialog, listener);
        }
    }

    protected abstract int getLayoutId();
    protected abstract void initView(Bundle savedInstanceState);
    protected abstract void initData();
    protected abstract void initEvents();
}
