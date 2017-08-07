package com.haokan.baiduh5.bean;

/**
 * 检查升级，请求回来的信息封装成的bean
 */
public class InitResponseWrapperBean {
    public UpdateBean ver;

    public UpdateBean getVer() {
        return ver;
    }

    public void setVer(UpdateBean ver) {
        this.ver = ver;
    }

    public class SwitchBean {
        private int review;

        public int getReview() {
            return review;
        }

        public void setReview(int review) {
            this.review = review;
        }
    }
}
