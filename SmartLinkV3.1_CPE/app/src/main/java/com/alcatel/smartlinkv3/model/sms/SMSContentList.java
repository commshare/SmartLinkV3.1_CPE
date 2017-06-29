package com.alcatel.smartlinkv3.model.sms;

import java.util.List;

/**
 * Created by qianli.ma on 2017/6/28.
 */

public class SMSContentList {

    private int Page;
    private int ContactId;
    private int TotalPageCount;

    private List<SMSContentBean> SMSContentList;
    private List<String> PhoneNumber;

    public int getPage() {
        return Page;
    }

    public void setPage(int Page) {
        this.Page = Page;
    }

    public int getContactId() {
        return ContactId;
    }

    public void setContactId(int ContactId) {
        this.ContactId = ContactId;
    }

    public int getTotalPageCount() {
        return TotalPageCount;
    }

    public void setTotalPageCount(int TotalPageCount) {
        this.TotalPageCount = TotalPageCount;
    }

    public List<SMSContentBean> getSMSContentList() {
        return SMSContentList;
    }

    public void setSMSContentList(List<SMSContentBean> SMSContentList) {
        this.SMSContentList = SMSContentList;
    }

    public List<String> getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(List<String> PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public static class SMSContentBean {
        private int SMSId;
        private int SMSType;
        private int ReportStatus;
        private String SMSContent;
        private String SMSTime;

        public int getSMSId() {
            return SMSId;
        }

        public void setSMSId(int SMSId) {
            this.SMSId = SMSId;
        }

        public int getSMSType() {
            return SMSType;
        }

        public void setSMSType(int SMSType) {
            this.SMSType = SMSType;
        }

        public int getReportStatus() {
            return ReportStatus;
        }

        public void setReportStatus(int ReportStatus) {
            this.ReportStatus = ReportStatus;
        }

        public String getSMSContent() {
            return SMSContent;
        }

        public void setSMSContent(String SMSContent) {
            this.SMSContent = SMSContent;
        }

        public String getSMSTime() {
            return SMSTime;
        }

        public void setSMSTime(String SMSTime) {
            this.SMSTime = SMSTime;
        }
    }
}
