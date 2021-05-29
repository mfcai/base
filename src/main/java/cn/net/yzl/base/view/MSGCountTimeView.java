package cn.net.yzl.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;


import androidx.appcompat.widget.AppCompatTextView;

import cn.net.yzl.base.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 短信倒计时控件<br>
 * 1.可以在代码和布局中设置属性 <br>
 * 2.修复了倒计时时间错乱问题，提供了默认的设置 <br>
 * 3.提供了倒计时完成和进行时的监听<br>
 * 4.控件的样式在布局或者代码中如同TextView的用法设置<br>
 *
 * @author yuhao 2016年3月18日
 */
public class MSGCountTimeView extends AppCompatTextView {
	// handler的Message
	private static final int COUNTTIME = 1;

	// 提供默认的设置
	private static final String INITTEXT = "获取验证码";
	private static final String PREFIXRUNTEXT = "";
	private static final String SUFFIXRUNTEXT ="" ;//"秒"
	private static final String FINISHTEXT = "点击重新获取";
	private static final int TOTALTIME = 60 * 1000;
	private static final int ONETIME = 1000;
	private static final int COLOR = Color.RED;

	// 来自布局文件中的属性设置
	private String mInittext;// 初始化文本
	private String mPrefixRuntext;// 运行时的文本前缀
	private String mSuffixRuntext;// 运行时的文本后缀
	private String mFinishtext;// 完成倒计时后的文本显示
	private int mTotaltime;// 倒计时的总时间
	private int mOnetime;// 一次时间
	private int mColor;

	// 实际使用的总时间
	private int Totaltime;

	// 判断是否在倒计时中，防止多次点击
	private boolean isRun;

	private String isStart="1";
	// 处理倒计时的方法

	// 记录按下时间
	private long mLastActionDownTime = 0L;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case COUNTTIME:
					// 对秒数进行格式化
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date();
					String strTotaltime = sdf.format(date);
					String runtimeText = mInittext+"\n" + strTotaltime ;

					// 对秒数进行颜色设置
					Spannable spannable = new SpannableString(runtimeText);
					ForegroundColorSpan redSpan = new ForegroundColorSpan(mColor);
					spannable.setSpan(redSpan, mPrefixRuntext.length(), mPrefixRuntext.length() + strTotaltime.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					spannable.setSpan(new RelativeSizeSpan(0.8f), mPrefixRuntext.length(), mPrefixRuntext.length() + strTotaltime.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					setText(spannable);
					Totaltime -= mOnetime;

//					if (Totaltime < 0) {
//						MSGCountTimeView.this.setText(mFinishtext);
//						setTextColor(getResources().getColor(R.color.color_font_359bff));
//						isRun = false;
//						clearTimer();
//						mDownTime.onFinish();
//					}
					break;

				default:
					break;
			}
		}
	};

	/**
	 * 倒计时的监听
	 */
	private onDownTime mDownTime = new onDownTime() {

		@Override
		public void onFinish() {

		}

		@Override
		public void onDown() {

		}

		@Override
		public void notifyTime() {

		}

		@Override
		public void onStart() {

		}

		@Override
		public void onClick() {

		}
	};

	public MSGCountTimeView(Context context) {
		this(context, null);
	}

	public MSGCountTimeView(Context context, AttributeSet attrs) {
		// 如果不写android.R.attr.textViewStyle会丢失很多属性
		this(context, attrs, android.R.attr.textViewStyle);
	}

	public MSGCountTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// 1. 在布局文件中提供设置
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MSGCountTimeView);
		mInittext = ta.getString(R.styleable.MSGCountTimeView_inittext);
		mPrefixRuntext = ta.getString(R.styleable.MSGCountTimeView_prefixruntext);
		mSuffixRuntext = ta.getString(R.styleable.MSGCountTimeView_suffixruntext);
		mFinishtext = ta.getString(R.styleable.MSGCountTimeView_finishtext);
		mTotaltime = ta.getInteger(R.styleable.MSGCountTimeView_totaltime, TOTALTIME);
		mOnetime = ta.getInteger(R.styleable.MSGCountTimeView_onetime, ONETIME);
		mColor = ta.getColor(R.styleable.MSGCountTimeView_timecolor, COLOR);
		ta.recycle();
		// 2.代码设置值
		// 3.如果布局和代码都没有设置，则给予默认值
		initData();
		initTimer();

		if (!isRun) {
			// 每次开始倒计时时初始化
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(COUNTTIME);
					mDownTime.notifyTime();
					mHandler.postDelayed(this, 1000);
				}
			},1000);
			isRun = true;
		}
