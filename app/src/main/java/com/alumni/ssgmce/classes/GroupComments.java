package com.alumni.ssgmce.classes;

/**
 * Created by USer on 07-10-2016.
 */

public class GroupComments {
    String GrpPostId,GrpPostSRplyeqNo,GrpId,ReplyMemberId,GrpPostReplyDate,GroupPostReply,ReplyMemberName,ReplyProfilePhoto,deleteDisp,isUpdated;

    public GroupComments(String grpPostId, String grpPostSRplyeqNo, String grpId, String replyMemberId, String grpPostReplyDate, String groupPostReply, String replyMemberName, String replyProfilePhoto, String deleteDisp, String isUpdated) {
        GrpPostId = grpPostId;
        GrpPostSRplyeqNo = grpPostSRplyeqNo;
        GrpId = grpId;
        ReplyMemberId = replyMemberId;
        GrpPostReplyDate = grpPostReplyDate;
        GroupPostReply = groupPostReply;
        ReplyMemberName = replyMemberName;
        ReplyProfilePhoto = replyProfilePhoto;
        this.deleteDisp = deleteDisp;
        this.isUpdated = isUpdated;
    }


    public String getGrpPostId() {
        return GrpPostId;
    }

    public String getGrpPostSRplyeqNo() {
        return GrpPostSRplyeqNo;
    }

    public String getGrpId() {
        return GrpId;
    }

    public String getReplyMemberId() {
        return ReplyMemberId;
    }

    public String getGrpPostReplyDate() {
        return GrpPostReplyDate;
    }

    public String getGroupPostReply() {
        return GroupPostReply;
    }

    public String getReplyMemberName() {
        return ReplyMemberName;
    }

    public String getReplyProfilePhoto() {
        return ReplyProfilePhoto;
    }

    public String getDeleteDisp() {
        return deleteDisp;
    }

    public String getIsUpdated() {
        return isUpdated;
    }
}
