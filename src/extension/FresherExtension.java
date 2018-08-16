package extension;


import bitzero.engine.sessions.ISession;

import bitzero.server.config.ConfigHandle;
import bitzero.server.core.BZEventType;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.ConnectionStats;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import bitzero.util.datacontroller.business.DataController;
import bitzero.util.socialcontroller.bean.UserInfo;

import bitzero.util.socialcontroller.exceptions.SocialControllerException;

import cmd.receive.authen.RequestLogin;

import eventhandler.LoginSuccessHandler;
import eventhandler.LogoutHandler;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.json.JSONObject;

import service.MapInfoHandler;
import service.TrainTroopHandle;
import service.TroopHandle;
import service.UserHandler;

import util.GuestLogin;

import util.metric.LogObject;
import util.metric.MetricLog;

import service.HarvestHandle;
import service.InteractiveGuildHandler;

import util.server.ServerConstant;
import util.server.ServerLoop;


public class FresherExtension extends BZExtension {
    private static String SERVERS_INFO =
        ConfigHandle.instance().get("servers_key") == null ? "servers" : ConfigHandle.instance().get("servers_key");

    private ServerLoop svrLoop;

    public FresherExtension() {
        super();
        setName("Fresher");
        svrLoop = new ServerLoop();
        ServerConstant.readConfig();
    }

    public void init() {
        trace("  Register Handler ");
        addRequestHandler(UserHandler.USER_MULTI_IDS, UserHandler.class);
        //addRequestHandler(DemoHandler.DEMO_MULTI_IDS, DemoHandler.class);
        addRequestHandler(MapInfoHandler.MAPINFO_MULTI_IDS, MapInfoHandler.class);
        addRequestHandler(TroopHandle.TROOP_MULTI_IDS, TroopHandle.class);
        
        addRequestHandler(TrainTroopHandle.TRAINTROOP_MULTI_IDS, TrainTroopHandle.class);
        addRequestHandler(InteractiveGuildHandler.INTERACTIVE_MULTI_IDS, InteractiveGuildHandler.class);

        trace(" Event Handler ");
        addEventHandler(BZEventType.USER_LOGIN, LoginSuccessHandler.class);
        addEventHandler(BZEventType.USER_LOGOUT, LogoutHandler.class);
        addEventHandler(BZEventType.USER_DISCONNECT, LogoutHandler.class);
    }

    public ServerLoop getServerLoop() {
        return svrLoop;
    }

    @Override
    public void monitor() {
        try {
            ConnectionStats connStats = bz.getStatsManager().getUserStats();
            JSONObject data = new JSONObject();

            data.put("totalInPacket", bz.getStatsManager().getTotalInPackets());
            data.put("totalOutPacket", bz.getStatsManager().getTotalOutPackets());
            data.put("totalInBytes", bz.getStatsManager().getTotalInBytes());
            data.put("totalOutBytes", bz.getStatsManager().getTotalOutBytes());

            data.put("connectionCount", connStats.getSocketCount());
            data.put("totalUserCount", bz.getUserManager().getUserCount());

            DataController.getController().setCache(SERVERS_INFO, 60 * 5, data.toString());
        } catch (Exception e) {
            trace("Ex monitor");
        }
    }

    @Override
    public void destroy() {
        List<User> allUser = ExtensionUtility.globalUserManager.getAllUsers();
        if (allUser.size() == 0)
            return;

        User obj = null;

        for (int i = 0; i < allUser.size(); i++) {
            obj = allUser.get(i);
            // do sth with user
            LogObject logObject = new LogObject(LogObject.ACTION_LOGOUT);
            logObject.zingId = obj.getId();
            logObject.zingName = obj.getName();
            //System.out.println("Log logout = " + logObject.getLogMessage());
            MetricLog.writeActionLog(logObject);
        }
    }


    public void doLogin(short cmdId, ISession session, DataCmd objData) {
        RequestLogin reqGet = new RequestLogin(objData);
        System.out.println("cmdId "+ cmdId);
        reqGet.unpackData();
       
        try {
            
            UserInfo uInfo = getUserInfo(reqGet.sessionKey, reqGet.userId, session.getAddress());
            User u = ExtensionUtility.instance().canLogin(uInfo, "", session);
            if (u!=null)
                System.out.println("userId "+ uInfo.getUserId());
                u.setProperty("userId", uInfo.getUserId());            
        } catch (Exception e) {
            Debug.warn("DO LOGIN EXCEPTION " + e.getMessage());
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }

    }

    private UserInfo getUserInfo(String username, int userId, String ipAddress) throws Exception {
        System.out.println("getUserInfo");
        int customLogin = ServerConstant.CUSTOM_LOGIN;
        switch(customLogin){
            case 1: // login zingme
            System.out.println("CASE 1");
                return ExtensionUtility.getUserInfoFormPortal(username);
            case 2: // set direct userid
            System.out.println("CASE 2");
            System.out.println("userId "+ userId);
            
                return GuestLogin.setInfo(userId, "Fresher_" + userId);
            default: // auto increment
                System.out.println("guest login");
                return GuestLogin.newGuest();
                
        }        
    }

}
