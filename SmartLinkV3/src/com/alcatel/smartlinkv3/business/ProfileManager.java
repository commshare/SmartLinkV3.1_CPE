package com.alcatel.smartlinkv3.business;

import java.util.List;

import com.alcatel.smartlinkv3.business.network.HttpSearchNetworkResult.NetworkItem;
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
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ProfileManager extends BaseManager{
	
	private List<ProfileItem> m_profile_list = null;;
	
	public List<ProfileItem> GetProfileListData(){
		return m_profile_list;
	}
	
	public void startGetProfileList(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "GetProfileList") != true){
			return;
		}
		
		HttpRequestManager.GetInstance().sendPostRequest(new HttpGetProfileList.GetProfileList("15.1", new IHttpFinishListener(){

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
                Intent megIntent= new Intent(MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
			}
			
		}));
	}
	
	public void startAddNewProfile(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "AddNewProfile") != true){
			return;
		}
		
		String ProfileName = (String) data.getParamByKey("profile_name");
		String APN = (String) data.getParamByKey("apn");
		String UserName = (String) data.getParamByKey("user_name");
		String Password = (String) data.getParamByKey("password");
		int AuthType = (Integer) data.getParamByKey("auth_type");
		
		HttpRequestManager.GetInstance().sendPostRequest(new HttpAddNewProfile.AddNewProfile("15.2", ProfileName, APN, UserName, Password, AuthType, new IHttpFinishListener(){

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
                Intent megIntent= new Intent(MessageUti.PROFILE_ADD_NEW_PROFILE_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
			}
			
		}));
	}
	
	
	public void startEditProfile(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "EditProfile") != true){
			return;
		}
		
		int profileID = (Integer) data.getParamByKey("profile_id");
		String ProfileName = (String) data.getParamByKey("profile_name");
		String APN = (String) data.getParamByKey("apn");
		String UserName = (String) data.getParamByKey("user_name");
		String Password = (String) data.getParamByKey("password");
		int AuthType = (Integer) data.getParamByKey("auth_type");
		
		HttpRequestManager.GetInstance().sendPostRequest(new HttpEditProfile.EditProfile("15.3", profileID, ProfileName, APN, UserName, Password, AuthType, new IHttpFinishListener(){

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
                
                Intent megIntent= new Intent(MessageUti.PROFILE_EDIT_PROFILE_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
                
			}
			
		}));
	}
	
	
	public void startDeleteProfile(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "DeleteProfile") != true){
			return;
		}
		
		int profileId = (Integer) data.getParamByKey("profile_id");
		
		HttpRequestManager.GetInstance().sendPostRequest(new HttpDeleteProfile.DeleteProfile("15.4", profileId, new IHttpFinishListener(){

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
                
                Intent megIntent= new Intent(MessageUti.PROFILE_DELETE_PROFILE_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
                
			}
			
		}));
		
	}
	
	public void startSetDefaultProfile(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("Profile", "SetDefaultProfile") != true){
			return;
		}
		
		int profileId = (Integer) data.getParamByKey("profile_id");
		
		HttpRequestManager.GetInstance().sendPostRequest(new HttpSetDefaultProfile.SetDefaultProfile("15.5", profileId, new IHttpFinishListener(){

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
                
                Intent megIntent= new Intent(MessageUti.PROFILE_SET_DEFAULT_PROFILE_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
                
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
