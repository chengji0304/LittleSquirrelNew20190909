package com.panda.littlesquirrel.entity;

/**
 * 用户投递记录参数/回收员回收记录
 */
public class RecordBean {
    private int canNum;//箱号
    private int category;//垃圾桶分类
    private int count;//垃圾个数
    private double weight;//垃圾重量

    public RecordBean() {
    }

    public RecordBean(int category, int count, double weight,int canNum) {
        this.category = category;
        this.count = count;
        this.weight = weight;
        this.canNum=canNum;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getCanNum() {
        return canNum;
    }

    public void setCanNum(int canNum) {
        this.canNum = canNum;
    }

    @Override
    public String toString() {
        return "RecordBean{" +
                "canNum=" + canNum +
                ", category=" + category +
                ", count=" + count +
                ", weight=" + weight +
                '}';
    }
}
