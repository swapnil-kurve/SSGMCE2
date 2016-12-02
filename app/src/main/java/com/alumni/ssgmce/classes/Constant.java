package com.alumni.ssgmce.classes;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by USer on 06-10-2016.
 */

public class Constant {

    public static final String STUDENT_HISTORY_URL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getMemberLoginDetails";
    public static final String WALL_POST_URL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getWallPosts";
    public static final String ADD_WALL_POST = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/addWallPost";
    public static final String ADD_WALL_Comment = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/insertWallReply";
    public static final String GET_WALL_Comment = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getWallReplyByID";
    public static final String GET_ALL_UPCOMING_EVENTS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getUpcomingEvents";
    public static final String GET_ALL_ACTIVITIES = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getNewJoinee";
    public static final String GET_ALL_GROUP = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getMyGroup";
    public static final String GET_MY_GROUP = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getMySubscribeGroup";
    public static final String SUBSCRIBE_GROUP_URL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/subscribeGroup";
    public static final String UNSUBSCRIBE_GROUP_URL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteMyGroup";
    public static final String GET_MESSAGES = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getMessages";
    public static final String GET_MESSAGES_JOB_REFERRAL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getJobReferralByMemberId";
    public static final String SEND_MESSAGES = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/SendMessage";
    public static final String GET_SETTINGS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getPrivacySettingByMemberID";
    public static final String SAVE_SETTINGS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/addUpdatePrivacySetting";
    public static final String GET_AVAILABLE_JOBS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getAvailableJobs";
    public static final String GET_POSTED_JOBS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getMemberAddedJobs";
    public static final String DELETE_JOBS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteJob";
    public static final String GET_ALL_BRANCHES = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getAllBranches";
    public static final String GET_ALL_COMPANY = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getOrganizations";
    public static final String DELETE_MESSAGE = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteMessage";
    public static final String ADD_JOB = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/addNewJob";
    public static final String UPDATE_JOB = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/updateJob";

    public static final String GET_ALL_BATCHMATES = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getBatchmates";
    public static final String GET_ALL_JUNIORS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getJuniors";
    public static final String GET_ALL_SENIORS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getSeniors";
    public static final String GET_ALL_FACULTY = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getFaculty";

    public static final String SEARCH_BATCHMATES = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getBatchmatesbysearch";
    public static final String SEARCH_JUNIORS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getJuniorsbysearch";
    public static final String SEARCH_SENIORS = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getSeniorsbysearch";
    public static final String SEARCH_FACULTY = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getFacultybysearch";
    public static final String GET_ALL_COURSES = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getAllCourses";

    public static final String GROUP_POST_URL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getGroupPosts";
    public static final String ADD_GROUP_WALL_POST = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/AddGroupPost";
    public static final String ADD_GROUP_WALL_Comment = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/insertGroupPostReply";
    public static final String GET_GROUP_WALL_Comment = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getGroupReplyByID";

    public static final String CHECK_EMAIL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getMemberDetailsbyEmail";
    public static final String SEND_OTP = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/sendOTPForUpdateRegistration";
    public static final String VERIFY_OTP_FOR_EMAIL = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/verifyOTPwithemail";

    public static final String GET_COUNTRY = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getCountryList";
    public static final String GET_STATE = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getStateList";
    public static final String GET_BRANCHES = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getBranchByCourseID";
    public static final String GET_YEAR = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getYears";
    public static final String MEMBER_REGISTRATION = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/memberRegistration";
    public static final String CHECK_USER = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/CheckUsers";

    public static final String CHECK_EMAIL_MOBILE = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/addOTPForUpdateEmailMobile";
    public static final String UPDATE_MEMBER = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/updateMember";
    public static final String VERIFY_OTP = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/verifyOTPForUpdate";

    public static final String GET_EDUCATION = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getMemberEducation";
    public static final String ADD_EDUCATION = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/addMemberEducation";
    public static final String UPDATE_EDUCATION = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/updateMemberEducation";

    public static final String GET_WORK = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getMemberWorkExperience";
    public static final String ADD_WORK = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/addMemberExperience";
    public static final String UPDATE_WORK = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/updateMemberExperience";
    public static final String UPDATE_GCM_ID = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/updateGCMId";

    public static final String FORGET_PASSWORD = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/ForgetPassword";
    public static final String ADD_EVENT_ATTENDANCE = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/addEventAttendance";
    public static final String GET_EVENT_IF_REGISTER = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/getEventIfRegistered";
    public static final String DELETE_WORK_EXP = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteMemberExperience";
    public static final String DELETE_EDUCATION_EXP = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteMemberEducation";

    public static final String DELETE_WALL_POST = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteWallPost";
    public static final String DELETE_GROUP_POST = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteGrpPost";

    public static final String DELETE_WALL_COMMENT = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteWallReply";
    public static final String DELETE_GROUP_COMMENT = "http://ssgmcealumni.nxglabs.in/alumniWebservice.asmx/deleteGrpPostReply";


    public static final String LOGIN_PREF = "login_pref";
    public static String mIsApproved = "";

    public static String IMG_PATH = "http://ssgmcealumni.nxglabs.in";
    public static int flag = 0;
    public static int PostWallFlag = 0;
    public static int postGroupFlag = 0;

    public static String GOOGLE_PROJ_ID = "992332856438";

    public static void showToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    // To Validate email id
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String getMonth(int pos)
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("January");
        list.add("February");
        list.add("March");
        list.add("April");
        list.add("May");
        list.add("June");
        list.add("July");
        list.add("August");
        list.add("September");
        list.add("October");
        list.add("November");
        list.add("December");

        return list.get(pos - 1);
    }

}
