/**
 * 
 */
package cn.net.yzl.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;


import java.lang.ref.SoftReference;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.net.yzl.base.R;
import cn.net.yzl.base.app.LibApplication;
import cn.net.yzl.base.utils.DialogUtils;

public class ToastUtils {

	public static final int LENGTH_SHORT = 0x00;
	public static final int LENGTH_LONG = 0x01;
	private static ToastUtils mInstance;
	// 动画时间
	private final int ANIMATION_DURATION = 600;
	private  SoftReference<TextView> mTextView;
	private  ViewGroup container;
	private   SoftReference<View> mView;
	// 默认展示时间
	private int HIDE_DELAY = 1500;
	private SoftReference<LinearLayout> mContainer;
	private AlphaAnimation mFadeOutAnimation;
	private AlphaAnimation mFadeInAnimation;
	private AtomicBoolean isShow = new AtomicBoolean(false);
	private  Context mContext;
	private Handler mHandler = new Handler();

	private ToastUtils(Context context) {
		mContext = context;
		container = (ViewGroup) ((Activity) context)
				.findViewById(android.R.id.content);

		mView =new SoftReference( ((Activity) context).getLayoutInflater().inflate(
				R.layout.toast_layout, container));
		mContainer =new SoftReference<> ((LinearLayout) mView.get().findViewById(R.id.mbContainer));
		mContainer.get().setVisibility(View.GONE);
		mTextView = new SoftReference<> ((TextView) mView.get().findViewById(R.id.mbMessage));
	}


	public static synchronized void showToast(Context context,String message){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toast_layout, null); //加載layout下的布局
		TextView title = view.findViewById(R.id.mbMessage);
		title.setText(message); //toast的标题
		final Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 12, 20);//setGravity用来设置Toast显示的位置，相当于xml中的android:gravity或android:layout_gravity
		toast.setDuration(Toast.LENGTH_SHORT);//setDuration方法：设置持续时间，以毫秒为单位。该方法是设置补间动画时间长度的主要方法
		toast.setView(view); //添加视图文件
		toast.show();
	}

	public static synchronized ToastUtils makeText(Context context, String message,
									  int HIDE_DELAY) {
		if (mInstance == null) {
			mInstance = new ToastUtils(context);
		} else {
			// 考虑Activity切换时，Toast依然显示
			if (!mInstance.mContext.getClass().getName().endsWith(context.getClass().getName())) {
				mInstance.cancel();
				mInstance.container.removeView(mInstance.mView.get());
				mInstance = new ToastUtils(context);
			}else{
				mInstance.cancel();
			}
		}

		if (HIDE_DELAY == LENGTH_LONG) {
			mInstance.HIDE_DELAY = 2500;
		} else {
			mInstance.HIDE_DELAY = 1500;
		}
		mInstance.mTextView.get().setText(message);
		return mInstance;
	}

	public static ToastUtils makeText(Context context, int resId, int HIDE_DELAY) {
		String mes = "";
		try {
			mes = context.getResources().getString(resId);
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
		return makeText(context, mes, HIDE_DELAY);
	}

	public synchronized void show() {
		if (isShow.get()) {
			// 若已经显示，则不再次显示
			return;
		}
		isShow.set(true);
		// 显示动画
		mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
		// 消失动画
		mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
		mFadeOutAnimation.setDuration(ANIMATION_DURATION);
		mFadeOutAnimation
				.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						// 消失动画后更改状态为 未显示
						isShow.set(false);
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// 隐藏布局，不使用remove方法为防止多次创建多个布局
						mContainer.get().setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});



			mContainer.get().setVisibility(View.VISIBLE);
			mFadeInAnimation.setDuration(ANIMATION_DURATION);
			mContainer.get().startAnimation(mFadeInAnimation);
			mHandler.postDelayed(mHideRunnable, HIDE_DELAY);

	}

	private final Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mContainer.get().startAnimation(mFadeOutAnimation);
		}
	};

	public void cancel() {
		if (isShow.get()) {
			isShow.set(false);
			mContainer.get().setVisibility(View.GONE);
			mHandler.removeCallbacks(mHideRunnable);
		}
	}

	/**
	 * 此方法主要为了解决用户在重启页面后单例还会持有上一个context，
	 * 且上面的mContext.getClass().getName()其实是一样的
	 * 所以使用上还需要在BaseActivity的onDestroy()方法中调用
	 */
	public static void reset() {
		mInstance = null;
	}




}
