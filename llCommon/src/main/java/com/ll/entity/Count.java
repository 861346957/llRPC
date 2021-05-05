package com.ll.entity;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 10:07
 */
public class Count {
    private int number;

    public Count() {
        this.number = -1;
    }
    public int get(){
        return this.number;
    }
    public void increasing(){
        this.number++;
    }

    public boolean equals(Integer num){
        return num==null ? false : num.intValue()==this.number;
    }
    public void residualCalc(Integer num){
        this.number = num == null ? -1 : this.number % num;
    }
    public void set(int number) {
        this.number = number;
    }
}
