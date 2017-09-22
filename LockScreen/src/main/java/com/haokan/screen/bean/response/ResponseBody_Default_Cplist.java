package com.haokan.screen.bean.response;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/2/17.
 */
public class ResponseBody_Default_Cplist {
    public ArrayList<CpBean_Injoo> list;

    /**
     * Created by wangzixu on 2017/2/18.
     */
    public static class CpBean_Injoo {
        private String cpId;
        private String cpName;
        private int isCollect;
        private String tId;
        private String logoUrl;
        private String tName;
        private String collect;
        private String cpInfo;

        public String getCpInfo() {
            return cpInfo;
        }

        public void setCpInfo(String cpInfo) {
            this.cpInfo = cpInfo;
        }

        public String getCollect() {
            return collect;
        }

        public void setCollect(String collect) {
            this.collect = collect;
        }

        public String getCpId() {
            return cpId;
        }

        public void setCpId(String cpId) {
            this.cpId = cpId;
        }

        public String getCpName() {
            return cpName;
        }

        public void setCpName(String cpName) {
            this.cpName = cpName;
        }

        public int getIsCollect() {
            return isCollect;
        }

        public void setIsCollect(int isCollect) {
            this.isCollect = isCollect;
        }

        public String getTId() {
            return tId;
        }

        public void setTId(String tId) {
            this.tId = tId;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }


        public String getTName() {
            return tName;
        }

        public void setTName(String tName) {
            this.tName = tName;
        }
    }
}
