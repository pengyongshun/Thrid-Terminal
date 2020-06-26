package com.xt.mobile.terminal.util.comm;

import com.xt.mobile.terminal.domain.SipInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 彭永顺 on 2020/5/13.
 */
public class ConTanctsUitl {

    /**
     * 当前界面全部取消后，剩余选中的条目
     * @param result  现在取消得数据
     * @param choiceItems  之前选中得数据
     * @return
     */
    public static ArrayList<SipInfo> getAllCalceChoiceItems(ArrayList<SipInfo> result,
                                                    ArrayList<SipInfo> choiceItems) {
        ArrayList<SipInfo> toalData=new ArrayList<SipInfo>();
        if (toalData.size()>0){
            toalData.clear();
        }

        if (result !=null && result.size()>0){
            if (choiceItems !=null && choiceItems.size()>0){
                //此此界面取消得数据
                for (int i = 0; i < result.size(); i++) {
                    SipInfo sipInfo = result.get(i);
                    for (int j = 0; j < choiceItems.size(); j++) {
                        SipInfo info = choiceItems.get(j);
                        if (sipInfo.getUsername().equals(info.getUsername())
                                && sipInfo.getUserid().equals(info.getUserid())){
                            //相同，从选中中删除
                            choiceItems.remove(j);
                            continue;
                        }

                    }
                }

                toalData.addAll(choiceItems);
                return toalData;


            }else {
                //此目录下没有选中得，不需要改变result
                return toalData;

            }
        }else {
            if (toalData.size()>0){
                toalData.clear();
            }
            toalData.addAll(choiceItems);
            return toalData;
        }


    }


    /**
     * 当前界面全部选中后，目前总共选中的条目（会涉及到去重）
     * @param result  现在选中得数据
     * @param choiceItems  之前选中得数据
     * @return
     */
    public static ArrayList<SipInfo> getAllAddChoiceItems(ArrayList<SipInfo> result,
                                                          ArrayList<SipInfo> choiceItems) {
        ArrayList<SipInfo> toalData=new ArrayList<SipInfo>();
        if (toalData.size()>0){
            toalData.clear();
        }

        if (result !=null && result.size()>0){
            if (choiceItems !=null && choiceItems.size()>0){
                //此此界面取消得数据
                for (int i = 0; i < result.size(); i++) {
                    SipInfo sipInfo = result.get(i);
                    for (int j = 0; j < choiceItems.size(); j++) {
                        SipInfo info = choiceItems.get(j);
                        if (sipInfo.getUsername().equals(info.getUsername())
                                && sipInfo.getUserid().equals(info.getUserid())){
                            //相同，从选中中删除
                            choiceItems.remove(j);
                            continue;
                        }

                    }
                }

                if (choiceItems !=null &&choiceItems.size()>0){
                    //t同级别目录下，其他地方有选中的
                    toalData.addAll(result);
                    toalData.addAll(choiceItems);
                }else {
                    //t同级别目录下，其他地方没有选中的
                    toalData.addAll(result);
                }

                return toalData;


            }else {
                //此目录下没有选中得，不需要改变result
                toalData.addAll(result);
                return toalData;

            }
        }else {
            if (toalData.size()>0){
                toalData.clear();
            }
            toalData.addAll(choiceItems);
            return toalData;
        }


    }


    /**
     * 获取选中得item，且在界面显示
     * @param result
     * @return
     */
    public static ArrayList<SipInfo> showCurrUiData(ArrayList<SipInfo> result,
                                              ArrayList<SipInfo> choiceItems) {
        ArrayList<SipInfo> toalData=new ArrayList<SipInfo>();
        ArrayList<SipInfo> xtData=new ArrayList<SipInfo>();
        if (toalData.size()>0){
            toalData.clear();
        }
        if (xtData.size()>0){
            xtData.clear();
        }
        if (result !=null && result.size()>0){
            if (choiceItems !=null && choiceItems.size()>0){
                //此目录下有选中得，需要改变result
                for (int i = 0; i < choiceItems.size(); i++) {
                    SipInfo sipInfo = choiceItems.get(i);
                    for (int j = 0; j < result.size(); j++) {
                        SipInfo info = result.get(j);
                        if (sipInfo.getUsername().equals(info.getUsername())
                                && sipInfo.getUserid().equals(info.getUserid())){
                            //相同，从result中移除，然后改变选中得状态，添加到临时容器
                            info.setSelect(true);
                            xtData.add(info);
                            result.remove(j);
                            continue;
                        }

                    }
                }

                //将选中得放在前面
                if (xtData.size()>0){
                    if (result.size()>0){
                        //还有剩余得
                        toalData.addAll(xtData);
                        toalData.addAll(result);
                    }else {
                        //没有剩余得
                        toalData.addAll(xtData);

                    }
                }else {
                    //此界面都没有选中,不需要改变result
                    toalData.addAll(result);
                }

            }else {
                //此目录下没有选中得，不需要改变result
                toalData.addAll(result);

            }
        }else {
            if (toalData.size()>0){
                toalData.clear();
            }
            if (xtData.size()>0){
                xtData.clear();
            }
        }

        return toalData;


    }

