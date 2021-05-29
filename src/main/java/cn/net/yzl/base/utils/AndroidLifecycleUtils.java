package cn.net.yzl.base.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class AndroidLifecycleUtils {
    public static boolean isActivityDestroy(Fragment fragment) {
        if (fragment == null) {
            return false;
        }

        FragmentActivity activity = fragment.getActivity();

        return isActivityDestroy(activity);
    }

    public static boolean isActivityDestroy(Context context) {
        if (context == null) {
            return false;
        }

        if (!(context instanceof Activity)) {
            return true;
        }

        Activity activity = (Activity) context;
        return isActivityDestroy(activity);
    }

    public static boolean isActivityDestroy(Activity activity) {
        if (activity == null) {
            return false;
        }

        boolean destroyed = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                activity.isDestroyed();

        if (destroyed || activity.isFinishing()) {
            return false;
        }

        return true;
    }
}
