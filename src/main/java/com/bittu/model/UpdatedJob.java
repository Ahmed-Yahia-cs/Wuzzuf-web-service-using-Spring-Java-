package com.bittu.model;

public class UpdatedJob {

    private String Title;
    private String Company;
    private String Location;
    private String Type;
    private String Level;
    private String YearsExp;
    private String Country;
    private String Skills;
    private int minYearsExp;

    public UpdatedJob(String Title, String Company, String Location,
                      String Type, String Level, String	YearsExp, String Country, String	Skills, int minYearsExp)
    {
        this.Title = Title;
        this.Company = Company;
        this.Location = Location;
        this.Type = Type;
        this.Level = Level;
        this.YearsExp = YearsExp;
        this.Country = Country;
        this.Skills = Skills;
        this.minYearsExp = minYearsExp;

    }
    public UpdatedJob()
    {
        this.Title = "";
        this.Company = "";
        this.Location = "";
        this.Type = "";
        this.Level = "";
        this.YearsExp = "";
        this.Country = "";
        this.Skills = "";
        this.minYearsExp = -1;

    }

    public String getTitle() {
        return Title;
    }

    public String getCompany() {
        return Company;
    }

    public String getLocation() {
        return Location;
    }

    public String getType() {
        return Type;
    }

    public String getLevel() {
        return Level;
    }

    public String getYearsExp() {
        return YearsExp;
    }

    public String getCountry() {
        return Country;
    }

    public String getSkills() {
        return Skills;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setCompany(String Company) {
        this.Company = Company;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public void setLevel(String Level) {
        this.Level = Level;
    }

    public void setYearsExp(String YearsExp) {
        this.YearsExp = YearsExp;
    }

    public void setCountry(String Country) {
        this.Country = Country;
    }

    public void setSkills(String Skills) {
        this.Skills = Skills;
    }


    public int getMinYearsExp() {
        return minYearsExp;
    }

    public void setMinYearsExp(int minYearsExp) {
        this.minYearsExp = minYearsExp;
    }


}
