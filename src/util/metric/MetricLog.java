package util.metric;


import bitzero.server.entities.User;

import cmd.receive.log.RequestLog;

import model.UserGame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.server.ServerUtil;


public class MetricLog {
    private static Logger logger = LoggerFactory.getLogger("MetricLog");
    public static final String LOG_SEP = "\t";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final byte LOG_IN = 1;
    public static final byte LOG_OUT = 2;
    public static final int ACT_SPEND_BUY_ITEM = -1;
    public static final int ACT_RECEIVE_MONEY = 1;
    public static final byte MONEY_GOLD = 1;
    public static final byte MONEY_XU = 2;

    public MetricLog() {
        super();
    }

    public static void write(String data) {
        long time = System.currentTimeMillis();
        StringBuilder s = new StringBuilder();
        s.append(time);
        s.append(LOG_SEP);
        s.append("moniter");
        s.append(LOG_SEP);
        s.append(data);

        //LogController.GetController().writeLog(ILogController.LogMode.ACTION, s.toString());
    }

    public static void writeCCULog(int ccu) {
        long time = System.currentTimeMillis();
        StringBuilder s = new StringBuilder();

        ccu += 100;

        s.append(time);
        s.append(LOG_SEP);
        s.append(LogDefine.CCU);
        s.append(LOG_SEP);
        s.append(ccu);

        //LogController.GetController().writeLog(ILogController.LogMode.ACTION, s.toString());
    }

    public static void writeActionLog(LogObject logObject) {
        //LogController.GetController().writeLog(ILogController.LogMode.ACTION, logObject.getLogMessage());
    }

    public static void handleClientLog(User user, RequestLog requestLogClient) {
        // TODO Auto-generated method stub
        System.out.println("start log client");
        switch (requestLogClient.actionId) {
        case LogObject.ACTION_LOGIN:
            user.setProperty("deviceId", requestLogClient.deviceId);
            user.setProperty("networkType", requestLogClient.networkType);
            user.setProperty("signalStrength", requestLogClient.signalStrength);
            user.setProperty("location", requestLogClient.location);
            user.setProperty("deviceType", requestLogClient.deviceType);
            user.setProperty("osVersion", requestLogClient.osVersion);
            break;
        case LogObject.ACTION_CHANGE_ACCOUNT:
        case LogObject.ACTION_GAME_INSTALL:
        case LogObject.ACTION_GAME_UPGRADE:
        case LogObject.ACTION_GAME_UNINSTALL:
        case LogObject.ACTION_GAME_JOIN:
            System.out.println("start log step 1");
            if (user.getProperty("deviceId") != null)
                requestLogClient.deviceId = (String) user.getProperty("deviceId");
            if (user.getProperty("networkType") != null)
                requestLogClient.networkType = (Integer) user.getProperty("networkType");
            if (user.getProperty("signalStrength") != null)
                requestLogClient.signalStrength = (Integer) user.getProperty("signalStrength");
            if (user.getProperty("location") != null)
                requestLogClient.location = (String) user.getProperty("location");
            if (user.getProperty("deviceType") != null)
                requestLogClient.deviceType = (String) user.getProperty("deviceType");
            if (user.getProperty("osVersion") != null)
                requestLogClient.osVersion = (String) user.getProperty("osVersion");

            //Luu data nhung game user da choi
            if (requestLogClient.actionId == LogObject.ACTION_GAME_JOIN) {
                UserGame userGame = null;
                System.out.println("start log step 2");
                try {
                    userGame =
                        (UserGame) UserGame.getModel(Integer.parseInt((String) user.getProperty("zingId")),
                                                     UserGame.class);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                if (userGame == null) {
                    userGame = new UserGame();
                }
                userGame.listPlayedGame.put(Long.parseLong(requestLogClient.itemId), System.currentTimeMillis());
                userGame.lastPlayedGame = Long.parseLong(requestLogClient.itemId);

                System.out.println("start log step 3");
                try {
                    userGame.saveModel(Integer.parseInt((String) user.getProperty("zingId")));
                    System.out.println("start log step 4");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        default:
            return;
        }
        requestLogClient.zingId = Integer.parseInt((String) user.getProperty("zingId"));
        requestLogClient.zingName = (String) user.getProperty("zingName");
        requestLogClient.accountType = (Integer) user.getProperty("accountType");
        requestLogClient.openAccount = (String) user.getProperty("openAccount");

        LogObject logObj = new LogObject(requestLogClient.actionId);
        ServerUtil.setData(requestLogClient, logObj);
        //        System.out.println(logObj.getLogMessage());
        MetricLog.writeActionLog(logObj);
    }
}
