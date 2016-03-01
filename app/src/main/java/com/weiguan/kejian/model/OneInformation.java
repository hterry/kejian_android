package com.weiguan.kejian.model;

/**
 * Created by Administrator on 2016/1/15 0015.
 */
public class OneInformation extends Information {
    public OneInformation() {
        super(1);
    }

    private OneInformation(int infoType) {
        super(infoType);
    }

    @Override
    public Info getInfo() {
        return info1;
    }
}
