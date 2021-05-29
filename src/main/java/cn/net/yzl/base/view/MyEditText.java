package cn.net.yzl.base.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.net.yzl.base.R;


@SuppressLint("AppCompatCustomView")
public class MyEditText extends EditText {
	private Drawable imgEnable;
	private String code;
	private Context context;
	private Activity activity;
	private boolean canClean = true;
	private boolean isNumber = false;//语音识别判断是否为数字3
	private boolean needBottom = true;//判断是否需要与下部间隔
	Drawable leftDrawable;
	public MyEditText(Context context) {

		super(context);

		this.context = context;
		init();
	}

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public MyEditText(Context context, AttributeSet attrs) {

		super(context, attrs);

		this.context = context;

		init();

	}

	public Activity getActivity() {
		return activity;
	}

	public boolean isNumber() {
		return isNumber;
	}

	public void setNumber(boolean number) {
		isNumber = number;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void SetImgEnable(int img){
		imgEnable = context.getResources().getDrawable(img);
	}

	private void init() {

		initWedgits();
		//获取图片资源
		imgEnable = context.getResources().getDrawable(R.mipmap.icon_edt_clear);
		addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,

										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setDrawable();
			}

		});

//		setDrawable();

	}
	/**
	 * 获取drawableLeft图片
	 *
	 */
	private void initWedgits() {
		try {
			// 获取drawableLeft图片，如果在布局文件中没有定义drawableLeft属性，则此值为空
			leftDrawable = getCompoundDrawables()[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 设置删除图片
	 */

	private void setDrawable() {
		if(length() == 0) {
			setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

		}else {
			if(canClean){
				setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, imgEnable, null);
			}

		}
	}


	/**
	 * event.getX() 获取相对应自身左上角的X坐标
	 * event.getY() 获取相对应自身左上角的Y坐标
	 * getWidth() 获取控件的宽度
	 * getTotalPaddingRight() 获取删除图标左边缘到控件右边缘的距离
	 * getPaddingRight() 获取删除图标右边缘到控件右边缘的距离
	 * getWidth() - getTotalPaddingRight() 计算删除图标左边缘到控件左边缘的距离
	 * getWidth() - getPaddingRight() 计算删除图标右边缘到控件左边缘的距离
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(imgEnable != null && event.getAction() == MotionEvent.ACTION_UP) {
			int x = (int) event.getX() ;
			//判断触摸点是否在水平范围内
//			boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight()-10)) &&
//					(x < (getWidth() - getPaddingRight()));
			boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) &&
					(x < getWidth());
			//获取删除图标的边界，返回一个Rect对象
			Rect rect = imgEnable.getBounds();
			//获取删除图标的高度.
//			int height = rect.height();
			int height = getHeight();
			int y = (int) event.getY();
			//计算图标底部到控件底部的距离
			int distance = (getHeight() - height) /2;
			//判断触摸点是否在竖直范围内(可能会有点误差)
			//触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
			boolean isInnerHeight = (y > distance) && (y < (distance + height));

			if(isInnerWidth && isInnerHeight) {
				if(canClean){
					setText("");
				}
			}

		}



		return super.onTouchEvent(event);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		if (focused){
			setDrawable();
//			详见VoiceUtil.voice_gong 注释
//			VoiceUtil.fuzhi(MyEditText.this);
		}else {
			setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
		}
	}

	public boolean isCanClean() {
		return canClean;
	}

	public void setCanClean(boolean canCleans) {
		this.canClean = canCleans;
		if(canCleans){
			setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, imgEnable, null);
		}else{
			setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
		}

	}

	@Override
	public void setEllipsize(TextUtils.TruncateAt ellipsis) {
		if (ellipsis == TextUtils.TruncateAt.MARQUEE) {
			throw new IllegalArgumentException("EditText cannot use the ellipsize mode "
					+ "TextUtils.TruncateAt.MARQUEE");
		}
		super.setEllipsize(ellipsis);
	}




	public void initHeightDifference() {
		Rect r = new Rect();
		//获取当前界面可视部分
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
		//获取屏幕的高度
		int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
		//此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
//		int heightDifference = screenHeight - r.bottom - VoiceUtil.getSoftButtonsBarHeight();
//		showAViewOverKeyBoard(heightDifference);

	}
	private View view;
	private RelativeLayout search_Rl;
	private RelativeLayout rl_add_clue;
	private TextView textView;



	public boolean isNeedBottom() {
		return needBottom;
	}

	public void setNeedBottom(boolean needBottom) {
		this.needBottom = needBottom;
	}
}
