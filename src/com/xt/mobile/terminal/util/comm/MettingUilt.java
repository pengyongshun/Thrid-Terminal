package com.xt.mobile.terminal.util.comm;

import com.xt.mobile.terminal.util.RandomUtils;

/**
 * Created by 彭永顺 on 2020/5/17.
 */
public class MettingUilt {

    /**
     * 创建会议ID
     * @return
     */
    public static String creatMettingID(){
       return  RandomUtils.getRandomNumbers(9);
    }
}
