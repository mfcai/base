package cn.net.yzl.base.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import androidx.annotation.RequiresApi;

public class MyExpandableListView extends ExpandableListView {
    public MyExpandableListView(Context context) {
        super(context);
    }

    public MyExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void scroll(int groupIndex, int childIndex) {
        int position = 0;
        for (int i = 0; i <= groupIndex; i++) {
            position++;
            if (isGroupExpanded(i)){
                position = position + getExpandableListAdapter().getChildrenCount(i);
            }
        }
        position++;
        position = position + childIndex;
        super.smoothScrollToPosition(position);
    }
}
