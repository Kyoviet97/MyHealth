package com.health.myhealth.model;

import java.util.List;

public class UserModel {
    private String userName;
    private String weight;
    private String age;
    private String height;
    private List<DateHealth> listDateHealth;

    public static class DateHealth {
        private String date;

        public DateHealth(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public static class DataHealth{
        private int step;
        private int bike;
        private long sleep;

        public DataHealth(int step, int bike, long sleep) {
            this.step = step;
            this.bike = bike;
            this.sleep = sleep;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public int getBike() {
            return bike;
        }

        public void setBike(int bike) {
            this.bike = bike;
        }

        public long getSleep() {
            return sleep;
        }

        public void setSleep(long sleep) {
            this.sleep = sleep;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public List<DateHealth> getListDateHealth() {
        return listDateHealth;
    }

    public void getListDateHealth(List<DateHealth> setDateHealth) {
        this.listDateHealth = setDateHealth;
    }
}