    /**
     * @param choiceItems  以前选中的数据
     * @param result 离线的数据
     * @return  去除离线后现有选中的数据
     */
    public static List<SipInfo> getChoicePeoplesDeletOutline(ArrayList<SipInfo> choiceItems
            ,ArrayList<SipInfo> result) {
        ArrayList<SipInfo> choinceData=new ArrayList<SipInfo>();

        if (result !=null && result.size()>0){
            if (choiceItems !=null && choiceItems.size()>0){
                choinceData.addAll(choiceItems);
                //此目录下有选中得，需要改变result
                for (int i = 0; i < result.size(); i++) {
                    SipInfo sipInfo = result.get(i);
                    for (int j = 0; j < choinceData.size(); j++) {
                        SipInfo info = choinceData.get(j);
                        if (sipInfo.getUsername().equals(info.getUsername())
                                && sipInfo.getUserid().equals(info.getUserid())){
                            //相同，从result中移除，然后改变选中得状态，添加到临时容器
                            choinceData.remove(j);
                            continue;
                        }

                    }
                }


            }
        }else {
            if (choiceItems !=null && choiceItems.size()>0){
                choinceData.addAll(choiceItems);
            }

        }
        return choinceData;
    }



    /**
     * @param outItems  离线的数据
     * @param result 总数据
     * @return  将离线人员的数据的状态改变
     */
    public static List<SipInfo> getOutlinePeopleStauts(ArrayList<SipInfo> outItems
            ,ArrayList<SipInfo> result) {
        ArrayList<SipInfo> total=new ArrayList<SipInfo>();

        if (result !=null && result.size()>0){
            total.addAll(result);
            if (outItems !=null && outItems.size()>0){

                //此目录下有选中得，需要改变result
                for (int i = 0; i < outItems.size(); i++) {
                    SipInfo sipInfo = outItems.get(i);
                    for (int j = 0; j < total.size(); j++) {
                        SipInfo info = total.get(j);
                        if (sipInfo.getUsername().equals(info.getUsername())
                                && sipInfo.getUserid().equals(info.getUserid())){
                            //相同，从result中移除，然后改变选中得状态，添加到临时容器
                            info.setSelect(false);
                            continue;
                        }

                    }
                }


            }
        }
        return total;
    }


    /**
     * @param result 总数据
     * @return  将离线人员的数据的状态改变
     */
    public static List<SipInfo> getOutlinePeoples(ArrayList<SipInfo> result) {
        ArrayList<SipInfo> total=new ArrayList<SipInfo>();
            if (result !=null && result.size()>0){

                //此目录下有选中得，需要改变result
                for (int i = 0; i < result.size(); i++) {
                    SipInfo sipInfo = result.get(i);
                    int status = sipInfo.getStatus();
                    switch (status){
                        case 1:
                            //在线
                            break;
                        case 0:
                            //不在线
                            total.add(sipInfo);
                            break;
                    }

                }


            }

        return total;
    }





    /**
     * @param result 总数据
     * @return  获取在线人员的数据
     */
    public static List<SipInfo> getOnlinePeoples(ArrayList<SipInfo> result) {
        ArrayList<SipInfo> total=new ArrayList<SipInfo>();
        if (result !=null && result.size()>0){

            //此目录下有选中得，需要改变result
            for (int i = 0; i < result.size(); i++) {
                SipInfo sipInfo = result.get(i);
                int status = sipInfo.getStatus();
                switch (status){
                    case 1:
                        //在线
                        total.add(sipInfo);
                        break;
                    case 0:
                        //不在线

                        break;
                }

            }


        }

        return total;
    }


