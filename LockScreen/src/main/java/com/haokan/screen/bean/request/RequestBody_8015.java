package com.haokan.screen.bean.request;

/**
 * Created by Maoyujiao on 2017/3/18.
 */

public class RequestBody_8015 {
    private String dId;
    private String cIds;//逗号分隔
    private int op;//1表示收藏 0表示取消
    private int type;// 1单图，2标签，3CP，4分类，5组图

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getcIds() {
        return cIds;
    }

    public void setcIds(String cIds) {
        this.cIds = cIds;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }
}
