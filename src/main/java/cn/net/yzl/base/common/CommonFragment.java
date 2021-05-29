package cn.net.yzl.base.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.trello.rxlifecycle3.components.support.RxFragment;

import cn.net.yzl.base.R;
import cn.net.yzl.base.dialog.MyAlertDialog;
import cn.net.yzl.base.utils.AndroidLifecycleUtils;

public abstract class CommonFragment  extends RxFragment {
    protected View rootView;
    private Context mContext;
    private MyAlertDialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mContext =getActivity();
            rootView = inflater.inflate(getLayoutId(), null);
            initView(rootView);
            initData();
            initEvents();
        } else {
            ViewGroup vg = (ViewGroup) rootView.getParent();
            if (vg != null) {
                vg.removeView(rootView);
            }
        }
        return rootView;
    }
    protected abstract int getLayoutId();
    protected abstract void initView(View rootView);
    protected abstract void initData();
    protected abstract void initEvents();

    public void showProgressDialog(String content) {
        if(AndroidLifecycleUtils.isActivityDestroy(getContext())){
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
        if(AndroidLifecycleUtils.isActivityDestroy(getContext())){
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
    protected MyAlertDialog createLoadingDialog(String content, boolean isbackdismiss){
        MyAlertDialog dialog = new MyAlertDialog.Builder(getContext())
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
    protected MyAlertDialog createLoadingDialog(String content){

        return createLoadingDialog(content,false);
    }
}
