package com.alcatel.wifilink.ui.view;

import java.util.ArrayList;

import com.alcatel.wifilink.R;

import android.widget.AdapterView.OnItemClickListener;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomSpinner extends Spinner implements OnItemClickListener,OnDismissListener {
    private Context mcontext;
    private PopupWindow mDialog = null;
    ListviewAdapter mAdapters;
    private int m_prePosition = -1;
    OnSpinnerItemSelectedListener mSpinnerItemSelectCallback = null;
    private int mSelectedPosition = 0;
    private ArrayList<String> mlist = new ArrayList<String>();

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mcontext = context;
    }

   /* @SuppressLint("NewApi")
	@Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if(visibility == View.GONE)    
        {         
        	onDetachedFromWindow();
        	if (mDialog != null && mDialog.isShowing()) {
        		mDialog.dismiss();
        		mDialog = null;
            }
        }
    }*/
    
    public void SetOnSpinnerItemSelectedListener(OnSpinnerItemSelectedListener listener) {
    	mSpinnerItemSelectCallback = listener;
    }
    
    public void setAdapter(ArrayAdapter<String> arrayAdapter, ArrayList<String> stringList) {
    	mlist = (ArrayList<String>) stringList.clone();
    	super.setAdapter(arrayAdapter);
    }
    
    private void getPopupWindowInstance() {
    	if(mDialog == null) {
    		LayoutInflater inflater = LayoutInflater.from(getContext());
        	View view = inflater.inflate(R.layout.custom_spinner_dialog, null);
        	ListView listview = (ListView) view.findViewById(R.id.customspinner_list);
        	mAdapters = new ListviewAdapter(mcontext, mlist);
    		listview.setAdapter(mAdapters);
    		listview.setOnItemClickListener(this);
    		
	    	mDialog =  new PopupWindow(view,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			mDialog.setWidth(this.getWidth());
			mDialog.setBackgroundDrawable(new BitmapDrawable());
			mDialog.setOutsideTouchable(true);
			mDialog.showAsDropDown(this);
			mDialog.setOnDismissListener(this);
    	}
    }
    
    @Override
    public boolean performClick() {
    	getPopupWindowInstance();
		
        return true;
    }
    
    public void setSelection(int nPosition) {
    	if(m_prePosition == -1)
    		m_prePosition = nPosition;
    	mSelectedPosition = nPosition;
    	super.setSelection(nPosition);
    }
    
    public interface OnSpinnerItemSelectedListener {
        void onSpinnerItemSelected(CustomSpinner view, int position);
    }
    
    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		// TODO Auto-generated method stub
    	setSelection(position);
    	mAdapters.notifyDataSetChanged();
    	if(mSpinnerItemSelectCallback != null && m_prePosition != position)
    		mSpinnerItemSelectCallback.onSpinnerItemSelected(this,position);
    	m_prePosition = position;
        mDialog.dismiss();
        mDialog = null;
	}

    private class ListviewAdapter extends BaseAdapter {
    	private Context context;
    	private ArrayList<String> list;
    	public ListviewAdapter(Context context,ArrayList<String> list){
    		this.context = context;
    		this.list = list;
    	}
    	@Override
    	public int getCount() {
    		// TODO Auto-generated method stub
    		return list.size();
    	}

    	@Override
    	public Object getItem(int arg0) {
    		// TODO Auto-generated method stub
    		return arg0;
    	}

    	@Override
    	public long getItemId(int arg0) {
    		// TODO Auto-generated method stub
    		return arg0;
    	}
    	
    	public final class ViewHolder{
			public TextView item;
		}
    	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item,null);	
				holder.item = (TextView)convertView.findViewById(R.id.custom_spinner_text);
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.item.setText(list.get(position));
			if(mSelectedPosition == position)
				holder.item.setTextColor(getResources().getColor(R.color.color_grey));
			else
				holder.item.setTextColor(getResources().getColor(R.color.color_black));

			return convertView;
		}
    }

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		 mDialog = null;
	}
}
