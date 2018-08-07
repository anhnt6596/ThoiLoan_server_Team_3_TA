package cmd;


public class CmdDefine {
    public static final short CUSTOM_LOGIN = 1;
    public static final short MANUAL_DISCONNECT = 2500;

    public static final short ERROR_CMD_ID = 999;

    public static final short USER_MULTI_IDS = 1000;
    public static final short AUTHEN_MULTI_IDS = 2000;
    public static final short FRIEND_MULTI_IDS = 3000;
    public static final short ADMIN_MULTI_IDS = 9000;


    public static final short GET_USER_INFO = 1001;
   
    //Log cmd
    public static final short GET_MAP_INFO = 2001;
    public static final short MOVE_CONSTRUCTION = 2002;
    public static final short ADD_CONSTRUCTION = 2003;
    public static final short UPGRADE_CONSTRUCTION = 2004;
    public static final short CANCLE_CONSTRUCTION = 2005;
    public static final short REMOVE_OBS = 2006;
    
    
    public static final short GET_SERVER_TIME = 2100;
    public static final short FINISH_TIME_CONSTRUCTION = 2101;
    public static final short QUICK_FINISH = 2102;

    public static final short ADD_RESOURCE = 1500;
    
    
    public static final short GET_FRIENDS = 3001;
    public static final short FRIEND_SEND_MESSAGE = 3002;
    public static final short MESSAGE_GET_BOX = 3003;
    public static final short MESSAGE_DELETE = 3004;
    public static final short MESSAGE_MARK_AS_READ = 3005;
    public static final short FRIEND_GET_PLAYING = 3006;
    public static final short FRIEND_GET_FROM_SOCIAL_NETWORK = 3007;
    public static final short FRIEND_ADD = 3008;
    public static final short FRIEND_REMOVE = 3009;
    public static final short FRIEND_GET_ONLINE = 3010;
    public static final short FRIEND_GET_FROM_DB = 3011;
    public static final short FRIEND_GET_SUGGESTIONS = 3012;
    public static final short FRIEND_ACCEPT_INVITATION = 3013;
    public static final short FRIEND_GET_PORTAL = 3014;
    public static final short FRIEND_ON_OFF = 3015;

    public static final short RELOAD_CONFIG = 9101;
    public static final short FRIEND_SEARCH_BY_NAME = 3016;
    public static final short FRIEND_SEND_INVITATION = 3017;
    public static final short FRIEND_GET_INVITATIONS = 3018;
    public static final short FRIEND_RESPONSE_INVITATION = 3019;
    public static final short FRIEND_SET_STAR = 3020;

    public static final short GET_TROOP_INFO = 4001;
    public static final short RESEARCH_TROOP = 4002;
    public static final short RESEARCH_TROOP_COMPLETE = 4003;
    
    public static final short GET_BARRACK_QUEUE_INFO = 7001;
    public static final short TRAIN_TROOP = 7002;
    public static final short CANCEL_TRAIN_TROOP = 7003;
    public static final short QUICK_FINISH_TRAIN_TROOP = 7004;
    public static final short FINISH_TIME_TRAIN_TROOP = 7005;

}
