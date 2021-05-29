package cn.net.yzl.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import android.util.Log;
import android.widget.ImageView;


import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


public class GlideLoadUtils {
    private static String TAG = "GlideLoadUtils";

    /**
     * 借助内部类 实现线程安全的单例模式
     * 属于懒汉式单例，因为Java机制规定，内部类SingletonHolder只有在getInstance()
     * 方法第一次调用的时候才会被加载（实现了lazy），而且其加载过程是线程安全的。
     * 内部类加载的时候实例化一次instance。
     */
    public GlideLoadUtils() {
    }

    private static class GlideLoadUtilsHolder {
        private final static GlideLoadUtils INSTANCE = new GlideLoadUtils();
    }

    public static GlideLoadUtils getInstance() {
        return GlideLoadUtilsHolder.INSTANCE;
    }

    /**
     * Glide 加载 简单判空封装 防止异步加载数据时调用Glide 抛出异常
     *
     * @param context
     * @param url       加载图片的url地址  String
     * @param imageView 加载图片的ImageView 控件
     */
    public static void glideLoad(Context context, String url, ImageView imageView) {
        if (isActivityAlive((Activity) context)) {
            Glide.with(context).load(url).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    public static void glideLoad(Context context, int resourceId, ImageView imageView) {
        if (isActivityAlive((Activity) context)) {
            Glide.with(context).load(resourceId).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    public static void glideLoad(Context context, Uri uri, ImageView imageView) {
        if (isActivityAlive((Activity) context)) {
            Glide.with(context).load(uri).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    public static void glideLoad(Context context, String url, ImageView imageView, int default_image) {
        if (isActivityAlive((Activity) context)) {
            Glide.with(context).load(url).apply(new RequestOptions().error(default_image)).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void glideLoad(Activity activity, String url, ImageView imageView) {
        if (isActivityAlive(activity)) {
            Glide.with(activity).load(url).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,activity is Destroyed");
        }
    }

    public void glideLoad(Fragment fragment, String url, ImageView imageView, int default_image) {
        if (fragment != null && fragment.getActivity() != null) {
            Glide.with(fragment).load(url).apply(new RequestOptions().error(default_image)).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,fragment is null");
        }
    }

    public static boolean isActivityAlive(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return false;
        } else {
            return true;
        }
    }

}
