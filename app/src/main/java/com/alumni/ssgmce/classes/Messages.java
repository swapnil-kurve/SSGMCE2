package com.alumni.ssgmce.classes;

/**
 * Created by USer on 12-10-2016.
 */

public class Messages {

    String PrivateMsgId,PvtMsgSeqNo,FromMemberId,ToMemberId,PvtMessage,PvtMsgDate,Attachment,Attachment1,ReplyToPvtMsgId,PvtMsgDay,ReadMsg,displayFile,FullName,ProfilePhoto,MonthYear,MsgType,JobId,MemberName,EmailId;

    public Messages(String privateMsgId, String pvtMsgSeqNo, String fromMemberId, String toMemberId, String pvtMessage, String pvtMsgDate, String attachment, String attachment1, String replyToPvtMsgId, String pvtMsgDay, String readMsg, String displayFile, String fullName, String profilePhoto, String monthYear, String msgType, String jobId, String memberName, String emailId) {
        PrivateMsgId = privateMsgId;
        PvtMsgSeqNo = pvtMsgSeqNo;
        FromMemberId = fromMemberId;
        ToMemberId = toMemberId;
        PvtMessage = pvtMessage;
        PvtMsgDate = pvtMsgDate;
        Attachment = attachment;
        Attachment1 = attachment1;
        ReplyToPvtMsgId = replyToPvtMsgId;
        PvtMsgDay = pvtMsgDay;
        ReadMsg = readMsg;
        this.displayFile = displayFile;
        FullName = fullName;
        ProfilePhoto = profilePhoto;
        MonthYear = monthYear;
        MsgType = msgType;
        JobId = jobId;
        MemberName = memberName;
        EmailId = emailId;
    }

    public String getPrivateMsgId() {
        return PrivateMsgId;
    }

    public String getPvtMsgSeqNo() {
        return PvtMsgSeqNo;
    }

    public String getFromMemberId() {
        return FromMemberId;
    }

    public String getToMemberId() {
        return ToMemberId;
    }

    public String getPvtMessage() {
        return PvtMessage;
    }

    public String getPvtMsgDate() {
        return PvtMsgDate;
    }

    public String getAttachment() {
        return Attachment;
    }

    public String getAttachment1() {
        return Attachment1;
    }

    public String getReplyToPvtMsgId() {
        return ReplyToPvtMsgId;
    }

    public String getPvtMsgDay() {
        return PvtMsgDay;
    }

    public String getReadMsg() {
        return ReadMsg;
    }

    public String getDisplayFile() {
        return displayFile;
    }

    public String getFullName() {
        return FullName;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public String getMonthYear() {
        return MonthYear;
    }

    public String getMsgType() {
        return MsgType;
    }

    public String getJobId() {
        return JobId;
    }

    public String getMemberName() {
        return MemberName;
    }

    public String getEmailId() {
        return EmailId;
    }
}
