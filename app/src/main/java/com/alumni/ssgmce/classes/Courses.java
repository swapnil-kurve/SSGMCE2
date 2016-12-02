package com.alumni.ssgmce.classes;

/**
 * Created by USer on 16-10-2016.
 */

public class Courses {

    String CourseId,CourseName;

    public Courses(String courseId, String courseName) {
        CourseId = courseId;
        CourseName = courseName;
    }

    public String getCourseId() {
        return CourseId;
    }

    public String getCourseName() {
        return CourseName;
    }
}
