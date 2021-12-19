package com.bittu.model;

public class ValueCount {

    private String name;
    private long count;

    public ValueCount(String Company, long count)
    {
        this.setName(Company);
        this.setCount(count);
    }
    public ValueCount()
    {
        this.setName("noName");
        this.setCount(0);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
