package com.example.luo.model;

/**
 * Created by Administrator on 2016/1/15 0015.
 */
public class TwoInformation extends Information {
    public TwoInformation() {
        super(2);
    }

    private TwoInformation(int infoType) {
        super(infoType);
    }

    @Override
    public Info getInfo() {
        return info1;
    }

    public Info getInfo2() {
        return info2;
    }
}
