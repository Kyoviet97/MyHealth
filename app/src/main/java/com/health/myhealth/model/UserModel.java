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
        private int run;
        private long sleep;
        private long timeBike;
        private Double caloBike;
        private Double kmBike;

        public DataHealth(int step, int run, long sleep, long timeBike, Double caloBike, Double kmBike) {
            this.step = step;
            this.run = run;
            this.sleep = sleep;
            this.timeBike = timeBike;
            this.caloBike = caloBike;
            this.kmBike = kmBike;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public int getRun() {
            return run;
        }

        public void setRun(int run) {
            this.run = run;
        }

        public long getSleep() {
            return sleep;
        }

        public void setSleep(long sleep) {
            this.sleep = sleep;
        }

        public long getTimeBike() {
            return timeBike;
        }

        public void setTimeBike(long timeBike) {
            this.timeBike = timeBike;
        }

        public Double getCaloBike() {
            return caloBike;
        }

        public void setCaloBike(Double caloBike) {
            this.caloBike = caloBike;
        }

        public Double getKmBike() {
            return kmBike;
        }

        public void setKmBike(Double kmBike) {
            this.kmBike = kmBike;
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
