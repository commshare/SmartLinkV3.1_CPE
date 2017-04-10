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
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONObject;

public class BaseResponse implements Parcelable {
    private final static String TAG = "BaseResponse";
    public final static String ENCODING = "utf-8";

    public final static int RESPONSE_OK = 0;
    public final static int RESPONSE_CONNECTION_ERROR = 1;
    public final static int RESPONSE_MALFORMED = 2;
    public final static int RESPONSE_INVALID = 3;
    public final static int RESPONSE_STATUS_ERROR = 4;
    public final static int RESPONSE_UNSUPPORTED_API = 5;
    public final static int RESPONSE_EMPTY = -1;
    public final static BaseResponse SUCCESS = new BaseResponse(RESPONSE_OK, "");
    public final static BaseResponse EMPTY = new BaseResponse(RESPONSE_EMPTY, "empty response");

    private Intent m_intent;
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
    private final static IHttpFinishListener EMPTY_LISTENER = response -> {
    };
    protected String m_strErrorCode = "";
    protected String m_strErrorMessage = "";
    protected String m_strId = "";
    protected int m_response_result;
    protected IHttpFinishListener m_finsishCallback = EMPTY_LISTENER;
    protected String broadcastAction = null;
    private boolean m_broadcast = true;

    public BaseResponse(String action, IHttpFinishListener callback) {
        this(callback);
        broadcastAction = action;
    }

    public BaseResponse(IHttpFinishListener callback) {
        m_response_result = RESPONSE_OK;
        if (callback != null)
            m_finsishCallback = callback;
    }

    public BaseResponse(int result, String errorCode) {
        m_response_result = result;
        m_strErrorCode = errorCode;
    }

    protected BaseResponse(Parcel in) {
        m_strErrorCode = in.readString();
        m_strErrorMessage = in.readString();
        m_strId = in.readString();
        m_response_result = in.readInt();
    }

    @NonNull
    public String getErrorCode() {
        return m_strErrorCode;
    }

    public void setErrorCode(String errorCode) {
        if (errorCode != null)
            m_strErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return m_strErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        m_strErrorMessage = errorMessage;
    }

    public int getResultCode() {
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

    public void setResult(int result) {
        m_response_result = result;
    }

    protected void parseContent(String strJsonResult) {
    }


    public void parseResult(Context context, JSONObject jsonResult) {
        if (jsonResult == null) {
            m_response_result = RESPONSE_INVALID;
            Log.d("SmartLinkServerResponse", "%s: Empty response!" + this.getClass().getName());
            return;
        }

        m_response_result = RESPONSE_OK;
        JSONObject resultObj = jsonResult.optJSONObject(ConstValue.JSON_RESULT);
        if (resultObj == null) {
            JSONObject errorObj = jsonResult.optJSONObject(ConstValue.JSON_ERROR);
            if (errorObj != null) {
                m_strErrorCode = DataUti.parseString(errorObj.optString(ConstValue.JSON_ERROR_CODE));
                m_strErrorMessage = DataUti.parseString(errorObj.optString(ConstValue.JSON_ERROR_MESSAGE));
            } else {
                m_response_result = RESPONSE_INVALID;
            }
        } else {
            parseContent(resultObj.toString());
        }

        if (m_strErrorCode.equalsIgnoreCase(ErrorCode.ERR_COMMON_ERROR_32604)) {
            Intent megIntent = new Intent(MessageUti.USER_COMMON_ERROR_32604_REQUEST);
            megIntent.putExtra(MessageUti.HTTP_RESPONSE, this);
            context.sendBroadcast(megIntent);
        }

        m_strId = DataUti.parseString(jsonResult.optString(ConstValue.JSON_ID));
    }

    public void invokeFinishCallback() {
        if (m_finsishCallback != null)
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

    public void setIntent(Intent intent) {
        if (intent == null) {
            Log.e(TAG, "null intent parameter");
            return;
        }
        m_intent = intent;
    }

    public Intent getIntent(String action) {
        if (m_intent == null) {
            if (action != null && !action.isEmpty()) {
                m_intent = new Intent(action);
            } else if (broadcastAction == null || broadcastAction.isEmpty()) {
                Log.e(TAG, "invalid broadcast action");
                m_intent = new Intent();
            } else {
                m_intent = new Intent(broadcastAction);
            }
        }
        return m_intent;
    }

    public boolean isBroadcast() {
        return m_broadcast;
    }

    public void setBroadcast(boolean sendBroadcast) {
        m_broadcast = sendBroadcast;
    }

    public void sendBroadcast(Context context) {
        if (isBroadcast()) {
            Intent intent = getIntent(null);
            intent.putExtra(MessageUti.HTTP_RESPONSE, this);
            context.sendBroadcast(intent);
        }
    }
}