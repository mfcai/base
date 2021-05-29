package cn.net.yzl.base.common;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.trello.rxlifecycle3.components.RxFragment;
import cn.net.yzl.base.R;
import cn.net.yzl.base.dialog.MyAlertDialog;


/**
 * Created by dell on 2018-11-11.
 */

public abstract class BaseFragment extends RxFragment {
    protected View rootView;
    private Context mContext;
    protected MyAlertDialog loadingDialog;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public MyAlertDialog createOneBtnDialog(String content){
        return new MyAlertDialog.Builder(getContext())
                .setContentView(R.layout.dialog_prompt_one_button_scroll).setAutoAdapter().setCancelable(true)
                .setText(R.id.tv_message,content).create();
    }
    public void showOneBtnDialog(final MyAlertDialog dialog){
        dialog.show();
        dialog.setOnclickListener(R.id.bn_dialog, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showOneBtnDialog(final MyAlertDialog dialog,View.OnClickListener listener){
        dialog.show();
        dialog.setOnclickListener(R.id.bn_dialog,listener);
    }
    protected void dealBack() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            getActivity().finish();
        } else {
            fm.popBackStack();
        }
    }

    protected MyAlertDialog createLoadingDialog(){
        return createLoadingDialog(getResources().getString(R.string.dialog_loading));
    }


    protected MyAlertDialog createLoadingDialog(String content){
        MyAlertDialog dialog = new MyAlertDialog.Builder(getActivity())
                .setContentView(R.layout.dialog_loading).setText(R.id.tv_content,content)
                .setWidthAndHeight( WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT)
                .formBottom(false).create();
        dialog.setCanceledOnTouchOutside(false);
//        dialog.getWindow().setBackgroundDrawableResource(R.color.oa_dialog_transparent_background);
        return dialog;
    }

    public void showProgressDialog() {

        try{
            if (loadingDialog == null) {
                if(mContext!=null){
                    loadingDialog = createLoadingDialog();
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
        try{
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }catch (Exception e){
        }
    }
}
