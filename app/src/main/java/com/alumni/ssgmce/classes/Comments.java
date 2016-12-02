package com.alumni.ssgmce.classes;

/**
 * Created by USer on 07-10-2016.
 */

public class Comments {
    String WallId,WallReplySeqNo,WallPostReply,MemberId,ProfilePhoto,FullName,commentDate,WallPostReplyDay,WallPostReplyMonth,WallPostReplyYear,deleteDisp,isUpdated;

    public Comments(String wallId, String wallReplySeqNo, String wallPostReply, String memberId, String profilePhoto, String fullName, String commentDate, String wallPostReplyDay, String wallPostReplyMonth, String wallPostReplyYear, String deleteDisp, String isUpdated) {
        WallId = wallId;
        WallReplySeqNo = wallReplySeqNo;
        WallPostReply = wallPostReply;
        MemberId = memberId;
        ProfilePhoto = profilePhoto;
        FullName = fullName;
        this.commentDate = commentDate;
        WallPostReplyDay = wallPostReplyDay;
        WallPostReplyMonth = wallPostReplyMonth;
        WallPostReplyYear = wallPostReplyYear;
        this.deleteDisp = deleteDisp;
        this.isUpdated = isUpdated;
    }

    public String getWallId() {
        return WallId;
    }

    public String getWallReplySeqNo() {
        return WallReplySeqNo;
    }

    public String getWallPostReply() {
        return WallPostReply;
    }

    public String getMemberId() {
        return MemberId;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public String getFullName() {
        return FullName;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public String getWallPostReplyDay() {
        return WallPostReplyDay;
    }

    public String getWallPostReplyMonth() {
        return WallPostReplyMonth;
    }

    public String getWallPostReplyYear() {
        return WallPostReplyYear;
    }

    public String getDeleteDisp() {
        return deleteDisp;
    }

    public String getIsUpdated() {
        return isUpdated;
    }
}
