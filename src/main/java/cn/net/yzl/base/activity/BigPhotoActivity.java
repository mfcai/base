package cn.net.yzl.base.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;




import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


import cn.net.yzl.base.R;
import cn.net.yzl.base.adapter.ImagePagerAdapter;
import cn.net.yzl.base.arouter.ARouterConstant;
import cn.net.yzl.base.common.BaseActivity;
import cn.net.yzl.base.constant.Constant;
import cn.net.yzl.base.statusbar.StatusBarBlackOnWhiteUtil;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;



/**
 * Created by dell on 2019-10-23.
 */
@Route(path = ARouterConstant.ACTIVITY_BIGPHOTO_ACTIVITY)
public class BigPhotoActivity extends BaseActivity {
    private ViewPager mViewPager;
    private ImagePagerAdapter mAdapter;
    private ArrayList<String> mViewPositions=new ArrayList<>();
    private ArrayList<String> mPhotoUrlList = new ArrayList<>();






    @Override
    protected int getLayoutId() {
        return R.layout.activity_bigphoto;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        //避免在状态栏的显示状态发生变化时重新布局
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        ARouter.getInstance().inject(this);
        StatusBarBlackOnWhiteUtil.setStatusBarColorAndFontColor(this, 0);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            mPhotoUrlList = bundle.getStringArrayList(Constant.BaseModule.BIGIMG_URL);
            mViewPositions = bundle.getStringArrayList(Constant.BaseModule.BIGIMG_POS);
        }
        mViewPager.setOffscreenPageLimit(mPhotoUrlList.size());
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mPhotoUrlList, mViewPager,mViewPositions, 0);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
    }


    @Override
    protected void initData() {

    }

    protected void initEvents() {

    }


}
