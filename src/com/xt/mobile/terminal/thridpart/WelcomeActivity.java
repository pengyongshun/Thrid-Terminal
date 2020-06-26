package com.xt.mobile.terminal.thridpart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.bean.QueryBindDeviceBean;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.media.MediaUtils;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.network.http.NetUrl;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseInitMobileMedia;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseInitMobileMrdiaBody;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseUserIdSwtich;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseUserIdSwtichBody;
import com.xt.mobile.terminal.network.sysim.HttpAsyncTask;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WebSocketUitl;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sipcapture.CaptureVideoService;
import com.xt.mobile.terminal.thridpart.bean.EntranceXT;
import com.xt.mobile.terminal.thridpart.bean.InvitePeopleBean;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.ui.activity.VedioMettingActivity;
import com.xt.mobile.terminal.util.CountdownUtil;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.MediaServiceUitl;
import com.xt.mobile.terminal.util.MediaUtil;
import com.xt.mobile.terminal.util.SystemUtil;
import com.xt.mobile.terminal.util.TimeUitls;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.util.ToolPhone;
import com.xt.mobile.terminal.util.VibrateUtil;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 入口
 * 用户类型  userType 0--自己  1--第三方
 */
public class WelcomeActivity extends BaseActivity implements CountdownUtil.OnCountdownListener, RequestUitl.HttpResultCall {

    private TextView countTV;
    private TextView mJoinResultTv;
    private CountdownUtil countdownUtil;
    //第三方的信息
    private String thridUserName;
    private String thridPassWord;
    private String thridToken;
    private String thridMeetingId;
    private boolean isThridCreatMeeting = false;
    private String broadcastAction;
    private List<EntranceXT.BroadcastName> broadcastNames;
    private String contacts;


    private UserMessge userMessge;
    //采集端
    private String captureWidth = "320";
    private String captureHeight = "240";
    private String captureFrameRate = "10";
    private String captureBitRate = "500000";
    //ip地址

//    public static  String corePort="";
//    public static String coreIp="";

//    public static String  coreIp="101.37.24.254";
//    public static String corePort="443";
//    //编码端(模拟)账号：彭永顺2
//    private String encodeId="00010000281";
//    private String encodePwd="123456";
//    private String encodePort="5071";
//    //这里是模拟的登陆
//    private String str_Username="pengyongshun1";
//    private String str_Password="123456";


    public static String coreIp = "192.168.101.1";
    public static String corePort = "180";

    private String encodeId = "00010000085";
    private String encodePwd = "123456";
    private String encodePort = "182";
    //这里是模拟的登陆
    private String str_Username = "pengyongshun";
    private String str_Password = "123456";

    //注册时候服务端的IP和端口
    public static final String serverIP = "192.168.101.1";
    public static final String serverPort = "182";
    private RequestUitl instans;
    private WebSocketUitl webSocketUitl;

    private boolean isLoginSuccess = false;

    private boolean isSuccess = false;
    private MediaServiceUitl mediaServiceUitl;
    private HttpAsyncTask mAsyncTask;
    private String groupId;
    private List<InvitePeopleBean.ChoicePeople> peopleBeen=new ArrayList<>();
    private String chairmanID;
    //主席  测试1
    public static final String chair_group="{\"broadcastNames\":[{\"key\":\"create_meeting\",\"value\":\"android.broadcast.app.xt.create_meeting\"},{\"key\":\"member_refuse\",\"value\":\"android.broadcast.app.xt.member_refuse\"},{\"key\":\"member_leave\",\"value\":\"android.broadcast.app.xt.member_leave\"},{\"key\":\"chairman_invite\",\"value\":\"android.broadcast.app.xt.chairman_invite\"},{\"key\":\"in_meeting_number\",\"value\":\"android.broadcast.app.xt.in.meeting.number\"},{\"key\":\"chairman_stop_meeting\",\"value\":\"android.broadcast.app.xt.chairman_stop_meeting\"}],\"chairmanID\":\"ces@core.ez\",\"contacts\":[{\"userName\":\"bug达\",\"userId\":\"bug@core.ez\"},{\"userName\":\"bug达\",\"userId\":\"bugda@core.ez\"},{\"userName\":\"王巍达1\",\"userId\":\"wwd1@core.ez\"},{\"userName\":\"王巍达2\",\"userId\":\"wwd2@core.ez\"},{\"userName\":\"王巍达3\",\"userId\":\"wwd3@core.ez\"},{\"userName\":\"王巍达4\",\"userId\":\"wwd4@core.ez\"},{\"userName\":\"王巍达5\",\"userId\":\"wwd5@core.ez\"},{\"userName\":\"王巍达6\",\"userId\":\"wwd6@core.ez\"},{\"userName\":\"测试人员1\",\"userId\":\"ces@core.ez\"},{\"userName\":\"测试人员3\",\"userId\":\"sda@core.ez\"},{\"userName\":\"测试人员4\",\"userId\":\"asda@core.ez\"},{\"userName\":\"ddd\",\"userId\":\"ddd@core.ez\"}],\"creatMeeting\":true,\"meetingType\":\"group\",\"meetingID\":\"136176474\",\"userID\":\"ces@core.ez\",\"xt_ip\":\"192.168.101.1\",\"xt_port\":\"180\",\"timeOut\":\"20\"}";
    //成员 王巍达2
    public static final String member_group="{\"broadcastNames\":[{\"key\":\"create_meeting\",\"value\":\"android.broadcast.app.xt.create_meeting\"},{\"key\":\"member_refuse\",\"value\":\"android.broadcast.app.xt.member_refuse\"},{\"key\":\"member_leave\",\"value\":\"android.broadcast.app.xt.member_leave\"},{\"key\":\"chairman_invite\",\"value\":\"android.broadcast.app.xt.chairman_invite\"},{\"key\":\"in_meeting_number\",\"value\":\"android.broadcast.app.xt.in.meeting.number\"},{\"key\":\"chairman_stop_meeting\",\"value\":\"android.broadcast.app.xt.chairman_stop_meeting\"}],\"chairmanID\":\"ces@core.ez\",\"contacts\":[{\"userName\":\"bug达\",\"userId\":\"bug@core.ez\"},{\"userName\":\"bug达\",\"userId\":\"bugda@core.ez\"},{\"userName\":\"王巍达1\",\"userId\":\"wwd1@core.ez\"},{\"userName\":\"王巍达2\",\"userId\":\"wwd2@core.ez\"},{\"userName\":\"王巍达3\",\"userId\":\"wwd3@core.ez\"},{\"userName\":\"王巍达4\",\"userId\":\"wwd4@core.ez\"},{\"userName\":\"王巍达5\",\"userId\":\"wwd5@core.ez\"},{\"userName\":\"王巍达6\",\"userId\":\"wwd6@core.ez\"},{\"userName\":\"测试人员1\",\"userId\":\"ces@core.ez\"},{\"userName\":\"测试人员3\",\"userId\":\"sda@core.ez\"},{\"userName\":\"测试人员4\",\"userId\":\"asda@core.ez\"},{\"userName\":\"ddd\",\"userId\":\"ddd@core.ez\"}],\"creatMeeting\":false,\"meetingType\":\"group\",\"meetingID\":\"136176474\",\"userID\":\"wwd2@core.ez\",\"xt_ip\":\"192.168.101.1\",\"xt_port\":\"180\",\"timeOut\":\"20\"}";
    private boolean isError=false;
    private String callOutTime="";
    private int mFlagLoginFinish = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        countTV = (TextView) findViewById(R.id.activity_wecome_count_tv);
        mJoinResultTv = (TextView) findViewById(R.id.activity_wecome_join_result_tv);
        instans = RequestUitl.getInstans(this, this);
        webSocketUitl = WebSocketUitl.getInstans(this, this);
        //初始化计时器
        countdownUtil = CountdownUtil.newInstance();
        countdownUtil.setTotalTime(30 * 1000);
        countdownUtil.setIntervalTime(1 * 1000);
        countdownUtil.callback(this);

