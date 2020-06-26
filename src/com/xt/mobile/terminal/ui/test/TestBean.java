package com.xt.mobile.terminal.ui.test;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/6/16.
 */
public class TestBean {


    /**
     * orgs : []
     * status : 1
     * users : [{"mail":"bug@core.ez","personTrueName":"bug达"},{"mail":"bugda@core.ez","personTrueName":"bug达"},{"mail":"wwd1@core.ez","personTrueName":"王巍达1"},{"mail":"wwd2@core.ez","personTrueName":"王巍达2"},{"mail":"wwd3@core.ez","personTrueName":"王巍达3"},{"mail":"wwd4@core.ez","personTrueName":"王巍达4"},{"mail":"wwd5@core.ez","personTrueName":"王巍达5"},{"mail":"wwd6@core.ez","personTrueName":"王巍达6"},{"mail":"ces@core.ez","personTrueName":"测试人员1"},{"mail":"sda@core.ez","personTrueName":"测试人员3"},{"mail":"asda@core.ez","personTrueName":"测试人员4"},{"mail":"ddd@core.ez","personTrueName":"ddd"}]
     */

    private int status;
    private List<?> orgs;
    private List<UsersBean> users;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<?> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<?> orgs) {
        this.orgs = orgs;
    }

    public List<UsersBean> getUsers() {
        return users;
    }

    public void setUsers(List<UsersBean> users) {
        this.users = users;
    }

    public static class UsersBean {
        /**
         * mail : bug@core.ez
         * personTrueName : bug达
         */

        private String mail;
        private String personTrueName;

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getPersonTrueName() {
            return personTrueName;
        }
        public void setPersonTrueName(String personTrueName) {
            this.personTrueName = personTrueName;
        }
    }
}
