package util;


public class ErrorDefine {

    public static final byte SUCCESS = 1;
    public static final byte ACTION_FAILED = 2;
    public static final byte NO_DATA = 3;
    public static final byte NOT_ENOUGH_MONEY = 4;
    public static final byte CANT_PLACE_ON_MAP = 5;
    public static final byte NOT_ENOUGH_ITEM = 6;

    //machine
    public static final byte MACHINE_QUEUE_FULL = 7;
    public static final byte PRODUCT_NOT_DONE = 8;

    //field
    public static final byte CANT_HARVEST = 9;

    //storage
    public static final byte STORAGE_FULL = 10;

    //coop
    public static final byte NOT_ENOUGH_ANIMAL_FOOD = 11;

    //shop
    public static final byte MAX_PRICE_EXCEED = 12;
    public static final byte CANT_PLACE_SELLING = 13;
    public static final byte NO_ITEM_AT_SPECIFIED_SLOT = 14;

    //boat order
    public static final byte BOAT_WRONG_STATUS = 15;

    //order
    public static final byte ORDER_CANT_RENEW = 16;

    //fruit tree
    public static final byte DONT_NEED_HELP = 17;
    public static final byte CANT_CUT = 18;
    public static final byte NOT_ENOUGH_ITEM_TO_CUT = 19;

    public static final byte MAX_BUILD_EXCEED = 20;
    public static final byte NOT_ENOUGH_LEVEL = 21;

    //cmd array size
    public static final byte CMD_ARR_TOO_BIG = 22;
    public static final byte ARGUMENT_MISMATCH = 23;

    //
    public static final byte CANT_FIND_OBJECT_INDEX = 24;
    public static final byte MACHINE_ALREADY_FINISH_BUILT = 25;
    public static final byte COOP_IS_FULL = 26;
    public static final byte NO_CONFIG = 27;
    public static final byte NO_PRODUCT_IN_QUEUE = 28;
    public static final byte MACHINE_NOT_FINISH_BUILT = 29;
    public static final byte CANT_CANCEL_ORDER = 30;

    //ZingMe
    public static final byte SESSION_EXPIRED = 31;

    public static final byte NOT_ENOUGH_RUBY = 32;
    public static final byte NOT_HAVE_TO_USE_RUBY = 33;
    public static final byte FIELD_NOT_HAS_CROP = 34;
    public static final byte ANIMAL_NOT_FEED = 35;
    public static final byte ALL_PRODUCT_IS_DONE = 36;
    public static final byte MAX_MACHINE_QUEUE_EXCEED = 37;
    public static final byte INVALID_USER_SIGN = 38;
    public static final byte SOMEONE_BOUGHT_THIS_ITEM = 39;
    public static final byte CHECK_AND_SET_FAILED = 40;
    public static final byte MAX_STORAGE_LEVEL = 41;

    // authen
    public static final byte SESSIONKEY_INVALID = 51;
}
