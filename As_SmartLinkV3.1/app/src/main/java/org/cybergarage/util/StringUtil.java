/******************************************************************
*
*	CyberUtil for Java
*
*	Copyright (C) Satoshi Konno 2002-2003
*
*	File: FileUtil.java
*
*	Revision:
*
*	01/12/03
*		- first revision.
*
******************************************************************/

package org.cybergarage.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil
{
	public final static boolean hasData(String value)
	{
		if (value == null)
			return false;
		if (value.length() <= 0)
			return false;
		return true;
	}
	
	public final static int toInteger(String value)
	{
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			Debug.warning(e);
		}
		return 0;
	}

	public final static long toLong(String value)
	{
		try {
			return Long.parseLong(value);
		}
		catch (Exception e) {
			Debug.warning(e);
		}
		return 0;
	}

	public final static int findOf(String str, String chars, int startIdx, int endIdx, int offset, boolean isEqual)
	{
		if (offset == 0)
			return -1;
		int charCnt = chars.length();
		int idx = startIdx;
		while (true) {
			if (0 < offset) {
				if (endIdx < idx)
					break; 
			}
			else {
				if (idx < endIdx)
					break; 
			}
			char strc = str.charAt(idx);
			int noEqualCnt = 0;
			for (int n=0; n<charCnt; n++) {
				char charc = chars.charAt(n);
				if (isEqual == true) {
					if (strc == charc)
						return idx;
				}
				else {
					if (strc != charc)
						noEqualCnt++;
					if (noEqualCnt == charCnt)
						return idx;
				}
			}
			idx += offset;
		}
		return -1;
	}
	
	public final static int findFirstOf(String str, String chars)
	{
		return findOf(str, chars, 0, (str.length()-1), 1, true);
	}
	
	public final static int findFirstNotOf(String str, String chars)
	{
		return findOf(str, chars, 0, (str.length()-1), 1, false);
	}
	
	public final static int findLastOf(String str, String chars)
	{
		return findOf(str, chars, (str.length()-1), 0, -1, true);
	}
	
	public final static int findLastNotOf(String str, String chars)
	{
		return findOf(str, chars, (str.length()-1), 0, -1, false);
	}
	
	public final static String trim(String trimStr, String trimChars)
	{
		int spIdx = findFirstNotOf(trimStr, trimChars);
		if (spIdx < 0) {
			String buf = trimStr;
			return buf;
		}
		String trimStr2 = trimStr.substring(spIdx, trimStr.length());
		spIdx = findLastNotOf(trimStr2, trimChars);
		if (spIdx < 0) {
			String buf = trimStr2;
			return buf;
		}
		String buf = trimStr2.substring(0, spIdx+1);
		return buf;
	}

    /**
     * 禁止EditText输空格+换行键
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" ")||source.toString().contentEquals("\n"))return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 禁止EditText输入特殊字符
     * @param editText
     */
    public static void setEditTextInhibitInputSpeChat(EditText editText){

        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 禁止EditText输入特殊字符+空格+换行键
     * @param editText
     */
    public static void setEditTextInputFilter(EditText editText){

        InputFilter filterSpace=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" ")||source.toString().contentEquals("\n"))return "";
                else return null;
            }
        };

        InputFilter filterChat=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filterSpace, filterChat});
    }
}

