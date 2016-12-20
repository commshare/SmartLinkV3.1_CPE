package com.alcatel.smartlinkv3.business.model;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.business.sms.SMSContentItem;
import com.alcatel.smartlinkv3.common.ENUM.EnumSMSType;

import android.os.Parcel;
import android.os.Parcelable;


public class SMSContentItemModel implements Parcelable{
	public int SMSId = 0;
	public EnumSMSType SMSType = EnumSMSType.Read;//0 : read 1 : unread 2 : sent 3 : sent failed 4 : report 5 : flash 6: draft
	public String SMSContent = new String();
	public String SMSTime = new String();//the latest SMS time.format: YYYY-MM-DDhh:mm:ss 
	
	public SMSContentItemModel() {
		SMSId = 0;
		SMSType = EnumSMSType.Read;
		SMSContent = new String();
		SMSTime = new String();
	}
	
	public SMSContentItemModel(Parcel source) {  
		SMSId = source.readInt();  
		SMSType = EnumSMSType.build(source.readInt());
		SMSContent = source.readString();
		SMSTime = source.readString();
	}
	
	public void buildFromResult(SMSContentItem result) {
		SMSId = result.SMSId;
		SMSType = EnumSMSType.build(result.SMSType);
		SMSContent = result.SMSContent;
		SMSTime = result.SMSTime;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(SMSId);
		dest.writeInt(SMSType.antiBuild(SMSType));
		dest.writeString(SMSContent);
		dest.writeString(SMSTime);
	}
	
	public static final Parcelable.Creator<SMSContentItemModel> CREATOR = new Creator<SMSContentItemModel>() {  
		@Override  
		public SMSContentItemModel createFromParcel(Parcel source) {  
			// TODO Auto-generated method stub  
			return new SMSContentItemModel(source);  
		}  
	
		@Override  
		public SMSContentItemModel[] newArray(int size) {  
			// TODO Auto-generated method stub  
			return new SMSContentItemModel[size];  
		}  
	};  
}
