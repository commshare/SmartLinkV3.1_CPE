package com.alcatel.wifilink.appwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.alcatel.wifilink.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wdpm
 *         use handler and  message to implement count down
 */
public class CountDownTextView extends TextView {
    private Paint mPaint;
    private int mFontHeight = 0;
    private Shader mShader;

    private static final int DEFAULT_COUNT = 60;
    private static final Typeface DEFAULT_TYPE_FACE = Typeface.SERIF;
    private static final int DEFAULT_TOP_COLOR = Color.BLACK;                                                // 默认颜色上部分黑色
    private static final int DEFAULT_BOTTOM_COLOR = Color.BLACK;                                                // 默认颜色下部分黑色

    private int mCount = DEFAULT_COUNT;
    private boolean isRunning = false;

    private int mUserCount = DEFAULT_COUNT;
    private Typeface mTypeface = DEFAULT_TYPE_FACE;
    private int mTopColor = DEFAULT_TOP_COLOR;
    private int mBottomColor = DEFAULT_BOTTOM_COLOR;

    private String mText = "60";
    private float mTextSize;

    public int getBottomColor() {
        return mBottomColor;
    }

    public void setBottomColor(int mBottomColor) {
        this.mBottomColor = mBottomColor;
    }

    public int getTopColor() {
        return mTopColor;
    }

    public void setTopColor(int mTopColor) {
        this.mTopColor = mTopColor;
    }

    public CountDownTextView(Context context) {
        super(context);
        init(null, null);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        //        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleText);
        //        mBottomColor = array.getColor(R.styleable.ScaleText_bottomColor, getTextColors().getDefaultColor());
        //        mTopColor = array.getColor(R.styleable.ScaleText_topColor, getTextColors().getDefaultColor());
        //        array.recycle();

    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            readAttrs(context, attrs);
        }
        mPaint = new Paint();
        setText(mText);
    }

    private void readAttrs(Context context, AttributeSet attrs) {
        int[] attrsArray = new int[]{android.R.attr.text, android.R.attr.textSize, R.attr.topColor, R.attr.bottomColor,

        };

        TypedArray ta = getContext().obtainStyledAttributes(attrs, attrsArray);
        mText = (String) ta.getText(0);
        if (TextUtils.isEmpty(mText)) {
            mText = "Begin";
        }
        mTextSize = ta.getDimension(1, mTextSize);
        mTopColor = ta.getColor(2, mTopColor);
        mBottomColor = ta.getColor(3, mBottomColor);
        mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/monos.ttf");

        ta.recycle();
        this.setTypeface(mTypeface);
        if (CanParseToInt(mText)) {
            this.setCount(Integer.parseInt(mText));
            Log.d("readAttrs", Integer.parseInt(mText) + "");

        } else {
            this.setCount(DEFAULT_COUNT);
        }
    }

    private boolean CanParseToInt(String mText) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(mText);
        boolean result = matcher.matches();
        Log.d("CanParseToInt", result + "");
        return result;

    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
        this.mUserCount = mCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mFontHeight == 0) {
            mPaint = getPaint();
            mFontHeight = getMeasuredHeight() * 7 / 9;
            if (mFontHeight > 0) {
                mShader = new LinearGradient(0, 0, 0, mFontHeight, mTopColor, mBottomColor, Shader.TileMode.CLAMP);
                mPaint.setShader(mShader);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("handleMessage", "arg1" + String.valueOf(msg.arg1));
            Log.d("handleMessage", "arg2" + String.valueOf(msg.arg2));
            Log.d("handleMessage", "describeContents" + String.valueOf(msg.describeContents()));
            Log.d("handleMessage", "what" + String.valueOf(msg.what));
            Log.d("handleMessage", "getTarget" + String.valueOf(msg.getTarget()));
            Log.d("handleMessage", "getData" + String.valueOf(msg.getData()));
            switch (msg.what) {
                case 1:
                    mCount--;
                    if (mCount < 0) {
                        return;
                    }
                    CountDownTextView.this.setText(mCount + "");
                    invalidate();
                    Message message = mHandler.obtainMessage(1);
                    mHandler.sendMessageDelayed(message, 1000);
                    Log.d("mCount2:", mCount + "");
                case 2:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void run() {
        if (isRunning) {//true,represent count is running. no need to run repeatly
            return;
        } else {
            isRunning = true;// set run state true
            Message message = mHandler.obtainMessage(1);
            mHandler.sendMessage(message);
        }
    }

    public void pause() {
        if (isRunning) {
            mHandler.removeMessages(1);
            isRunning = false;
        }
    }

    public void reset() {
        if (isRunning) {
            mHandler.removeMessages(1);
        } else {
            //don't need to remove message
        }
        isRunning = false;
        mCount = mUserCount;
        Log.d("reset", "mCount:" + mCount);
        CountDownTextView.this.setText(mUserCount + "");
        invalidate();
    }

}
