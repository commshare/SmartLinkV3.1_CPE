package com.alcatel.smartlinkv3.ui.view;

import com.alcatel.smartlinkv3.R;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

public class SearchEditText extends FrameLayout implements OnClickListener {
	private EditText m_editText;  
    private ImageButton m_deleteButton;
    private ImageButton m_searchButton;
    private ProgressBar m_waitingProgressBar;
    private Context m_context;
    private OnSearch m_searchCallBack = null;
    private LinearLayout m_focuseTool;

	public SearchEditText(Context context) {
		super(context);
		m_context = context;
		init();
	}

	public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		m_context = context;
		init();
	}

	public SearchEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_context = context;
		init();
	}
	
	private void init() {
		LayoutInflater.from(m_context).inflate(R.layout.search_edit_layout, this, true);
        this.m_editText = (EditText)findViewById(R.id.edit_text);
        addEditorListener();
        this.m_deleteButton = (ImageButton)findViewById(R.id.delete_btn);  
        this.m_deleteButton.setOnClickListener(this); 
        this.m_searchButton = (ImageButton)findViewById(R.id.search_btn);  
        this.m_waitingProgressBar = (ProgressBar)findViewById(R.id.waiting_progress);  
        m_focuseTool = (LinearLayout)findViewById(R.id.focuse_tool);   
        addTextWatcherListener();   
	}
	
	private void addEditorListener() {
		m_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				// EditorInfo.IME_ACTION_UNSPECIFIED use for 3-rd input
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					search();
					
					m_focuseTool.requestFocus();
					InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(m_editText.getWindowToken(), 0);
				}

				return false;
			}

		});
		
		m_editText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus == true) {
					if(m_searchCallBack != null)
						m_searchCallBack.onEndSearch();
					endSearch();
				}
			}
			
		});
	}
	
	public void endSearch() {
		m_searchButton.setVisibility(View.VISIBLE);
		m_waitingProgressBar.setVisibility(View.INVISIBLE);
		String strText = m_editText.getEditableText().toString();
		if(strText != null && strText.length() > 0)
			m_deleteButton.setVisibility(View.VISIBLE);
		else
			m_deleteButton.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.delete_btn:
			deleteBtnClick();
			break;
		}
	}
	
	public String getSearchText() {
		return m_editText.getEditableText().toString();
	}
	
	private void search() {
		String strSearch = m_editText.getEditableText().toString();
		if(strSearch == null)
			return;
		//strSearch = strSearch.trim();
		if(strSearch.length() == 0)
			return;
		m_searchButton.setVisibility(View.INVISIBLE);
		m_waitingProgressBar.setVisibility(View.VISIBLE);
		m_deleteButton.setVisibility(View.INVISIBLE);
		if(m_searchCallBack != null)
			m_searchCallBack.onSearch(strSearch);
	}
	
	public void setSearchCallBack(OnSearch callback) {
		m_searchCallBack = callback;
	}
	
	public interface OnSearch
	{
		public void onSearch(String strSearch);
		public void onEndSearch();
	}
	
	private void deleteBtnClick() {
		m_editText.setText(null);  
    	m_deleteButton.setVisibility(View.INVISIBLE);
	}
	
	private void addTextWatcherListener() {
		this.m_editText.addTextChangedListener(new TextWatcher() {  
            @Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {             
                if(!TextUtils.isEmpty(s)){  
                	m_deleteButton.setVisibility(View.VISIBLE);  
                }else{  
                	m_deleteButton.setVisibility(View.GONE);  
                }  
            }  
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {  
                // TODO Auto-generated method stub   
            }  
              
            @Override  
            public void afterTextChanged(Editable s) {  
                // TODO Auto-generated method stub   
            }  
        });  
	}
}