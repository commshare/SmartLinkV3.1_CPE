package com.alcatel.smartlinkv3.business.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.business.sms.SmsContentListResult;

public class SmsContentMessagesModel extends BaseResult implements Parcelable {
	public ArrayList<SMSContentItemModel> SMSContentList = new ArrayList<SMSContentItemModel>();
	public int Page = 1;
	public int ContactId = 0;
	private ArrayList<String> PhoneNumber = new ArrayList<String>();
	public int TotalPageCount = 1;
	
	public SmsContentMessagesModel() {
		SMSContentList = new ArrayList<SMSContentItemModel>();
		Page = 1;
		ContactId = 0;
		PhoneNumber = new ArrayList<String>();
		TotalPageCount = 1;
	}
	
	public SmsContentMessagesModel(Parcel source) {
		SMSContentList = new ArrayList<SMSContentItemModel>();  
		source.readTypedList(SMSContentList, SMSContentItemModel.CREATOR); 
		Page = source.readInt();
		ContactId = source.readInt();
		PhoneNumber = new ArrayList<String>();
		source.readStringList(PhoneNumber);
		TotalPageCount = source.readInt();
	}
	
	@Override
	public void clear(){
		SMSContentList.clear();
		Page = 1;
		ContactId = 0;
		PhoneNumber.clear();
		TotalPageCount = 1;
	}
	
	public void buildFromResult(SmsContentListResult result) {
		SMSContentList.clear();
		for(int i = 0;i < result.SMSContentList.size();i++) {
			SMSContentItemModel model = new SMSContentItemModel();
			model.buildFromResult(result.SMSContentList.get(i));
			SMSContentList.add(model);
		}
		Page = result.Page;
		ContactId = result.ContactId;
		PhoneNumber = (ArrayList<String>) result.PhoneNumber.clone();
		TotalPageCount = result.TotalPageCount;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeTypedList(SMSContentList);  
		dest.writeInt(Page);
		dest.writeInt(ContactId);
		dest.writeStringList(PhoneNumber);
		dest.writeInt(TotalPageCount);
	}
	
	public static final Parcelable.Creator<SmsContentMessagesModel> CREATOR = new Creator<SmsContentMessagesModel>() {  
		@Override  
		public SmsContentMessagesModel createFromParcel(Parcel source) {  
			// TODO Auto-generated method stub  
			return new SmsContentMessagesModel(source);  
		}  
	
		@Override  
		public SmsContentMessagesModel[] newArray(int size) {  
			// TODO Auto-generated method stub  
			return new SmsContentMessagesModel[size];  
		}  
	};  
}
