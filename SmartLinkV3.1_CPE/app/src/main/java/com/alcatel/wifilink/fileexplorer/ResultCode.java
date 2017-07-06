package com.alcatel.wifilink.fileexplorer;

class ResultCode {
    
    public interface OnResultListener {
        void onResult(ResultCode resultCode);
    }
    
    private static final boolean RESULT_SUCCEED = true;
    private static final boolean RESULT_FAIL = false;
    
    private static final int RESULT_CODE_SUCCEED = 0x00000000;
    private static final int RESULT_CODE_FAIL = 0x00010000;
    private static final int RESULT_CODE_MRSK = 0x00010000;
    private static final int CAUSE_CODE_MRSK = 0x0000FFFF;
    
    private static int getResultCodeValue(boolean result, int cause) {
        int value = CAUSE_CODE_MRSK & cause;
        return result ? (RESULT_CODE_SUCCEED | value) : (RESULT_CODE_FAIL | value);
    }
    private static boolean getResultFormValue(int value) {
        return ((value & RESULT_CODE_MRSK) != RESULT_CODE_FAIL);
    }
    private static int getCauseFormValue(int value) {
        return CAUSE_CODE_MRSK & value;
    }
    
    public static final int SUCCEED_CREATE_FOLDER = getResultCodeValue(RESULT_SUCCEED,0);
    public static final int FAIL_FOLDER_EXISTS = getResultCodeValue(RESULT_FAIL,0);
    public static final int FAIL_FOLDER_CREATE = getResultCodeValue(RESULT_FAIL,1);
    
    private final String fail;
    private final String succeed;
    private final String failCause[];
    private final String succeedDesc[];
    private final OnResultListener mListener;
    
    final boolean result;
    final int cause;
    
    public String getStringByResult() {
        return isSucceed() ? succeed : fail;
    }
    public String getStringByCause() {
        return isSucceed() ? succeedDesc[this.cause] : failCause[this.cause];
    }
    
    private ResultCode(boolean result, int cause,
            String fail, String succeed,
            String[] failCause, String[] succeedDesc,
            OnResultListener listener) {
        this.result = result;
        this.cause = cause;
        
        this.fail = fail;
        this.succeed = succeed;
        this.failCause = failCause;
        this.succeedDesc = succeedDesc;
        this.mListener = listener;
    }
    
    public static ResultCode create(int resultCode,
            String fail, String succeed,
            String[] failCause, String[] succeedDesc,
            OnResultListener listener) {
        boolean result = getResultFormValue(resultCode);
        int cause = getCauseFormValue(resultCode);
        return new ResultCode(result, cause,
                fail,succeed,failCause,succeedDesc,listener);
    }
    
    public boolean isSucceed() {
        return this.result;
    }
    
    public boolean doResult() {
        if (mListener != null) {
            mListener.onResult(this);
        }
        
        return isSucceed();
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    
    public static class Builder {
        private String fail;
        private String succeed;
        private String failCause[];
        private String succeedDesc[];
        private OnResultListener mListener;
        
        public Builder(String fail, String succeed,
                String[] failCause, String[] succeedDesc,
                OnResultListener listener) {
            this.fail = fail;
            this.succeed = succeed;
            this.failCause = failCause;
            this.succeedDesc = succeedDesc;
            this.mListener = listener;
        }
        
        public Builder() {}

        public Builder setFail(String fail) {
            this.fail = fail;
            return this;
        }

        public Builder setSucceed(String succeed) {
            this.succeed = succeed;
            return this;
        }

        public Builder setFailCause(String[] failCause) {
            this.failCause = failCause;
            return this;
        }

        public Builder setSucceedDesc(String[] succeedDesc) {
            this.succeedDesc = succeedDesc;
            return this;
        }

        public Builder setmListener(OnResultListener mListener) {
            this.mListener = mListener;
            return this;
        }

        public ResultCode builder(int resultCode) {
            return create(resultCode,
                    this.fail,this.succeed,
                    this.failCause,this.succeedDesc,
                    this.mListener);
        }
    }
}
