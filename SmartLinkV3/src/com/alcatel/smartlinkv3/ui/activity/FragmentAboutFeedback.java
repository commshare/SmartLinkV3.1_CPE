package com.alcatel.smartlinkv3.ui.activity;

import java.io.FileNotFoundException;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.view.SquareLayout;

import android.app.Fragment;
import android.content.ContentResolver;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FragmentAboutFeedback extends Fragment implements OnClickListener{
	
	private final int RESULT_OK = 101;
	private final int RESULT_CODE = 1;
	
	private SettingNewAboutActivity m_parent_activity;
	private SquareLayout m_add_photo;
	private LinearLayout m_add_photo_container;
	private SquareLayout m_photo;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  {
		
		View rootView = inflater.inflate(R.layout.fragment_about_feedback, container, false);  
		initUi(rootView);
        return rootView;
    }

	private void initUi(View rootView) {
		// TODO Auto-generated method stub
		m_parent_activity = (SettingNewAboutActivity)getActivity();
		m_add_photo_container = (LinearLayout)rootView.findViewById(R.id.setting_about_feedback_add_picture_container);
		m_add_photo = (SquareLayout) rootView.findViewById(R.id.setting_about_feedback_add_picture);
		m_add_photo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_photo = new SquareLayout(getActivity());
//				m_photo.setBackgroundColor(Color.BLACK);
				LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);  
				layoutParams.setMargins(0, 0, 20, 0);
				m_photo.setLayoutParams(layoutParams);
				Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_CODE);  
				m_add_photo_container.addView(m_photo, 0);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		super.onActivityResult(requestCode, resultCode, data);  
        if (requestCode == RESULT_CODE && null != data) {  
        	 Uri selectedImage = data.getData();
             String[] filePathColumn = { MediaStore.Images.Media.DATA };
  
             Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                     filePathColumn, null, null, null);
             cursor.moveToFirst();
  
             int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
             String picturePath = cursor.getString(columnIndex);
             cursor.close();
             
             Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
             BitmapDrawable bd= new BitmapDrawable(getActivity().getResources(), bitmap); 
             m_photo.setBackground(bd);
        }  
        
    }  

}
