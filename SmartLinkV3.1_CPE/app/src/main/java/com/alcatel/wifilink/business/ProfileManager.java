package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.wifilink.business.profile.HttpAddNewProfile;
import com.alcatel.wifilink.business.profile.HttpDeleteProfile;
import com.alcatel.wifilink.business.profile.HttpEditProfile;
import com.alcatel.wifilink.business.profile.HttpGetProfileList;
import com.alcatel.wifilink.business.profile.HttpGetProfileList.GetProfileListResult;
import com.alcatel.wifilink.business.profile.HttpGetProfileList.ProfileItem;
import com.alcatel.wifilink.business.profile.HttpSetDefaultProfile;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;

import java.util.List;

public class ProfileManager extends BaseManager{
	
	private List<ProfileItem> m_profile_list = null;;
	
	public List<ProfileItem> GetProfileListData(){
		return m_profile_list;
	}
	
	public void startGetProfileList(DataValue data){
		LegacyHttpClient.getInstance().sendPostRequest(new HttpGetProfileList.GetProfileList(response -> {
						if(response.isOk()) {
							GetProfileListResult profileList = response.getModelResult();
							m_profile_list = profileList.ProfileList;
//							for(ProfileItem tmp : profileList.ProfileList){
//            					Log.v("GetProfileResultAPN", tmp.APN);
//            					Log.v("GetProfileResultDialNumber", tmp.DailNumber);
////            					Log.v("GetProfileResultIPAdress", tmp.IPAddress);
//            					Log.v("GetProfileResultPassword", tmp.Password);
//            					Log.v("GetProfileResultProfileName", tmp.ProfileName);
//            					Log.v("GetProfileResultUserName", tmp.UserName);
//            					Log.v("GetProfileResultAuthType", "" + tmp.AuthType);
//            					Log.v("GetProfileResultDefault", "" + tmp.Default);
//            					Log.v("GetProfileResultIsPredefine","" +  tmp.IsPredefine);
//            					Log.v("GetProfileResultProfileID", "" + tmp.ProfileID);
//            				}

//    			sendBroadcast(response, MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST);
			}
			
		}));
	}
	
	public void startAddNewProfile(DataValue data){
		String ProfileName = (String) data.getParamByKey("profile_name");
		String DialNumber = (String) data.getParamByKey("dial_number");
		String APN = (String) data.getParamByKey("apn");
		String UserName = (String) data.getParamByKey("user_name");
		String Password = (String) data.getParamByKey("password");
		int AuthType = (Integer) data.getParamByKey("auth_type");
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpAddNewProfile.AddNewProfile(ProfileName,
				DialNumber,
				APN,
				UserName,
				Password,
				AuthType,
				response -> {
                if(response.isValid()) {
                startGetProfileList(null);
//    			sendBroadcast(response, MessageUti.PROFILE_ADD_NEW_PROFILE_REQUEST);
			}
			
		}));
	}
	
	
	public void startEditProfile(DataValue data){
		int profileID = (Integer) data.getParamByKey("profile_id");
		String ProfileName = (String) data.getParamByKey("profile_name");
		String DialNumber = (String) data.getParamByKey("dial_number");
		String APN = (String) data.getParamByKey("apn");
		String UserName = (String) data.getParamByKey("user_name");
		String Password = (String) data.getParamByKey("password");
		int AuthType = (Integer) data.getParamByKey("auth_type");
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpEditProfile.EditProfile(profileID, DialNumber, ProfileName, APN, UserName, Password, AuthType,
				response -> {
				if(response.isValid()) {
					startGetProfileList(null);
//					sendBroadcast(response, MessageUti.PROFILE_EDIT_PROFILE_REQUEST);
			}
		}));
	}
	
	
	public void startDeleteProfile(DataValue data){
		int profileId = (Integer) data.getParamByKey("profile_id");
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpDeleteProfile.DeleteProfile(profileId, response -> {
            if(response.isValid()) {
//    			sendBroadcast(response, MessageUti.PROFILE_DELETE_PROFILE_REQUEST);
			}
		}));
		
	}
	
	public void startSetDefaultProfile(DataValue data){
		int profileId = (Integer) data.getParamByKey("profile_id");
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpSetDefaultProfile.SetDefaultProfile(profileId, response -> {
//            sendBroadcast(response, MessageUti.PROFILE_SET_DEFAULT_PROFILE_REQUEST);
		}));
		
	}

	public ProfileManager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void stopRollTimer() {
		// TODO Auto-generated method stub
		
	}

}
