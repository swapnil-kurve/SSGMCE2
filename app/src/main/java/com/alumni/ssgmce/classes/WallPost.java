package com.alumni.ssgmce.classes;

/**
 * Created by USer on 06-10-2016.
 */

public class WallPost {

    String RowNumber,MemberId,MemberType,FullName,SignUpMembers,Designation,Department,ProfilePhoto,InsertDate,Date,MonthYear,WallId,WallSeqNo,WallPhotoVideo,
            WallImageName,RecordType,imgDisp,dispVid,audDisp,EditDeletePostDisp,isUpdate,commentcount;

    public WallPost(String rowNumber, String memberId, String memberType, String fullName, String signUpMembers, String designation, String department, String profilePhoto, String insertDate, String date, String monthYear, String wallId, String wallSeqNo, String wallPhotoVideo, String wallImageName, String recordType, String imgDisp, String dispVid, String audDisp, String editDeletePostDisp, String isUpdate, String commentcount) {
        RowNumber = rowNumber;
        MemberId = memberId;
        MemberType = memberType;
        FullName = fullName;
        SignUpMembers = signUpMembers;
        Designation = designation;
        Department = department;
        ProfilePhoto = profilePhoto;
        InsertDate = insertDate;
        Date = date;
        MonthYear = monthYear;
        WallId = wallId;
        WallSeqNo = wallSeqNo;
        WallPhotoVideo = wallPhotoVideo;
        WallImageName = wallImageName;
        RecordType = recordType;
        this.imgDisp = imgDisp;
        this.dispVid = dispVid;
        this.audDisp = audDisp;
        EditDeletePostDisp = editDeletePostDisp;
        this.isUpdate = isUpdate;
        this.commentcount = commentcount;
    }

    public String getCommentcount() {
        return commentcount;
    }

    public String getRowNumber() {
        return RowNumber;
    }

    public String getMemberId() {
        return MemberId;
    }

    public String getMemberType() {
        return MemberType;
    }

    public String getFullName() {
        return FullName;
    }

    public String getSignUpMembers() {
        return SignUpMembers;
    }

    public String getDesignation() {
        return Designation;
    }

    public String getDepartment() {
        return Department;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public String getInsertDate() {
        return InsertDate;
    }

    public String getDate() {
        return Date;
    }

    public String getMonthYear() {
        return MonthYear;
    }

    public String getWallId() {
        return WallId;
    }

    public String getWallSeqNo() {
        return WallSeqNo;
    }

    public String getWallPhotoVideo() {
        return WallPhotoVideo;
    }

    public String getWallImageName() {
        return WallImageName;
    }

    public String getRecordType() {
        return RecordType;
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

    public String getEditDeletePostDisp() {
        return EditDeletePostDisp;
    }

    public String getIsUpdate() {
        return isUpdate;
    }
}
