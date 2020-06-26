package com.xt.mobile.terminal.network;

import com.google.gson.Gson;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.PasreJoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMettingBean;
import com.xt.mobile.terminal.bean.MettingListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/18.
 */
public class JsonParseUilt {
    /**
     * 创建会议，将结果解析成 List<MettingListBean>
     * @param json
     * @return
     */
    public static JoinMettingBean getMettingListBeen(String json){
        JoinMettingBean joinMettingBean=null;
        if (json !=null && json.length()>0){
            if (json.indexOf("sceneID") >= 0 ){
                //代表是创建会议后直接加入会议
                Gson gson=new Gson();
                PasreJoinMettingBean bean = gson.fromJson(json, PasreJoinMettingBean.class);
                if (bean !=null){
                    joinMettingBean=new JoinMettingBean();
                    joinMettingBean.setUserName(bean.getExtend().getUserName());
                    joinMettingBean.setUserID(bean.getExtend().getUserID());
                    String body = bean.getBody();
                    if (body !=null && body.length()>0){
                        ParseMettingBean mettingBean = gson.fromJson(body, ParseMettingBean.class);
                        if (mettingBean !=null){
                            joinMettingBean.setSceneID(mettingBean.getParams().getSceneID());
                            joinMettingBean.setSceneName(mettingBean.getParams().getSceneName());
                            List<ParseMettingBean.ParamsBean.MembersBean> members = mettingBean.getParams().getMembers();
                            if (members !=null && members.size()>0){
                                List<JoinMettingBean.MembersBean> list=new ArrayList<JoinMettingBean.MembersBean>();
                                for (int i = 0; i < members.size(); i++) {
                                    JoinMettingBean.MembersBean bean1=new JoinMettingBean.MembersBean();
                                    ParseMettingBean.ParamsBean.MembersBean membersBean = members.get(i);
                                    bean1.setUserID(membersBean.getUserID());
                                    bean1.setIndex(membersBean.getIndex());
                                    bean1.setRole(membersBean.getRole());
                                    bean1.setStatus(membersBean.getStatus());
                                    list.add(bean1);

                                }

                                joinMettingBean.setMembers(list);
                            }
                        }
                    }
                }



            }
        }
        return joinMettingBean;
    }


}
