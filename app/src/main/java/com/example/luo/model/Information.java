package com.example.luo.model;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public abstract class Information {
    public Info info1;
    public Info info2;

    public int infoType;

    public Information(int infoType) {
        this.infoType = infoType;
    }

    public int getInfoType() {
        return infoType;
    }

    public abstract Info getInfo();
}
