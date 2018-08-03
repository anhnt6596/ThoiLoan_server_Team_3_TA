package util;

import bitzero.server.config.ConfigHandle;

public class Constant {
    public static final String SEPERATE = "__";
    public static final int CACHE_EXP_TIME = 259200;

    public static final String WEB_SERVICE_IP = ConfigHandle.instance().get("ws_ip");
    public static final int WEB_SERVICE_PORT = ConfigHandle.instance().getLong("ws_port").intValue();
    public static final boolean ADMIN_ENABLE = (ConfigHandle.instance().get("is_admin_enable").equals("1"));

    public static final String USER_INFO = "user_info";
    public static final String MESSAGE_STATE_READ = "Read";
    public static final String MESSAGE_STATE_UNREAD = "Unread";
    public static final String MESSAGE_TYPE_INVITATION = "Invitation";
    public static final String MESSAGE_TYPE_FRIEND_MESSAGE = "FriendMessage";
    public static final String DEFAULT_AVATAR_URL = "http://zingplay.static.g6.zing.vn/images/zpp/zpdefault.png";


    public static final int PORTAL_DATA_VERSION = ConfigHandle.instance().getLong("data_version").intValue();
    public static final int MESSAGE_TYPE_TEXT = 0;
    public static final int MESSAGE_TYPE_INVITE_GAME = 1;
    public static final int MESSAGE_TYPE_SEND_GIFT = 2;
    public static final int MESSAGE_TYPE_REQUEST_GIFT = 3;


    public static final int JETTY_PORT = ConfigHandle.instance().getLong("jetty_port").intValue();

    public static final String DISTRIBUTION_DEFAULT = "default";

}
