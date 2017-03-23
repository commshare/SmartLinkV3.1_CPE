package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.business.profile.HttpAddNewProfile;
import com.alcatel.smartlinkv3.business.profile.HttpDeleteProfile;
import com.alcatel.smartlinkv3.business.profile.HttpEditProfile;
import com.alcatel.smartlinkv3.business.profile.HttpGetProfileList;
import com.alcatel.smartlinkv3.business.profile.HttpGetProfileList.GetProfileListResult;
import com.alcatel.smartlinkv3.business.profile.HttpGetProfileList.ProfileItem;
import com.alcatel.smartlinkv3.business.profile.HttpSetDefaultProfile;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.List;

public class ProfileManager extends BaseManager{
	
	private List<ProfileItem> m_profile_list = null;;
	
	public List<ProfileItem> GetProfileListData(){
		return m_profile_list;
	}
	
	public void startGetProfileList(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "GetProfileList") != true){
			return;
		}
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpGetProfileList.GetProfileList(new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	try {
						strErrcode = response.getErrorCode();
						if(strErrcode.length() == 0) { 
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
						}else{
							//Log
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }else{
                	//Log
                }
    			sendBroadcast(response, MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST);
			}
			
		}));
	}
	
	public void startAddNewProfile(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "AddNewProfile") != true){
			return;
		}
		
		String ProfileName = (String) data.getParamByKey("profile_name");
		String DialNumber = (String) data.getParamByKey("dial_number");
		String APN = (String) data.getParamByKey("apn");
		String UserName = (String) data.getParamByKey("user_name");
		String Password = (String) data.getParamByKey("password");
		int AuthType = (Integer) data.getParamByKey("auth_type");
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpAddNewProfile.AddNewProfile(ProfileName, DialNumber, APN, UserName, Password, AuthType, new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	try {
						strErrcode = response.getErrorCode();
						if(strErrcode.length() == 0) { 
//							Log.v("AddOrEditProfile", "Yes");
						}else{
							//Log
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }else{
                	//Log
//                	Log.v("AddOrEditProfile", "No");
                }
                startGetProfileList(null);
    			sendBroadcast(response, MessageUti.PROFILE_ADD_NEW_PROFILE_REQUEST);
			}
			
		}));
	}
	
	
	public void startEditProfile(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "EditProfile") != true){
			return;
		}
		
		int profileID = (Integer) data.getParamByKey("profile_id");
		String ProfileName = (String) data.getParamByKey("profile_name");
		String DialNumber = (String) data.getParamByKey("dial_number");
		String APN = (String) data.getParamByKey("apn");
		String UserName = (String) data.getParamByKey("user_name");
		String Password = (String) data.getParamByKey("password");
		int AuthType = (Integer) data.getParamByKey("auth_type");
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpEditProfile.EditProfile(profileID, DialNumber, ProfileName, APN, UserName, Password, AuthType, new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	try {
						strErrcode = response.getErrorCode();
						if(strErrcode.length() == 0) { 
//							Log.v("AddOrEditProfile", "Yes");
						}else{
							//Log
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }else{
                	//Log
//                	Log.v("AddOrEditProfile", "No");
                }
                startGetProfileList(null);

    			sendBroadcast(response, MessageUti.PROFILE_EDIT_PROFILE_REQUEST);
                
			}
			
		}));
	}
	
	
	public void startDeleteProfile(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "DeleteProfile") != true){
			return;
		}
		
		int profileId = (Integer) data.getParamByKey("profile_id");
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpDeleteProfile.DeleteProfile(profileId, new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	try {
						strErrcode = response.getErrorCode();
						if(strErrcode.length() == 0) { 
//							Log.v("GetProfileResultDELETE", "Yes");
						}else{
							//Log
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }else{
                	//Log
//                	Log.v("GetProfileResultDELETE", "No");
                }

    			sendBroadcast(response, MessageUti.PROFILE_DELETE_PROFILE_REQUEST);
                
			}
			
		}));
		
	}
	
	public void startSetDefaultProfile(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "SetDefaultProfile") != true){
			return;
		}
		
		int profileId = (Integer) data.getParamByKey("profile_id");
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpSetDefaultProfile.SetDefaultProfile(profileId, new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	try {
						strErrcode = response.getErrorCode();
						if(strErrcode.length() == 0) { 
//							Log.v("SetDefaultProfileResult", "Yes");
						}else{
							//Log
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }else{
                	//Log
//                	Log.v("SetDefaultProfileResult", "No");
                }

    			sendBroadcast(response, MessageUti.PROFILE_SET_DEFAULT_PROFILE_REQUEST);
			}
			
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
