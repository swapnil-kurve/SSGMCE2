package com.alumni.ssgmce.classes;

/**
 * Created by USer on 13-10-2016.
 */

public class GroupPost {
    String GrpPostId,GrpPostSeqNo,GrpId,MemberId,Post,PostAttachment,Date,MonthYear,Member,profilepic,ImagePath,imgDisp,dispVid,audDisp,onDate1,EditDeletePostDisp,isUpdate, commentcount;


    public GroupPost(String grpPostId, String grpPostSeqNo, String grpId, String memberId, String post, String postAttachment, String date, String monthYear, String member, String profilepic, String imagePath, String imgDisp, String dispVid, String audDisp, String onDate1, String editDeletePostDisp, String isUpdate,String commentcount) {
        GrpPostId = grpPostId;
        GrpPostSeqNo = grpPostSeqNo;
        GrpId = grpId;
        MemberId = memberId;
        Post = post;
        PostAttachment = postAttachment;
        Date = date;
        MonthYear = monthYear;
        Member = member;
        this.profilepic = profilepic;
        ImagePath = imagePath;
        this.imgDisp = imgDisp;
        this.dispVid = dispVid;
        this.audDisp = audDisp;
        this.onDate1 = onDate1;
        EditDeletePostDisp = editDeletePostDisp;
        this.isUpdate = isUpdate;
        this.commentcount = commentcount;
    }

    public String getCommentcount() {
        return commentcount;
    }

    public String getGrpPostId() {
        return GrpPostId;
    }

    public String getGrpPostSeqNo() {
        return GrpPostSeqNo;
    }

    public String getGrpId() {
        return GrpId;
    }

    public String getMemberId() {
        return MemberId;
    }

    public String getPost() {
        return Post;
    }

    public String getPostAttachment() {
        return PostAttachment;
    }

    public String getDate() {
        return Date;
    }

    public String getMonthYear() {
        return MonthYear;
    }

    public String getMember() {
        return Member;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public String getImgDisp() {
        return imgDisp;
    }

    public String getDispVid() {
        return dispVid;
    }

    public String getAudDisp() {
        return audDisp;
    }

    public String getOnDate1() {
        return onDate1;
    }

    public String getEditDeletePostDisp() {
        return EditDeletePostDisp;
    }

    public String getIsUpdate() {
        return isUpdate;
    }
}
