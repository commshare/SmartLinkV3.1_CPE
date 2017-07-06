package com.alcatel.wifilink.model.sms;

import java.util.List;

/**
 * Created by qianli.ma on 2017/6/27.
 */

public class SMSContactList {

    public List<SMSContact> SMSContactList;
    public int Page;
    public int TotalPageCount;

    public int getPage() {
        return Page;
    }

    public void setPage(int page) {
        Page = page;
    }

    public int getTotalPageCount() {
        return TotalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        TotalPageCount = totalPageCount;
    }

    public List<SMSContact> getSMSContactList() {
        return SMSContactList;
    }

    public void setSMSContactList(List<SMSContact> SMSContactList) {
        this.SMSContactList = SMSContactList;
    }

    public class SMSContact {

        public int ContactId;
        public List<String> PhoneNumber;
        public long SMSId;
        public int SMSType;
        public int ReportStatus;
        public String SMSContent;
        public String SMSTime;
        public int UnreadCount;
        public int TSMSCount;

        public int getContactId() {
            return ContactId;
        }

        public void setContactId(int contactId) {
            ContactId = contactId;
        }

        public List<String> getPhoneNumber() {
            return PhoneNumber;
        }

        public void setPhoneNumber(List<String> phoneNumber) {
            PhoneNumber = phoneNumber;
        }

        public long getSMSId() {
            return SMSId;
        }

        public void setSMSId(long SMSId) {
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

        public void setReportStatus(int reportStatus) {
            ReportStatus = reportStatus;
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

        public int getUnreadCount() {
            return UnreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            UnreadCount = unreadCount;
        }

        public int getTSMSCount() {
            return TSMSCount;
        }

        public void setTSMSCount(int TSMSCount) {
            this.TSMSCount = TSMSCount;
        }
    }
}
