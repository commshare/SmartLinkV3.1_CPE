/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alcatel.smartlinkv3.fileexplorer;

import com.alcatel.smartlinkv3.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextInputDialog extends AlertDialog {
	private String mInputString;
	private String mOptionTitle;
	private String mTips;
	private OnFinishListener mListener;
	private Context mContext;
	private View mView;
	private EditText mEditText;
	private TextView mOTTextView;
	private TextView mTipsTextView;

	public interface OnFinishListener {
		// return true to accept and dismiss, false reject
		boolean onFinish(String text);
	}

	public TextInputDialog(Context context, String optionTitle, String tips,
			OnFinishListener listener) {
		super(context);
		mContext = context;
		mOptionTitle = optionTitle;
		mTips = tips;
		mListener = listener;
	}

	public String getInputText() {
		return mInputString;
	}

	protected void onCreate(Bundle savedInstanceState) {
		mView = getLayoutInflater().inflate(R.layout.textinput_dialog, null);

		mOTTextView = (TextView) mView.findViewById(R.id.option_title);
		mTipsTextView = (TextView) mView.findViewById(R.id.tips);
		mEditText = (EditText) mView.findViewById(R.id.input_edit_text);

		mOTTextView.setText(mOptionTitle);
		if (mTips == null) {
			mTipsTextView.setVisibility(View.GONE);
		} else {
			mTipsTextView.setText(mTips);
		}

		setView(mView);
		setButton(BUTTON_POSITIVE, mContext.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mInputString = mEditText.getText().toString();
						if (mListener.onFinish(mInputString)) {
							dismiss();
						}
					}
				});
		setButton(BUTTON_NEGATIVE, mContext.getString(R.string.cancel),
				(DialogInterface.OnClickListener) null);

		super.onCreate(savedInstanceState);
	}
}