//		if(!this.isEnabled()){
//			this.setTextColor(getResources().getColor(R.color.color_font_9dceff));
//		}
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 如果为空，则设置默认的值

		if (TextUtils.isEmpty(mInittext)) {
			mInittext = INITTEXT;
		}
		if (TextUtils.isEmpty(mPrefixRuntext)) {
			mPrefixRuntext = PREFIXRUNTEXT;
		}
		if (TextUtils.isEmpty(mSuffixRuntext)) {
			mSuffixRuntext = SUFFIXRUNTEXT;
		}
		if (TextUtils.isEmpty(mFinishtext)) {
			mFinishtext = FINISHTEXT;
		}
		if (mTotaltime < 0) {
			mTotaltime = TOTALTIME;
		}
		if (mOnetime < 0) {
			mOnetime = ONETIME;
		}
		setText(mInittext);
	}

	/**
	 * 初始化时间
	 */
	private void initTimer() {
		Totaltime = mTotaltime;

	}

//	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if(!this.isEnabled()) {
				return true;
			}
			mLastActionDownTime = System.currentTimeMillis();
			mDownTime.onDown();
			Log.d("caimingfu","clicked timeview");
			mDownTime.onClick();

		}else if(event.getAction() == MotionEvent.ACTION_UP){
			mDownTime.onFinish();

		}
		return true;
	}

	/**
	 * 清除时间
	 */
	public void clearTimer() {
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	/**
	 * 设置初始化的文字
	 *
	 * @param str
	 * @return
	 */
	public MSGCountTimeView setInittext(String str) {
		this.mInittext = str;
		if(!TextUtils.isEmpty(str) && !str.equals(mInittext)) {
			setText(mInittext);
		}
		return this;
	}

	/**
	 * 设置运行时的文字前缀
	 *
	 * @param mPrefixRuntext
	 * @return
	 */
	public MSGCountTimeView setPrefixRuntext(String mPrefixRuntext) {
		this.mPrefixRuntext = mPrefixRuntext;
		return this;
	}

	/**
	 * 设置运行时的文字后缀
	 *
	 * @param mSuffixRuntext
	 * @return
	 */
	public MSGCountTimeView setSuffixRuntext(String mSuffixRuntext) {
		this.mSuffixRuntext = mSuffixRuntext;
		return this;
	}

	/**
	 * 设置结束的文字
	 *
	 * @param mFinishtext
	 * @return
	 */
	public MSGCountTimeView setFinishtext(String mFinishtext) {
		this.mFinishtext = mFinishtext;
		return this;
	}

	/**
	 * 设置倒计时的总时间
	 *
	 * @param mTotaltime
	 * @return
	 */
	public MSGCountTimeView setTotaltime(int mTotaltime) {
		this.mTotaltime = mTotaltime;
		return this;
	}

	/**
	 * 设置一次倒计时的时间
	 *
	 * @param mOnetime
	 * @return
	 */
	public MSGCountTimeView setOnetime(int mOnetime) {
		this.mOnetime = mOnetime;
		return this;
	}

	/**
	 * 设置默认倒计时秒数的颜色
	 *
	 * @param mColor
	 * @return
	 */
	public MSGCountTimeView setTimeColor(int mColor) {
		this.mColor = mColor;
		return this;
	}

	/**
	 * 对外提供接口，编写倒计时时和倒计时完成时的操作
	 *
	 * @author yuhao 2016年3月15日
	 */
	public interface onDownTime {
		void onDown();
		void notifyTime();
		void onFinish();
		void onStart();
		void onClick();

	}

	public void onDownTime(onDownTime mDownTime) {
		this.mDownTime = mDownTime;
	}

	/**
	 * 窗口销毁时，倒计时停止
	 */
	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		clearTimer();
	}


	/**
	 * 是否允许倒计时
	 */
	public void isisRun(Boolean isRun) {
		this.isRun = isRun;
	}
	public void isStart(String isStart) {
		this.isStart = isStart;
	}
}