    /**
     * 判断当前界面是否都选中
     * @param choiceItems  选中的
     * @param result  当前界面在线的所有的数据
     * @return
     */
    public static boolean getCurrUiChoiceItemCount(ArrayList<SipInfo> choiceItems
            ,ArrayList<SipInfo> result) {
            if (choiceItems !=null && choiceItems.size()>0){
                ArrayList<SipInfo> data=new ArrayList<SipInfo>();
                data.addAll(choiceItems);
                if (result !=null && result.size()>0){
                    ArrayList<SipInfo> data1=new ArrayList<SipInfo>();
                    data1.addAll(result);
                    for (int i = 0; i < data.size(); i++) {
                        SipInfo sipInfo = data.get(i);
                        String username = sipInfo.getUsername();
                        String userid = sipInfo.getUserid();
                        String belongsys = sipInfo.getBelongsys();
                        for (int j = 0; j < data1.size(); j++) {
                            SipInfo info = data1.get(j);
                            if (userid.equals(info.getUserid())&&
                                    username.equals(info.getUsername())&&
                                    belongsys.equals(info.getBelongsys())){
                                //相同
                                data1.remove(j);
                            }
                        }
                    }
                    if (data1.size()>0){
                        return false;
                    }else {
                        return true;
                    }
                }else {
                    return false;
                }
            }else {
                //此目录下没有选中得，不需要改变result
                return false;

            }



    }


    /**
     * 判断是否存在
     * @param sipInfo
     */
    public static Boolean isEixit(ArrayList<SipInfo> data, SipInfo sipInfo) {
            if (data.size()>0){
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getBelongsys().equals(sipInfo.getBelongsys())
                            && data.get(i).getUsername().equals(sipInfo.getUsername())){
                        //相同
                        return true;
                    }
                }
                return false;

            }else {
                return false;

            }

    }
    /**
     * 删除
     * @param sipInfo
     */
    public static  List<SipInfo>  delet(ArrayList<SipInfo> data, SipInfo sipInfo) {
        List<SipInfo> list=new ArrayList<>();
        if (data.size()>0){
            list.addAll(data);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getBelongsys().equals(sipInfo.getBelongsys())
                        && list.get(i).getUsername().equals(sipInfo.getUsername())){
                    //相同  移除
                    list.remove(i);
                }
            }
            return list;

        }else {
            return list;

        }

    }


    /**
     * 添加
     * @param sipInfo
     */
    public static  List<SipInfo> add(ArrayList<SipInfo> data, SipInfo sipInfo) {
        List<SipInfo> list=new ArrayList<>();
        boolean isXT=false;
        if (data.size()>0){
            list.addAll(data);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getBelongsys().equals(sipInfo.getBelongsys())
                        && list.get(i).getUsername().equals(sipInfo.getUsername())){
                    //相同  移除
                    list.remove(i);
                    list.add(sipInfo);
                    isXT=true;
                }
            }

            if (isXT){

            }else {
                list.add(sipInfo);
            }
            return list;

        }else {
            list.add(sipInfo);
            return list;

        }

    }

    /**
     * 选中中，消除相同的,对选中得进行收集
     * @param data
     * @param sipInfo
     * @param isAdd
     * @param isShow
     * @return
     */
    public static ArrayList<SipInfo> getChoiceDiffernetBean(ArrayList<SipInfo> data,
                                                      SipInfo sipInfo,boolean isAdd,boolean isShow) {
        if (isShow){
            return data;
        }else {
            if (data.size()>0){
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getBelongsys().equals(sipInfo.getBelongsys())
                            && data.get(i).getUsername().equals(sipInfo.getUsername())){
                        //相同，移除
                        data.remove(i);
                    }
                }
                //添加
                if (isAdd){
                    data.add(sipInfo);
                }

            }else {
                if (isAdd){
                    //添加
                    data.add(sipInfo);
                }
            }
        }


        return data;


    }


    /**
     * 判断当前界面是否有选中的
     * @param choiceItems
     * @param dirs 为选中的item的上一个所有目录
     * @return
     */
    public static Map<String,ArrayList<SipInfo>> isCurrUiHaveData(ArrayList<SipInfo> choiceItems, ArrayList<SipInfo> dirs){
        Map<String,ArrayList<SipInfo>> map=new HashMap<String,ArrayList<SipInfo>>();
        if (dirs !=null &&  dirs.size()>0){
            if (choiceItems !=null && choiceItems.size()>0){
                for (int i = 0; i  < dirs.size(); i++) {
                    SipInfo dir = dirs.get(i);
                    String userid = dir.getUserid();
                    ArrayList<SipInfo> data=new ArrayList<SipInfo>();
                    for (int j = 0; j < choiceItems.size(); j++) {
                        String belongsys = choiceItems.get(j).getBelongsys();
                        if (userid.equals(belongsys)){
                            //相同，代表是一组的
                            data.add(choiceItems.get(j));
                            //choiceItems.remove(j);

                        }
                    }
                    map.put(userid,data);


                }

                return map;

            }else {
                //没有选中的
                return null;

            }
        }else {
            //没有上一个目录，此时为空
            return null;
        }


    }
}
