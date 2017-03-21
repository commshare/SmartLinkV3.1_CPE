package com.alcatel.smartlinkv3.common;


import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by pifangsi on 15-12-30.
 */
public class RegexTextWatcher implements TextWatcher {

    private String mRegexMatch;
    private EditText mEditText;
    private String mLastText;
    private int mSelectionStart;
    private int mSelectionEnd;
    //   private Pattern mMultiBlankPattern;
    public RegexTextWatcher(@NonNull String regex, @NonNull EditText editText)
    {
        mRegexMatch = regex;
        mEditText = editText;
        //       mMultiBlankPattern = Pattern.compile(Constants.MUTIL_BLANK_INPUT_MATCH);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start,
                                  int count, int after)
    {
        mLastText = s.toString();
        mSelectionStart = mEditText.getSelectionStart();
        mSelectionEnd = mEditText.getSelectionEnd();
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        String newText = s.toString();
        //            || mMultiBlankPattern.matcher(newText).find()
        if ((!newText.isEmpty() && !mRegexMatch.isEmpty() && !newText.matches(mRegexMatch)))
        {
            int start = mSelectionStart;
            int end = mSelectionEnd;
            mEditText.setText(mLastText);
            mEditText.setSelection(start, end);
        }
    }
}
