package id.cnn.gomudik.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DifferenceTime {
    private String ResultDefault;
    private String time;

    public DifferenceTime(String time){
        this.time = time;
    }

    public void executeDateTimeDifference(){
        long diff;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            Date dateNow = Calendar.getInstance().getTime();
            String dateNowString = dateFormat.format(dateNow);
            Date now = dateFormat.parse(dateNowString);
            Date timeVideo = dateFormat.parse(time);
            diff = now.getTime() - timeVideo.getTime();
            count(diff);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void count(long diff){
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (60 * 60 * 1000 * 24);
        long diffWeeks = diff / (60 * 60 * 1000 * 24 * 7);
        long diffMonths = (long) (diff / (60 * 60 * 1000 * 24 * 30.41666666));
        long diffYears = diff / ((long)60 * 60 * 1000 * 24 * 365);
        if (diffSeconds < 1) {
            this.ResultDefault = "now";
        } else if (diffMinutes < 1) {
            if(diffSeconds > 1){
                this.ResultDefault = diffSeconds + " seconds ago";
            } else {
                this.ResultDefault = diffSeconds + " second ago";
            }
        } else if (diffHours < 1) {
            if(diffMinutes > 1){
                this.ResultDefault = diffMinutes + " minutes ago";
            } else {
                this.ResultDefault = diffMinutes + " minute ago";
            }
        } else if (diffDays < 1) {
            if(diffHours > 1){
                this.ResultDefault = diffHours + " hours ago";
            } else {
                this.ResultDefault = diffHours + " hour ago";
            }
        } else if (diffWeeks < 1) {
            if(diffDays > 1){
                this.ResultDefault = diffDays + " days ago";
            } else {
                this.ResultDefault = diffDays + " day ago";
            }
        } else if (diffMonths < 1) {
            if(diffWeeks > 1){
                this.ResultDefault = diffWeeks + " weeks ago";
            } else {
                this.ResultDefault = diffWeeks + " week ago";
            }
        } else if (diffYears < 1) {
            if(diffMonths > 1){
                this.ResultDefault = diffMonths+" months ago";
            } else {
                this.ResultDefault = diffMonths+" month ago";
            }

        } else {
            if(diffYears > 1){
                this.ResultDefault = diffYears+" years Ago";
            } else {
                this.ResultDefault = diffYears+" year Ago";
            }
        }
    }

    public void executeTimeRemaining(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm",Locale.ENGLISH);
        Date currentTime = Calendar.getInstance().getTime();
        String now = dateFormat.format(currentTime);
        long diff;
        try {
            Date timeNow = dateFormat.parse(now);
            Date timeShalat = dateFormat.parse(time);
            diff = timeShalat.getTime() - timeNow.getTime();
            long diffSeconds = diff / 1000;
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            if (diffSeconds < 1){
                this.ResultDefault = "Waktu Salat Telah Tiba";
            } else if (diffMinutes < 1){
                this.ResultDefault =  "Waktu Salat Segera Tiba";
            } else if (diffHours < 1){
                this.ResultDefault =  diffMinutes%60 + " Menit Lagi";
            } else{
                this.ResultDefault =  diffHours+ " Jam "+diffMinutes%60+ " Menit Lagi";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            this.ResultDefault =  "";
        }
    }

    public void executeTimeChat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        Date currentTime = Calendar.getInstance().getTime();
        String now = dateFormat.format(currentTime);
        long diff;
        try {
            Date timeNow = dateFormat.parse(now);
            Date timeChat = dateFormat.parse(time);
            diff = timeNow.getTime() - timeChat.getTime();
            long diffDays = diff / (60 * 60 * 1000 * 24);
            if (diffDays < 1){
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm",Locale.ENGLISH);
                this.ResultDefault = timeFormat.format(timeChat);
            } else{
                SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yy",Locale.ENGLISH);
                this.ResultDefault = timeFormat.format(timeChat);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            this.ResultDefault =  "";
        }
    }

    public String getResultDefault() {
        return ResultDefault;
    }

}