        userMessge = UserMessge.getInstans(this);
        mediaServiceUitl = MediaServiceUitl.getInstans(this);

        mFlagLoginFinish = 0;
        //addData();
        //addData1();

        //获取参数
        PLog.d("WelcomeActivity","--------启动aar包------获取kx传来的数据---->开始时间：time="+ TimeUitls.getLongSysnTime());
        Bundle xt_entrance = getIntent().getBundleExtra(XTContants.APP_ENTRANCE);
        if (xt_entrance != null) {
            String result = xt_entrance.getString(XTContants.APP_ENTRANCE_DATA);
            if (result != null && result.length() > 0) {
                EntranceXT entranceXT = FastJsonTools.json2BeanObject(result, EntranceXT.class);
                if (entranceXT != null) {
                    //用户名
                    thridUserName = entranceXT.getUserID();
                    //密码
                    thridPassWord = "";
                    //用户的TOKEN
                    thridToken = "";
                    //meetingId
                    thridMeetingId = entranceXT.getMeetingID();
                    //iscreatmeeting  ture-正在创建会议中  false--会议已经创建完成
                    isThridCreatMeeting = entranceXT.isCreatMeeting();

                    //创建会议成功后返回meetingID给客户的广播唯一标识
                    broadcastNames = entranceXT.getBroadcastNames();

                    //ip地址
                    coreIp = entranceXT.getXt_ip();
                    //端口号
                    corePort = entranceXT.getXt_port();

                    //通讯录的信息
                    List<InvitePeopleBean.ChoicePeople> peopleS = entranceXT.getContacts();
                    if (peopleS !=null && peopleS.size()>0){
                        Gson gson = new Gson();
                        peopleBeen.addAll(peopleS);
                        contacts = gson.toJson(peopleBeen);
                    }else {
                        contacts ="";
                    }
                    PLog.d("WelcomeActivity","-------contacts---------->contacts ="+contacts);


                    //群id  单聊为空，群聊有值
                    groupId = entranceXT.getMeetingType();
                    //主席id  创建者的时候为空  成员的时候，有值
                    chairmanID = entranceXT.getChairmanID();
                    //通话超时
                    callOutTime=entranceXT.getTimeOut();
                    judgeData();
                } else {
                    ToastUtil.showShort(WelcomeActivity.this, "参数不能为空");
                    finish();
                }
            } else {
                ToastUtil.showShort(WelcomeActivity.this, "参数不能为空");
                finish();
            }

        } else {
            ToastUtil.showShort(WelcomeActivity.this, "参数不能为空");
            finish();
            //非第三方的
            // ActivityTools.startActivity(WelcomeActivity.this,ActivityLogin.class,true);
        }

    }

    private void addData() {
        String chair_group = getResources().getString(R.string.chair_group);
        EntranceXT entranceXT = FastJsonTools.json2BeanObject(chair_group, EntranceXT.class);
        List<InvitePeopleBean.ChoicePeople> contacts = entranceXT.getContacts();

        Intent intent=new Intent(this,ThridJoinListActivity.class);
        String json="";
        if (contacts.size()>0){
            Gson gson=new Gson();
            json=gson.toJson(contacts);
        }else {
            json="";
        }

        intent.putExtra("json",json);
        intent.putExtra("isCreatMeeting",false);
        intent.putExtra("meetingId","1267468622");
        startActivity(intent);
    }

    private void addData1(){
        EntranceXT entranceXT = FastJsonTools.json2BeanObject(chair_group, EntranceXT.class);
        if (entranceXT != null) {
            //用户名
            thridUserName = entranceXT.getUserID();
            //密码
            thridPassWord = "";
            //用户的TOKEN
            thridToken = "";
            //meetingId
            thridMeetingId = entranceXT.getMeetingID();
            //iscreatmeeting  ture-正在创建会议中  false--会议已经创建完成
            isThridCreatMeeting = entranceXT.isCreatMeeting();

            //创建会议成功后返回meetingID给客户的广播唯一标识
            broadcastNames = entranceXT.getBroadcastNames();

            //ip地址
            coreIp = entranceXT.getXt_ip();
            //端口号
            corePort = entranceXT.getXt_port();

            //通讯录的信息
            List<InvitePeopleBean.ChoicePeople> peopleS = entranceXT.getContacts();
            if (peopleS !=null && peopleS.size()>0){
                Gson gson = new Gson();
                peopleBeen.addAll(peopleS);
                contacts = gson.toJson(peopleBeen);
            }else {
                contacts ="";
            }
            PLog.d("WelcomeActivity","-------contacts---------->contacts ="+contacts);

            //群id  单聊为空，群聊有值
            groupId = entranceXT.getMeetingType();
            //主席id  创建者的时候为空  成员的时候，有值
            chairmanID = entranceXT.getChairmanID();
            //通话超时
            callOutTime=entranceXT.getTimeOut();
            judgeData();
        } else {
            ToastUtil.showShort(WelcomeActivity.this, "参数不能为空");
            finish();
        }
    }


    private void judgeData() {
        if (groupId !=null && groupId.length()>0 ){
            if ( groupId.equals(XTContants.MEETING_SINGLE) ||
                    groupId.equals(XTContants.MEETING_GROUP)){
                if (coreIp != null && coreIp.length() > 0 &&
                        corePort != null && corePort.length() > 0) {
                    if (thridUserName != null && thridUserName.length() > 0) {
                        if (broadcastNames != null && broadcastNames.size() > 0) {
                            if (!isThridCreatMeeting) {
                                if (thridMeetingId != null && thridMeetingId.length() > 0) {
                                    if (chairmanID !=null && chairmanID.length()>0){
                                        isSuccess = true;
                                        //保存数据
                                        saveUserMessgeData();
                                        //请求公司的登陆服务
                                        if (isServerCfg()) {
                                            mUserId = "";
                                            mUserName = "";
                                            mTokenKey = "";
                                        }
                                        PLog.d("WelcomeActivity","--------启动aar包------获取kx传来的数据---->结束时间：time="+ TimeUitls.getLongSysnTime());
                                        //id转换
                                        onSwtichID();
                                        //startLogin();
                                        PLog.d("WelcomeActivity", "----------judgeData------->成员");
                                    }else {
                                        ToastUtil.showShort(WelcomeActivity.this,"群主id不能为空");
                                    }


                                } else {
                                    ToastUtil.showShort(WelcomeActivity.this, "会议id为空");
                                    finish();
                                }
                            } else {
                                isSuccess = true;
                                //保存数据
                                saveUserMessgeData();
                                //请求公司的登陆服务
                                if (isServerCfg()) {
                                    mUserId = "";
                                    mUserName = "";
                                    mTokenKey = "";

                                }

                                //id转换
                                onSwtichID();
                                //startLogin();
                                PLog.d("WelcomeActivity", "----------judgeData------->创建者");
                            }
                        } else {
                            ToastUtil.showShort(WelcomeActivity.this, "广播名不能为空");
                            finish();
                        }

                    } else {
                        ToastUtil.showShort(WelcomeActivity.this, "用户名为空");
                        finish();
                    }
                } else {
                    ToastUtil.showShort(WelcomeActivity.this, "ip和端口不能为空");
                    finish();
                }
            }else {
                ToastUtil.showShort(WelcomeActivity.this,"会议类型不正确");
                finish();
            }

        }else {
            ToastUtil.showShort(WelcomeActivity.this, "会议类型不能为空");
            finish();
        }


    }


    private boolean isServerCfg() {

        getDeviceInfo();
        return true;
    }


    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {
        int size[] = ToolPhone.getInstance(this).getScreenSize();
        ConstantsValues.ScreenWidth = size[0];
        ConstantsValues.ScreenHeight = size[1];

        int sdkVersion = ToolPhone.getInstance(this).getDeviceSDKVersion();
        ConstantsValues.SdkVersion = sdkVersion;

        String model = ToolPhone.getInstance(this).getDeviceModel();
        ConstantsValues.DeviceModel = model;

        String brand = ToolPhone.getInstance(this).getDeviceBrand();
        ConstantsValues.DeviceBrand = brand;

        String display = ToolPhone.getInstance(this).getDeviceDisplay();
        ConstantsValues.DeviceDisplay = display;

        String product = ToolPhone.getInstance(this).getDeviceProduct();
        ConstantsValues.DeviceProduct = product;
    }

    /**
     * 保存用户信息
     */
    private void saveUserMessgeData() {
        //保存第三方的信息
        userMessge.setThirdPartyCreatMeeting(isThridCreatMeeting);
        if (thridMeetingId != null && thridMeetingId.length() > 0) {
            userMessge.setThirdPartyMeetingId(thridMeetingId);

        } else {
            thridMeetingId = "";
            userMessge.setThirdPartyMeetingId("");

        }

        if (thridPassWord != null && thridPassWord.length() > 0) {
            userMessge.setThirdPartyPwd(thridPassWord);
        } else {
            thridPassWord = "";
            userMessge.setThirdPartyPwd("");
        }
        if (thridToken != null && thridToken.length() > 0) {
            userMessge.setThirdPartyTokenkey(thridToken);
        } else {
            thridToken = "";
            userMessge.setThirdPartyTokenkey("");
        }

        userMessge.setThirdPartyUserName(thridUserName);

        //保存广播名
        Gson gson = new Gson();
        broadcastAction = gson.toJson(broadcastNames);
        userMessge.setThirdPartyBroadcastTag(broadcastAction);
        //保存通讯录的信息
        if (contacts != null && contacts.length() > 0) {
            userMessge.setThirdPartyPeoples(contacts);
        } else {
            contacts = "";
            userMessge.setThirdPartyPeoples("");
        }
        //保存ip和端口号
        userMessge.setCoreIP(coreIp);
        userMessge.setCorePort(corePort);

        //群id
        if (groupId !=null && groupId.length()>0){
            userMessge.setThirdPartyGroupId(groupId);
        }else {
            groupId="";
            userMessge.setThirdPartyGroupId("");
        }
        //群主ID
        if (isThridCreatMeeting){
            //主席
            userMessge.setThirdPartyChairmanID(thridUserName);
        }else {
            //成员
            userMessge.setThirdPartyChairmanID(chairmanID);
        }

        //通话超时
        if (callOutTime !=null && callOutTime.length()>0){
            userMessge.setCallOutTime(callOutTime);
        }else {
            userMessge.setCallOutTime("60");
        }

        //采集宽度
        ConstantsValues.vCaptureWidth = Integer.valueOf(captureWidth);
        //采集高度
        ConstantsValues.vCaptureHeight = Integer.valueOf(captureHeight);
        //采集帧率
        ConstantsValues.vCaptureFrameRate = Integer.valueOf(captureFrameRate);
        //采集码率
        ConstantsValues.vCaptureBitRate = Integer.valueOf(captureBitRate);
        ConstantsValues.v_CORE_PORT = Integer.valueOf(corePort);
        //采集的宽度、高度、帧率、码率
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConstantsValues.CaptureWidth, captureWidth);
        edit.putString(ConstantsValues.CaptureHeight, captureHeight);
        edit.putString(ConstantsValues.CaptureFrameRate, captureFrameRate);
        edit.putString(ConstantsValues.CaptureBitRate, captureBitRate);
        edit.commit();


