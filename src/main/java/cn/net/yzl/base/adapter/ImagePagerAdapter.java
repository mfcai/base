package cn.net.yzl.base.adapter;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import cn.net.yzl.base.activity.BigPhotoFragment;

/**
 * by sxh
 */

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private ViewPager mPager;
    private ArrayList<Fragment> mFragmentList=new ArrayList<>();
    private ArrayList<String> mViewPosition=new ArrayList<>();
    private int mSelectPosition;

    public ImagePagerAdapter(FragmentManager fm, List<String> datas, ViewPager pager, ArrayList<String> viewPosotion, int position) {
        super(fm);
        mPager=pager;
        mPager.setAdapter(this);
        mViewPosition=viewPosotion;
        mSelectPosition=position;
        updateData(datas);
    }


    public void updateData(List<String> dataList) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0, size = dataList.size(); i < size; i++) {
            BigPhotoFragment fragment = BigPhotoFragment.newInstance(dataList.get(i),dataList);
            fragment.setViewPager(mPager);
            fragment.setViewPosition(mViewPosition,mSelectPosition);
            fragments.add(fragment);
        }
        setViewList(fragments);
    }

    private void setViewList(ArrayList<Fragment> fragmentList) {
        if (mFragmentList != null) {
            mFragmentList.clear();
        }
        mFragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFragmentList==null?0:mFragmentList.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


}