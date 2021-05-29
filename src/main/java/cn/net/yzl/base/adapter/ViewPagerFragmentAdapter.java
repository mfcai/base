package cn.net.yzl.base.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {
    List<Fragment> mFragmentList;
    public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    public void setListFragments(List<Fragment> listFragments){
        this.mFragmentList = listFragments;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return mFragmentList.get(0);
            case 1:
                return mFragmentList.get(1);
            case 2:
                return mFragmentList.get(2);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