//        //采集端（编码）
//        userMessge.setEncodePwd(encodePwd);
//        userMessge.setEncodePort(encodePort);
//        userMessge.setEncodeId(encodeId);
        PLog.d("WelcomeActivity", "----------saveUserMessgeData------->broadcastNames=" + broadcastNames.size());
        PLog.d("WelcomeActivity", "----------saveUserMessgeData------->contacts=" + (!contacts.equals("")));
        PLog.d("WelcomeActivity", "----------saveUserMessgeData------->thridMeetingId=" + thridMeetingId);
        PLog.d("WelcomeActivity", "----------saveUserMessgeData------->kx_Username=" + thridUserName);
        PLog.d("WelcomeActivity", "----------saveUserMessgeData------->coreIp=" + coreIp + "\n corePort=" + corePort);
    }


    /**
     * 登陆
     * 我们自己的userId和密码是
     * 用的userId和密码是我们自己的
     */
    private void startLogin() {
        if (!isError){
            ToolLog.i("开始登录");
            // 检查服务器地址信息
            if (!isServerCfg()) {
                return;
            }
            // 登陆服务器
            mFlagLoginFinish = 0;
            // 登陆服务器
            mUserId = "";
            mUserName = "";
            mTokenKey = "";

            onStartLogin();
            startJWebSClientService();
            PLog.d("WelcomeActivity", "----------startLogin------->开始JWebSClientService");
        }

    }


    /**
     * 登陆的网络请求
     */
    public void onStartLogin() {
        PLog.d("WelcomeActivity","--------启动aar包------登陆---->开始时间：time="+ TimeUitls.getLongSysnTime());
        if (!isError){
            //参数
            Map<String, String> map = new HashMap<String, String>();
            map.put("userID", str_Username);
            map.put("password", str_Password);
            map.put("vcode", "aaaa");
            List<NameValuePair> params = MoudleParams.getloginParams(this, map);
            //请求
            if (instans != null) {
                PLog.d("WelcomeActivity", "----------onStartLogin------->xt_Username=" + str_Username);
                instans.sendRequest(params, true, Constants.HTTP_GET_LOGIN_LOGIN);
            }
        }
    }


    /**
     * 获取兴图的userId
     */
    public void onSwtichID() {
        PLog.d("WelcomeActivity","--------启动aar包------userID转换---->开始时间：time="+ TimeUitls.getLongSysnTime());
        if (!isError){
            if (thridUserName != null && thridUserName.length() > 0) {
                //参数
                List<NameValuePair> params = MoudleParams.getXTUserIdParams(this, thridUserName);
                //请求
                if (instans != null) {
                    instans.sendRequest(params, true, Constants.HTTP_GET_KXID_SWTICH_XTID);
                }
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////


    protected void onLoginStatus(boolean loginSuccess) {
        ToolLog.i("loginSuccess:" + loginSuccess);
        if (loginSuccess) {
            PLog.d("WelcomeActivity", "----------onLoginStatus------->success");
            mFlagLoginFinish++;
            if (mFlagLoginFinish >= 2) {
                finishLogin();
            }
        } else {
            PLog.d("WelcomeActivity", "----------onLoginStatus------->登录sip服务失败");
            showToast("登录sip服务失败");
        }
    }

    protected void onCaptureLoginStatus(boolean loginSuccess) {
        ToolLog.i("onCaptureLoginStatus:" + loginSuccess);
        if (!isError){
            if (loginSuccess) {
                PLog.d("WelcomeActivity", "----------onCaptureLoginStatus------->success");
                mFlagLoginFinish++;
                if (mFlagLoginFinish >= 2) {
                    finishLogin();
                }
            } else {
                PLog.d("WelcomeActivity", "----------onCaptureLoginStatus------->登录采集sip服务失败");
                showToast("登录采集sip服务失败");
            }
        }

    }

//    private void startRegister() {
//         if (!isError){
//             if (isLoginSuccess) {
//                 String sipServerId = userMessge.getDecodeServerID();
//                 String sipServerIp = userMessge.getDecodeServerIP();
//                 String sipServerPort = userMessge.getDecodeServerPort();
//                 String localIp = XTUtils.getLocalIpAddress();
//                 if (localIp == null || localIp.length() <= 0 || sipServerIp.length() <= 0
//                         || sipServerId.length() <= 0 || sipServerPort.length() <= 0) {
//                     ToolLog.i("==========获取SIP服务信息失败: sipServerIp[" + sipServerIp + "] sipServerPort["
//                             + sipServerPort + "] sipServerId[" + sipServerId + "] localIp" + localIp
//                             + "]");
//                     onLoginStatus(false);
//                     PLog.d("WelcomeActivity", "----------获取SIP服务信息失败------->注册失败");
//                 } else {
//                     Intent intent1 = new Intent(VideoService.ACTION_PLAY_SIP_INIT);
//                     sipServerIp = serverIP;
//                     sipServerPort = serverPort;
//                     PLog.d("==========SIP=====", "----------SIP服务信息------->开始注册");
//                     PLog.d("*********开始注册解码***********");
//                     PLog.d("*********开始注册解码***********");
//                     PLog.d("sipid="+sipServerId+",ip="+sipServerIp+",port="+sipServerPort+",localIp"+localIp);
//                     PLog.d("*********开始注册解码***********");
//                     PLog.d("*********开始注册解码***********");
//                     intent1.putExtra("SipServerIp", sipServerIp);
//                     intent1.putExtra("SipServerId", sipServerId);
//                     intent1.putExtra("SipServerPort", sipServerPort);
//                     intent1.putExtra("LocalIp", localIp);
//                     sendBroadcast(intent1);
//                     ToolLog.i("==========SIP=====: sipServerIp[" + sipServerIp + "] sipServerPort["
//                             + sipServerPort + "] sipServerId[" + sipServerId + "] localIp" + localIp
//                             + "]");
//                     PLog.d("WelcomeActivity", "----------SIP服务信息------->注册成功");
//
//                     String encodeId = userMessge.getEncodeId();
//                     String encodePwd = userMessge.getEncodePwd();
//                     String encodePort = userMessge.getEncodePort();
//                     if (encodeId.length() <= 0 || encodePwd.length() <= 0 || encodePort.length() <= 0) {
//                         ToolLog.i("==========获取采集设备信息失败: encodeId[" + encodeId + "] encodePwd["
//                                 + encodePwd + "] encodePort[" + encodePort + "]");
//                         onCaptureLoginStatus(false);
//                         PLog.d("WelcomeActivity", "----------获取采集设备信息失败------->注册失败");
//                     } else {
//                         ConstantsValues.v_CAPTURE_SIP_ID = encodeId;
//                         sipServerIp = serverIP;
//                         sipServerPort = serverPort;
//                         PLog.d("==========SIP=====", "----------capture_SIP服务信息------->开始注册");
//                         PLog.d("*********开始注册编码***********");
//                         PLog.d("*********开始注册编码***********");
//                         PLog.d("EncodeId="+encodeId+",EncodePort="+encodePort+",LocalIp="+localIp+",EncodePwd="+encodePwd);
//                         PLog.d("*********开始注册编码***********");
//                         PLog.d("*********开始注册编码***********");
//                         Intent intent2 = new Intent(CaptureVideoService.ACTION_CAPTURE_SIP_INIT);
//                         intent2.putExtra("SipServerIp", sipServerIp);
//                         intent2.putExtra("SipServerId", sipServerId);
//                         intent2.putExtra("SipServerPort", sipServerPort);
//                         intent2.putExtra("LocalIp", localIp);
//                         intent2.putExtra("EncodeId", encodeId);
//                         intent2.putExtra("EncodePwd", encodePwd);
//                         intent2.putExtra("EncodePort", encodePort);
//                         sendBroadcast(intent2);
//
//                         PLog.d("WelcomeActivity", "----------获取采集设备信息------->注册成功");
//                         ToolLog.i("===aaa===ScreenSize:" + ConstantsValues.ScreenWidth + "-" + ConstantsValues.ScreenHeight
//                                 + "; sdkVersion:" + ConstantsValues.SdkVersion + "; DeviceModel:" + ConstantsValues.DeviceModel
//                                 + "; DeviceBrand:" + ConstantsValues.DeviceBrand + "; display:" + ConstantsValues.DeviceDisplay
//                                 + "; product:" + ConstantsValues.DeviceProduct);
//                         PLog.d("WelcomeActivity", "----------startRegister------->sipServerIp=" + sipServerIp + "\n sipServerPort=" + sipServerPort);
//                         PLog.d("WelcomeActivity", "----------startRegister------->encodeId=" + encodeId + "\n encodePwd=" + encodePwd + "\n" +
//                                 "encodePort=" + encodePort);
//                         PLog.d("WelcomeActivity", "----------startRegister------->注册成功");
//                     }
//                 }
//
//             }
//         }
//
//    }


    private void startRegister() {
        PLog.d("WelcomeActivity","--------启动aar包------sip服务注册---->开始时间：time="+ TimeUitls.getLongSysnTime());
        if (!isError){
            if (isLoginSuccess) {
                String sipServerId = userMessge.getDecodeServerID();
                String sipServerIp = userMessge.getDecodeServerIP();
                String sipServerPort = userMessge.getDecodeServerPort();
                String localIp = XTUtils.getLocalIpAddress();
                if (localIp == null || localIp.length() <= 0 || sipServerIp.length() <= 0
                        || sipServerId.length() <= 0 || sipServerPort.length() <= 0) {
                    ToolLog.i("==========获取SIP服务信息失败: sipServerIp[" + sipServerIp + "] sipServerPort["
                            + sipServerPort + "] sipServerId[" + sipServerId + "] localIp" + localIp
                            + "]");
                    onLoginStatus(false);
                    PLog.d("WelcomeActivity", "----------获取SIP服务信息失败------->注册失败");
                } else {
                    Intent intent1 = new Intent(VideoService.ACTION_PLAY_SIP_INIT);
                    sipServerIp = serverIP;
                    sipServerPort = serverPort;
                    PLog.d("==========SIP=====", "----------SIP服务信息------->开始注册");
                    PLog.d("*********开始注册解码***********");
                    PLog.d("*********开始注册解码***********");
                    PLog.d("sipid="+sipServerId+",ip="+sipServerIp+",port="+sipServerPort+",localIp"+localIp);
                    PLog.d("*********开始注册解码***********");
                    PLog.d("*********开始注册解码***********");
                    intent1.putExtra("SipServerIp", sipServerIp);
                    intent1.putExtra("SipServerId", sipServerId);
                    intent1.putExtra("SipServerPort", sipServerPort);
                    intent1.putExtra("LocalIp", localIp);
                    sendBroadcast(intent1);
                    ToolLog.i("==========SIP=====: sipServerIp[" + sipServerIp + "] sipServerPort["
                            + sipServerPort + "] sipServerId[" + sipServerId + "] localIp" + localIp
                            + "]");
                    PLog.d("WelcomeActivity", "----------SIP服务信息------->注册成功");

                    String encodeId = userMessge.getEncodeId();
                    String encodePwd = userMessge.getEncodePwd();
                    String encodePort = userMessge.getEncodePort();
                    if (encodeId.length() <= 0 || encodePwd.length() <= 0 || encodePort.length() <= 0) {
                        ToolLog.i("==========获取采集设备信息失败: encodeId[" + encodeId + "] encodePwd["
                                + encodePwd + "] encodePort[" + encodePort + "]");
                        onCaptureLoginStatus(false);
                        PLog.d("WelcomeActivity", "----------获取采集设备信息失败------->注册失败");
                    } else {
                        ConstantsValues.v_CAPTURE_SIP_ID = encodeId;
                        sipServerIp = serverIP;
                        sipServerPort = serverPort;
                        PLog.d("==========SIP=====", "----------capture_SIP服务信息------->开始注册");
                        PLog.d("*********开始注册编码***********");
                        PLog.d("*********开始注册编码***********");
                        PLog.d("EncodeId="+encodeId+",EncodePort="+encodePort+",LocalIp="+localIp+",EncodePwd="+encodePwd);
                        PLog.d("*********开始注册编码***********");
                        PLog.d("*********开始注册编码***********");
                        Intent intent2 = new Intent(CaptureVideoService.ACTION_CAPTURE_SIP_INIT);
                        intent2.putExtra("SipServerIp", sipServerIp);
                        intent2.putExtra("SipServerId", sipServerId);
                        intent2.putExtra("SipServerPort", sipServerPort);
                        intent2.putExtra("LocalIp", localIp);
                        intent2.putExtra("EncodeId", encodeId);
                        intent2.putExtra("EncodePwd", encodePwd);
                        intent2.putExtra("EncodePort", encodePort);
                        sendBroadcast(intent2);

                        PLog.d("WelcomeActivity", "----------获取采集设备信息------->注册成功");
                        ToolLog.i("===aaa===ScreenSize:" + ConstantsValues.ScreenWidth + "-" + ConstantsValues.ScreenHeight
                                + "; sdkVersion:" + ConstantsValues.SdkVersion + "; DeviceModel:" + ConstantsValues.DeviceModel
                                + "; DeviceBrand:" + ConstantsValues.DeviceBrand + "; display:" + ConstantsValues.DeviceDisplay
                                + "; product:" + ConstantsValues.DeviceProduct);
                        PLog.d("WelcomeActivity", "----------startRegister------->sipServerIp=" + sipServerIp + "\n sipServerPort=" + sipServerPort);
                        PLog.d("WelcomeActivity", "----------startRegister------->encodeId=" + encodeId + "\n encodePwd=" + encodePwd + "\n" +
                                "encodePort=" + encodePort);
                        PLog.d("WelcomeActivity", "----------startRegister------->注册成功");
                    }
                }

            }
        }

    }
    ///////////////////////////////////////////wss服务启动//////////////////////////

    /**
     * 启动wss，且设置url
     */
    private void startJWebSClientService() {
        if (webSocketUitl != null) {
            webSocketUitl.startJWebSClientService();
        }

    }

    /**
     * 停止wss
     */
    private void stopJWebSClientService() {
        if (webSocketUitl != null) {
            webSocketUitl.stopJWebSClientService(mTokenKey);
        }


    }

    private void onWssSubscribeResource() {
        // 用户资源指令
        WebSocketCommand.getInstance().onSendSubscribeUser();
        // 设备资源指令
        WebSocketCommand.getInstance().onSendSubscribeDevice();
    }


    /**
     * 删除会议
     */
    private void deletMeetingRequest(){
        PLog.d("WelcomeActivity","--------启动aar包------删除会议缓存---->开始时间：time="+ TimeUitls.getLongSysnTime());
        if (!isError){
            String userID = userMessge.getUserID();
            //网络请求
            if (userID != null && userID.length() > 0) {
                //参数
                Map<String,String> map=new HashMap<>();
                map.put("userId",userID);
                List<NameValuePair> params = MoudleParams.deletMeetingParams(this, map);
                //请求
                if (instans != null) {
                    instans.sendRequest(params, true, Constants.HTTP_GET_DELET_MEETING);
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (countdownUtil != null && countdownUtil.isRunning()) {
            countdownUtil.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (countdownUtil != null && !(countdownUtil.isRunning())) {
            countdownUtil.start();
        }
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if (isSuccess) {
            if (!isLoginSuccess) {
                stopJWebSClientService();
                PLog.d("WelcomeActivity", "----------onStop------->停止JWebSClientService");
            }
        }

        super.onStop();
    }

    @Override
    public void onRemain(long millisUntilFinished) {
        //倒计时进行时
        int count = (int) (millisUntilFinished / 1000);
        countTV.setText(count + " s");
    }

    @Override
    public void onFinish() {
        //倒计时已经结束
        mJoinResultTv.setText("会议服务失败，请重新登陆");
        isError=true;
        showLoadingDaliog("连接会议服务失败，正在退出...");
        handler.sendEmptyMessageDelayed(1,1000*2);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideLoadingDialog();
            finish();
        }
    };

    @Override
    public void success(int tag, String result) {
        switch (tag) {
            //删除会议
            case Constants.HTTP_GET_DELET_MEETING:
                PLog.d("WelcomeActivity","--------启动aar包------删除会议缓存---->结束时间：time="+ TimeUitls.getLongSysnTime());
                PLog.d("WelcomeActivity", "----------success------->删除会议成功");
                PLog.d("WelcomeActivity", "----------success------->开始登陆");
                //进行登陆
                startLogin();
                break;
            //获取登陆的信息
            case Constants.HTTP_GET_LOGIN_LOGIN:
                //消除加载框
                try {
                    PLog.d("WelcomeActivity","--------启动aar包------登陆---->结束时间：time="+ TimeUitls.getLongSysnTime());
                    ToolLog.i("=========createUserTokenForWeb====" + result);
                    JSONObject jobj = new JSONObject(result);
                    String State = jobj.getString("responseCode");
                    if (State != null && !State.isEmpty() && State.equals("1")) {
                        JSONObject obj = jobj.getJSONObject("data");
                        String userId = obj.getString("userID");
                        String userName = obj.getString("userName");
                        String token = obj.getString("tokenKey");
                        // String ipAddress = obj.getString("ipAddress");
                        String validTime = obj.getString("validTime");

                        //保存用户信息
                        userMessge.setUserID(userId);
                        userMessge.setUserName(userName);
                        userMessge.setUserTokenkey(token);

                        mUserId = userId;
                        mUserName = userName;
                        mTokenKey = token;

                        isLoginSuccess = true;
                        //初始化媒体
                        PLog.d("WelcomeActivity","--------启动aar包------初始化媒体---->开始时间：time="+ TimeUitls.getLongSysnTime());
                        mediaServiceUitl.initMediaService(mUserId, mUserName, mTokenKey);
                        PLog.d("WelcomeActivity", "----------success------->登陆已成功");
                        PLog.d("WelcomeActivity", "----------success------->开始初始化媒体");
                        ToolLog.i("===aaa===ScreenSize:" + ConstantsValues.ScreenWidth + "-" + ConstantsValues.ScreenHeight
                                + "; sdkVersion:" + ConstantsValues.SdkVersion + "; DeviceModel:" + ConstantsValues.DeviceModel
                                + "; DeviceBrand:" + ConstantsValues.DeviceBrand + "; display:" + ConstantsValues.DeviceDisplay
                                + "; product:" + ConstantsValues.DeviceProduct);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("用户登录失败");
                }
                break;
            //退出登陆
            case Constants.HTTP_GET_LOGIN_DESTROYTOKEN:
                break;
            case Constants.HTTP_GET_KXID_SWTICH_XTID:
                //空信userId转换为兴图的userID
                if (result != null && result.length() > 0) {
                    try {
                        // TODO 进行数据解析
                        PLog.d(result);
                        ParseUserIdSwtich parseUserIdSwtich = FastJsonTools.json2BeanObject(result,
                                ParseUserIdSwtich.class);
                        if (parseUserIdSwtich != null) {
                            String data = parseUserIdSwtich.getData();
                            if (data != null && data.length() > 0) {
                                ParseUserIdSwtichBody parseUserIdSwtichBody = FastJsonTools.
                                        json2BeanObject(data, ParseUserIdSwtichBody.class);
                                //获取兴图的userId
                                String xtUserID = parseUserIdSwtichBody.getXTUserID();
                                str_Username = xtUserID;

                                // 存储已经登陆的账户和密码
                                userMessge.setUserID(str_Username);
                                userMessge.setUserPwd(str_Password);

                                PLog.d("WelcomeActivity", "----------success------->userID转换成功");
                                PLog.d("WelcomeActivity", "----------success------->开始清理上次会议的数据");
                                PLog.d("WelcomeActivity","--------启动aar包------userID转换---->结束时间：time="+ TimeUitls.getLongSysnTime());
                                //清理掉以前的会议信息
                                deletMeetingRequest();
                            } else {
                                PLog.d("WelcomeActivity", "----------success------->userI为空");
                                ToastUtil.showShort(WelcomeActivity.this, "userI为空");
                            }
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
                break;
            case Constants.HTTP_GET_QUERY_BIND_DEVICE:
                //查询设备是否绑定
                //如果已绑定，直接跳转
                //如果未绑定，需要进行绑定
                PLog.d("WelcomeActivity", "----------success------->result="+result);
                try{
                    if (result !=null && result.length()>0){
                        QueryBindDeviceBean bean = FastJsonTools.json2BeanObject(result,
                                QueryBindDeviceBean.class);
                        if (bean !=null){
                            QueryBindDeviceBean.DataBean data = bean.getData();
                            if (data !=null){
                                String encoderItem = data.getEncoderItem();
                                if (encoderItem !=null && encoderItem.length()>0){
                                    //已绑定
                                    finishLogin();
                                }else {
                                    //未绑定
                                    String encodeId = userMessge.getEncodeId();
                                    onBindDevice(encodeId);
                                }
                            }else {
                                //未绑定
                                String encodeId = userMessge.getEncodeId();
                                onBindDevice(encodeId);
                            }
                        }
                    }
                    PLog.d("WelcomeActivity", "----------success------->查询设备是否绑定--->查询成功");
                }catch (Exception e){
                    PLog.d("WelcomeActivity", "----------success------->查询设备是否绑定--->查询失败");
                    e.getStackTrace();
                }

                break;
        }
    }

    @Override
    public void faile(int tag, String error) {
        switch (tag) {
            //获取登陆的信息
            case Constants.HTTP_GET_LOGIN_LOGIN:
                //消除加载框
                PLog.d("WelcomeActivity", "----------faile------->登陆失败");
                showToast("用户认证失败");
                break;
            //删除会议
            case Constants.HTTP_GET_DELET_MEETING:
                PLog.d("WelcomeActivity", "----------faile------->删除会议失败");
                break;
            case Constants.HTTP_GET_KXID_SWTICH_XTID:
                //空信userId转换为兴图的userID
                PLog.d("WelcomeActivity", "----------faile------->用户信息转换失败");
                ToastUtil.showShort(WelcomeActivity.this, "用户信息转换失败");
                break;
            case Constants.HTTP_GET_QUERY_BIND_DEVICE:
                //查询设备是否绑定
                ToastUtil.showShort(WelcomeActivity.this, "查询设备失败");
                PLog.d("WelcomeActivity", "----------faile------->查询设备是否绑定--->查询失败");
                break;
        }
    }


    //////////////////////////////////////////////wss返回结果的地方///////////////////////

    /**
     * Wss请求连接成功与否的地方
     *
     * @param msg
     */
    protected void onReceiveWssConnect(String msg) {

        if (msg.isEmpty()) {
            return;
        } else if (msg.equals("websocket_connected")) {
            // 心跳指令
            hideLoadingDialog();
            if (mUserId != null && mUserName != null && !mUserId.isEmpty() && !mUserName.isEmpty()) {
                if (isLoginSuccess) {
                    PLog.d("WelcomeActivity", "----------onReceiveWssConnect------->正在初始化媒体");
                    mediaServiceUitl.initMediaService(mUserId, mUserName, mTokenKey);
                }

            }

        }
    }

    /**
     * wss请求返回的结果
     *
     * @param msg
     */
    protected void onReceiveWssMessage(String msg) {

        if (msg.isEmpty()) {
            return;
        } else if (msg.indexOf(WssContant.WSS_CLOSE_TIME) >= 0) {

            ToolLog.i("==========onReceiveWssMessage closeTime");

        } else if (msg.indexOf(WssContant.WSS_INFORM_INIT_MOBLIE_MEDIA) >= 0) {
            //初始化移动端媒体服务（即获取SIP消息）
            try {
                ParseInitMobileMedia parseInitMobileMedia = FastJsonTools.
                        json2BeanObject(msg, ParseInitMobileMedia.class);
                if (parseInitMobileMedia != null) {
                    String body = parseInitMobileMedia.getBody();
                    if (body != null && body.length() > 0) {
                        ParseInitMobileMrdiaBody beanObject = FastJsonTools.json2BeanObject(body,
                                ParseInitMobileMrdiaBody.class);
                        ParseInitMobileMrdiaBody.ParamsBean params = beanObject.getParams();
                        if (params != null) {
                            SharedPreferences.Editor edit = sp.edit();
                            //解码端  （播放）
                            userMessge.setDecodeId(params.getDecoderSIPID());
                            userMessge.setDecodePwd(params.getClientPassword());
                            userMessge.setDecodeServerIP(params.getServerIP());
                            userMessge.setDecodeServerPort(params.getServerPort());
                            userMessge.setDecodeServerID(params.getServerID());

                            //编码端（采集）
                            //保存编码端的密码和端口号
                            encodeId = params.getEncoderSIPID();
                           // userMessge.setEncodePwd(encodePwd);
                            userMessge.setEncodePwd(params.getEncoderClientPassword());
                            userMessge.setEncodePort(encodePort);
                            userMessge.setEncodeId(encodeId);


                            ToolLog.i("==========sipInfo: sipId[" + params.getDecoderSIPID() + "] sipPwd[" + params.getClientPassword()
                                    + "] sipServerIp[" + params.getServerIP() + "] sipServerPort[" + params.getServerPort()
                                    + "] sipServerId[" + params.getServerID() + "]");
                            PLog.d("WelcomeActivity","--------启动aar包------初始化媒体---->结束时间：time="+ TimeUitls.getLongSysnTime());
                            PLog.d("WelcomeActivity", "----------onReceiveWssMessage------->初始化媒体成功");
                            PLog.d("WelcomeActivity", "----------onReceiveWssMessage------->dcodeId=" + userMessge.getDecodeId() + "\n dcodePwd=" + userMessge.getDecodePwd());
                            PLog.d("WelcomeActivity", "----------onReceiveWssMessage------->encodeId=" + userMessge.getEncodeId() + "\n encodePort=" + userMessge.getEncodePort() + "\n" +
                                    "encodePwd=" + userMessge.getEncodePwd());
                            //onWssSubscribeResource();
                            startRegister();
                        }
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    /////////////////////////////////////////登陆成功后进入主界面的入口/////
    //正式的
    private void finishLogin() {
        if (!isError){
            PLog.d("WelcomeActivity","--------启动aar包------sip服务注册---->结束时间：time="+ TimeUitls.getLongSysnTime());
            PLog.d("WelcomeActivity","--------启动aar包------开始转跳会议界面---->开始时间：time="+ TimeUitls.getLongSysnTime());
            PLog.d("WelcomeActivity", "----------finishLogin------->进入到跳转");
            ToolLog.i("finishLogin");
            //保存登陆状态
            onMakeUserDirctory();
            boolean creatMeeting = userMessge.isThirdPartyCreatMeeting();
            if (creatMeeting) {
                //创建者
                String groupID = userMessge.getThirdPartyGroupID();
                if (groupID.equals(XTContants.MEETING_GROUP)){
                    //群聊
                    Intent intent = new Intent(WelcomeActivity.this, GroupContactActivity.class);
                    intent.putExtra(Constants.ACTIVTY_TAG, Constants.ACTIVTY_THRID_APP);
                    startActivity(intent);
                    finish();
                }else if (groupID.equals(XTContants.MEETING_SINGLE)){
                    //单聊
                    Intent intent = new Intent(WelcomeActivity.this, VedioMettingActivity.class);
                    intent.putExtra(Constants.ACTIVTY_TAG, Constants.ACTIVTY_THRID_APP);
                    startActivity(intent);
                    finish();
                }

                PLog.d("WelcomeActivity", "----------finishLogin------->创建者");


            } else {
                //成员
                PLog.d("WelcomeActivity", "----------finishLogin------->成员");
                Intent intent = new Intent(WelcomeActivity.this, ThridCallingActivity.class);
                intent.putExtra(Constants.ACTIVTY_TAG, Constants.ACTIVTY_THRID_APP);
                startActivity(intent);
                finish();
            }
        }

    }

    private void onMakeUserDirctory() {
        try {
            File file = new File(ConstantsValues.UserVideoPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(ConstantsValues.UserPhotoPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(ConstantsValues.UserFilePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(ConstantsValues.TempFilePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 查询是否绑定设备
     *
     */
    public void onQueryBindDevice() {
        if (!isError){
            //参数
            String token=userMessge.getUserTokenkey();
            Map<String, String> map = new HashMap<String, String>();
            map.put("operatorToken", token);
            List<NameValuePair> params = MoudleParams.getQueryBindDevice(this, map);
            //请求
            if (instans != null) {
                PLog.d("WelcomeActivity", "----------onQueryBindDevice------->operatorToken=" + token);
                instans.sendRequest(params, true, Constants.HTTP_GET_QUERY_BIND_DEVICE);
            }
        }

    }


    /**
     * 绑定设备的网络请求
     *
     * @param deviceId
     */
    public void onBindDevice(String deviceId) {
        if (!isError){
            PLog.d("WelcomeActivity", "----------onBindDevice------->开始绑定设备");
            String cmd = null;
            try {
                JSONObject subObj = new JSONObject();
                subObj.put("userID", mUserId);
                subObj.put("callItem", "MANUAL");
                subObj.put("encoderItem", deviceId);
                subObj.put("decoderItem", "");
                subObj.put("meetingItem", "MANUAL");
                cmd = "operatorToken=" + mTokenKey + "&&obj=" + subObj.toString();
            } catch (Exception e) {
                cmd = null;
            }

            String path = ConstantsValues.getHttpUrl(this, NetUrl.
                    HTTP_BindDevice);

            ToolLog.i("===@@@===path: " + path);
            ToolLog.i("===@@@===cmd: " + cmd);

            BindDeviceCallback jsonCallback = new BindDeviceCallback();
            mAsyncTask = new HttpAsyncTask(this, path, cmd, jsonCallback, false, true);
            mAsyncTask.execute("");
        }
    }



    public class BindDeviceCallback implements HttpAsyncTask.HttpCallBack {

        @Override
        public void setResult(String result) {
            // TODO Auto-generated method stub
            if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
                mAsyncTask.cancel(true);
                mAsyncTask = null;
            }

            if (!result.equals("errorNet") && !result.contentEquals("errorData")) {
                try {
                    ToolLog.i("=========onBindDevice====" + result);

                    JSONObject jobj = new JSONObject(result);
                    String State = jobj.getString("responseCode");
                    if (State != null && !State.isEmpty() && State.equals("1")) {
                        //绑定设备成功,跳转页面
                        PLog.d("WelcomeActivity", "----------BindDeviceCallback------->绑定设备成功");
                        finishLogin();
                    } else {
                        PLog.d("WelcomeActivity", "----------BindDeviceCallback------->绑定设备失败");
                        showToast("绑定设备失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    PLog.d("WelcomeActivity", "----------BindDeviceCallback------->绑定设备失败");
                    showToast("绑定设备失败");
                }
            }
        }
    }

}
