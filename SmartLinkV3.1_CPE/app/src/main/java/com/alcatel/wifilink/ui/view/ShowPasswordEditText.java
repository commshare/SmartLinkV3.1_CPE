package com.alcatel.wifilink.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.alcatel.wifilink.R;

import static android.R.attr.editable;


/**
 * Created by ZQ on 2017/1/24.
 */

public class ShowPasswordEditText extends android.support.v7.widget.AppCompatEditText {

    private DrawableRightListener mRightListener;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;
    boolean mIsShow;

    public interface DrawableRightListener {
        void onDrawableRightClick(View view);
    }


    public ShowPasswordEditText(Context context) {
        super(context);
    }

    public ShowPasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowPasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //                if (mRightListener != null) {
                Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
                if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                    //                        mRightListener.onDrawableRightClick(this) ;
                    if (!mIsShow) {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_show, 0);
                        setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);
                        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    // 屏蔽剪貼板
                    this.setLongClickable(false);
                    // 光標移動到最後
                    int length = getEditableText().toString().length();
                    setSelection(length);
                    mIsShow = !mIsShow;
                    return true;
                }
                break;
        }

        return super.onTouchEvent(event);
    }


}
