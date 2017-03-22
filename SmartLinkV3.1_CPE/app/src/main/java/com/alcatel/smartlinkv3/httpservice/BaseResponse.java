package com.alcatel.smartlinkv3.httpservice;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alcatel.smartlinkv3.common.DataUti;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONObject;

public class BaseResponse implements Parcelable
{
	public final static String ENCODING = "utf-8";
	
	public final static int RESPONSE_OK = 0;
	public final static int RESPONSE_CONNECTION_ERROR = 1;
	public final static int RESPONSE_INVALID = 2;
    public final static int RESPONSE_EMPTY = -1;
	
	protected String m_strErrorCode = "";
	protected String m_strErrorMessage = "";
	protected String m_strId = "";
	protected int m_response_result;
	protected IHttpFinishListener m_finsishCallback = EMPTYLISTENER;
    public final static BaseResponse SUCCESS = new BaseResponse(RESPONSE_OK, "");
    public final static BaseResponse EMPTY = new BaseResponse(RESPONSE_EMPTY, "empty response");

    private final static IHttpFinishListener EMPTYLISTENER = response -> {};

	public BaseResponse(IHttpFinishListener callback)
	{
	    m_response_result = RESPONSE_OK;
	    m_finsishCallback = callback;
	}

    public BaseResponse(int result, String errorCode)
    {
        m_response_result = result;
        m_strErrorCode = errorCode;
    }

    protected BaseResponse(Parcel in) {
        m_strErrorCode = in.readString();
        m_strErrorMessage = in.readString();
        m_strId = in.readString();
        m_response_result = in.readInt();
    }

    public static final Creator<BaseResponse> CREATOR = new Creator<BaseResponse>() {
        @Override
        public BaseResponse createFromParcel(Parcel in) {
            return new BaseResponse(in);
        }

        @Override
        public BaseResponse[] newArray(int size) {
            return new BaseResponse[size];
        }
    };

    @NonNull
    public String getErrorCode() {
		return m_strErrorCode;
	}
	
	public String getErrorMessage() {
		return m_strErrorMessage;
	}
	
	public int getResultCode() 
	{
		return m_response_result;
	}

	public boolean isValid() {
        return getResultCode() == RESPONSE_OK;
    }

    public boolean isNoError() {
        return getErrorCode() == null || getErrorCode().length() == 0;
    }

    public boolean isOk() {
        return isValid() && isNoError();
    }
	public void setResult(int result)
	{
	    m_response_result = result;
	}
	
	protected void parseContent(String strJsonResult) {}
	
	
	public void parseResult(Context context, JSONObject jsonResult)
	{
	    if(jsonResult == null)
		{
			m_response_result = RESPONSE_INVALID;
			Log.d("SmartLinkServerResponse", "%s: Empty response!" + this.getClass().getName());
			return;
		}

		try
		{
			 m_response_result = RESPONSE_OK;
			 JSONObject resultObj = jsonResult.optJSONObject(ConstValue.JSON_RESULT);
			 if(resultObj == null) {
				 JSONObject errorObj = jsonResult.optJSONObject(ConstValue.JSON_ERROR);
				 if(errorObj != null) {
					 m_strErrorCode = DataUti.parseString(errorObj.optString(ConstValue.JSON_ERROR_CODE));
					 m_strErrorMessage = DataUti.parseString(errorObj.optString(ConstValue.JSON_ERROR_MESSAGE));
				 }else{
					 m_response_result = RESPONSE_INVALID;
				 }
			 }else{
				 parseContent(resultObj.toString());
			 }

			 if(m_strErrorCode.equalsIgnoreCase(ErrorCode.ERR_COMMON_ERROR_32604))
			 {
				 Intent megIntent= new Intent(MessageUti.USER_COMMON_ERROR_32604_REQUEST);
				 megIntent.putExtra(MessageUti.HTTP_RESPONSE, this);
				 context.sendBroadcast(megIntent);
			 }

			 m_strId = DataUti.parseString(jsonResult.optString(ConstValue.JSON_ID));
		 }catch(Exception e){
			 m_response_result = RESPONSE_INVALID;
			 e.printStackTrace();
		 }
	}
	
	public void invokeFinishCallback()
	{
		m_finsishCallback.onHttpRequestFinish(this);
	}
	
	public <T> T getModelResult() {
        return null;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_strErrorCode);
        dest.writeString(m_strErrorMessage);
        dest.writeString(m_strId);
        dest.writeInt(m_response_result);
    }
}