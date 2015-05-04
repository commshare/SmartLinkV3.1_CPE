package com.alcatel.smartlinkv3.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.mediaplayer.activity.ContentActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAboutActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingBackupRestoreActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingDeviceActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingUpgradeActivity;
import com.alcatel.smartlinkv3.ui.view.ViewSetting.SettingItem;
import com.alcatel.smartlinkv3.ui.view.ViewSetting.UprgadeAdapter;
import com.alcatel.smartlinkv3.ui.view.ViewSms.SmsAdapter.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ViewMicroSD extends BaseViewImpl{
	
	private final int ITEM_FILE = 0;
	private final int ITEM_MOVIE = 1;
	private final int ITEM_MUSIC = 2;
	private final int ITEM_PICTURES = 3;
	
	private ListView m_MicrosdListView = null;
	private UpitemAdapter adapter;
	private List<MicrosdItem>list;
	
	@Override
	protected void init(){
		m_view = LayoutInflater.from(m_context).inflate(R.layout.view_microsd,
				null);
		
		m_MicrosdListView = (ListView)m_view.findViewById(R.id.microsdList);
		list = getData(m_context);
		adapter = new UpitemAdapter(m_context, list);
		
		m_MicrosdListView.setAdapter(adapter);
		m_MicrosdListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				switch(position){
				case ITEM_FILE:
					goToFilePage();
					break;
				case ITEM_MOVIE:
					goToDlnaMoviePage();
					break;
				case ITEM_MUSIC:
					goToDlnaMusicPage();
					break;
				case ITEM_PICTURES:
					goToDlnaPicPage();
					break;
				}
			}
		});
	}

	public ViewMicroSD(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
	
	private List<MicrosdItem> getData(Context context){
		List<MicrosdItem> list = new ArrayList<MicrosdItem>();
		
		MicrosdItem item = new MicrosdItem(context.getString(R.string.microsd_file), context.getResources().getDrawable(R.drawable.microsd_item_folder), false);
		list.add(item);
		
		item = new MicrosdItem(context.getString(R.string.microsd_music), context.getResources().getDrawable(R.drawable.microsd_item_music), false);
		list.add(item);
		
		item = new MicrosdItem(context.getString(R.string.microsd_pictures), context.getResources().getDrawable(R.drawable.microsd_item_pictures), false);
		list.add(item);
		
		item = new MicrosdItem(context.getString(R.string.microsd_videos), context.getResources().getDrawable(R.drawable.microsd_item_videos), false);
		list.add(item);
		return list;
	}
	
	public class UpitemAdapter extends BaseAdapter{

		private Context context;
		private List<MicrosdItem> listMicrosdItmes;
		
		public UpitemAdapter(Context context, List<MicrosdItem>list){
			this.context =context;
			this.listMicrosdItmes = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listMicrosdItmes.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listMicrosdItmes.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public final class ViewHolder{
			public ImageView Itemicon;
			public TextView ItemName;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(this.context).inflate(R.layout.microsd_item,null);	
				holder.Itemicon = (ImageView)convertView.findViewById(R.id.microsdItemIcon);
				holder.ItemName = (TextView)convertView.findViewById(R.id.microsdItemText);
				convertView.setTag(holder);	
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			MicrosdItem item = listMicrosdItmes.get(position);
			
			holder.Itemicon.setBackgroundDrawable(item.getDrawableId());
			holder.ItemName.setText(item.getItemName());
			
			/*是否显示*/
//			Boolean blShowFlag = item.getIsShowFlag();
//			if(blShowFlag){
//				itemView.setVisibility(View.VISIBLE);				
//			}else{
//				itemView.setVisibility(View.GONE);
//			}
			return convertView;
		}
	}
	
	public class MicrosdItem{
		//Item name
		private String m_strItem;
		private Drawable m_drawableId ;
		private Boolean m_IsShowFlag;
		
		public MicrosdItem(String strItem, Drawable mdrawableid,Boolean IsShowFlag){
			super();
			this.m_strItem = strItem;
			this.m_drawableId = mdrawableid;
			this.m_IsShowFlag = IsShowFlag;
		}
		
		public Drawable getDrawableId(){
			return m_drawableId;
		}
		
		public void setDrawableId(Drawable mdrawableid){
			m_drawableId = mdrawableid;
		}
		
		public String getItemName(){
			return m_strItem;
		}
		
		public Boolean getIsShowFlag(){
			return m_IsShowFlag;
		}
		
		public void setIsShowFlag(Boolean IsShowFlag){
			m_IsShowFlag = IsShowFlag;
		}
	}
	
	public void onDLNAClick() {
		// TODO Auto-generated method stub
		
		goContentActivity();
	}
	
	private void goContentActivity(){
		Intent intent = new Intent(this.m_context, ContentActivity.class);
		m_context.startActivity(intent);
	}

	
	private void goToFilePage(){
		//Intent intent = new Intent(m_context, SettingBackupRestoreActivity.class);
		//m_context.startActivity(intent);
	}
	
	private void goToDlnaMoviePage(){
		
	}
	
	private void goToDlnaMusicPage(){
		
	}
	
	private void goToDlnaPicPage(){
		
	}
}
