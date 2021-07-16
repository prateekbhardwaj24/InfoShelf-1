package com.card.infoshelf.bottomfragment;

import java.util.ArrayList;

public class GridModel {

    private String CompanyTo,past_clg_name,job_role,job_ex,FileName,qualification,FileType,JobProfile,PostURL,RelatedTo,TextBoxData,UserId,ctcType,ctcValue,standard,Current_company,timeStamp,userName,userEmail,profession,course,college_name,user_bio,school_name;
    private ArrayList<String> interestArrayData;

    public GridModel() {
    }

    public GridModel(String companyTo, String past_clg_name, String job_role, String job_ex, String fileName, String qualification, String fileType, String jobProfile, String postURL, String relatedTo, String textBoxData, String userId, String ctcType, String ctcValue, String standard, String current_company, String timeStamp, String userName, String userEmail, String profession, String course, String college_name, String user_bio, String school_name, ArrayList<String> interestArrayData) {
        CompanyTo = companyTo;
        this.past_clg_name = past_clg_name;
        this.job_role = job_role;
        this.job_ex = job_ex;
        FileName = fileName;
        this.qualification = qualification;
        FileType = fileType;
        JobProfile = jobProfile;
        PostURL = postURL;
        RelatedTo = relatedTo;
        TextBoxData = textBoxData;
        UserId = userId;
        this.ctcType = ctcType;
        this.ctcValue = ctcValue;
        this.standard = standard;
        Current_company = current_company;
        this.timeStamp = timeStamp;
        this.userName = userName;
        this.userEmail = userEmail;
        this.profession = profession;
        this.course = course;
        this.college_name = college_name;
        this.user_bio = user_bio;
        this.school_name = school_name;
        this.interestArrayData = interestArrayData;
    }

    public String getCompanyTo() {
        return CompanyTo;
    }

    public void setCompanyTo(String companyTo) {
        CompanyTo = companyTo;
    }

    public String getPast_clg_name() {
        return past_clg_name;
    }

    public void setPast_clg_name(String past_clg_name) {
        this.past_clg_name = past_clg_name;
    }

    public String getJob_role() {
        return job_role;
    }

    public void setJob_role(String job_role) {
        this.job_role = job_role;
    }

    public String getJob_ex() {
        return job_ex;
    }

    public void setJob_ex(String job_ex) {
        this.job_ex = job_ex;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public String getJobProfile() {
        return JobProfile;
    }

    public void setJobProfile(String jobProfile) {
        JobProfile = jobProfile;
    }

    public String getPostURL() {
        return PostURL;
    }

    public void setPostURL(String postURL) {
        PostURL = postURL;
    }

    public String getRelatedTo() {
        return RelatedTo;
    }

    public void setRelatedTo(String relatedTo) {
        RelatedTo = relatedTo;
    }

    public String getTextBoxData() {
        return TextBoxData;
    }

    public void setTextBoxData(String textBoxData) {
        TextBoxData = textBoxData;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getCtcType() {
        return ctcType;
    }

    public void setCtcType(String ctcType) {
        this.ctcType = ctcType;
    }

    public String getCtcValue() {
        return ctcValue;
    }

    public void setCtcValue(String ctcValue) {
        this.ctcValue = ctcValue;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getCurrent_company() {
        return Current_company;
    }

    public void setCurrent_company(String current_company) {
        Current_company = current_company;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }

    public String getUser_bio() {
        return user_bio;
    }

    public void setUser_bio(String user_bio) {
        this.user_bio = user_bio;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public ArrayList<String> getInterestArrayData() {
        return interestArrayData;
    }

    public void setInterestArrayData(ArrayList<String> interestArrayData) {
        this.interestArrayData = interestArrayData;
    }
}
