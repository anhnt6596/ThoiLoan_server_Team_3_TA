package util;


public class FarmConstant {
    //dont change this value
    public static final String SECRET_SIGN_KEY = "nghiecnghiecnghiec";
    //TODO move some of these constant to farm.config
    public static final String GOLD = "gold";
    public static final String EXP = "exp";
    public static final String DIAMOND = "diamond";
    public static final String STONE = "stone";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

    //use in LevelConfig.csv
    public static final String NEIGHBOR_ORDER_TEXT = "neighborOrder";
    public static final String NPC_ORDER_TEXT = "NPCOrder";

    public static final String BOAT_POINT = "bp";
    public static final String UNDERLINE_SEP = "_";

    //Map object type
    public static final byte MO_TYPE_ANIMAL = 1; //animals
    public static final byte MO_TYPE_FIELD = 2; //fields
    public static final byte MO_TYPE_COOP = 3; //coop, stable, dog house...
    public static final byte MO_TYPE_MACHINE = 5; //popcorn, food machine..
    public static final byte MO_TYPE_DECO = 6; //fence, flowers..
    public static final byte MO_TYPE_TREE_FRUIT = 8; //cherry, apple..
    public static final byte MO_TYPE_SILO = 9;
    public static final byte MO_TYPE_WAREHOUSE = 10;
    public static final byte MO_TYPE_MINE = 11;
    public static final byte MO_TYPE_PIER = 12;

    //max elements in autoId array
    public static final byte MAX_CMD_ARR_SIZE_FIELD = 100;
    public static final byte MAX_CMD_ARR_SIZE_ANIMAL = 50;
    public static final byte MAX_CMD_ARR_SIZE_FRUIT = 100;

    //coop constants
    public static final byte ANIMAL_NUM_OUTPUT = 1;
    public static final byte FOOD_USE_PER_FEED = 1;

    //order constants
    public static final byte NPC_ORDER = 1;
    public static final byte NEIGHBOR_ORDER = 2;
    public static final byte SHOP_NPC = 4;
    public static final byte BOAT_ORDER = 3;
    public static final byte MAX_NEIGHBOR_ORDER = 9;
    public static final byte MAX_NPC_ORDER = 5;
    public static final byte INIT_NEIGHBOR_ORDER = 4;
    public static final byte INIT_NPC_ORDER = 3;
    public static final int TIME_WAIT_NPC_CANCEL = 60 * 5;
    public static final int TIME_WAIT_NPC_DELIVER = 60 * 5;
    public static final int TIME_WAIT_NEIGHBOR_CANCEL = 60 * 5;
    public static final int TIME_WAIT_NEIGHBOR_DELIVER = 0;

    public static final byte BOAT_STATUS_NON_ACTIVE = 0; //chua sua ben tau
    public static final byte BOAT_STATUS_WAIT_NEW_TURN = 1; //trang thai dang cho tau den
    public static final byte BOAT_STATUS_START_MAKE_CRATE = 2; //trang thai dang cho dong hang, chua lam gi ca
    public static final byte BOAT_STATUS_DURING_MAKE_CRATE =
        3; //trang thai dang cho dong hang, da nho ban hoac dong >=1 hang
    public static final int BOAT_TIME_WAIT_NEW_TURN = 4 * 3600;
    public static final int BOAT_TIME_WAIT_COMPLETE = 16 * 3600;

    //storage constant
    public static final byte SILO = 1;
    public static final byte WAREHOUSE = 2;
    public static final byte MATERIAL_NUM_DROP = 1;

    //machine constant
    public static final byte MACHINE_INITIAL_QUEUE_SIZE = 1;
    public static final byte PRODUCT_NUM_OUTPUT = 1;

    //roadsideshop constant
    public static final short RUBY_REMOVE_SELL = 1;
    public static final short RUBY_BUY_AD = 1;
    public static final short RUBY_EXPAND_SELL_SLOT = 5;
    public static final short MAX_SELL_SLOT = 50;
    //fruit tree
    public static final byte ITEM_NUM_TO_CUT = 1;

    //drop material constant
    public static final byte MAX_FIX_DROP = 20;

    public static final int RUBY_TO_RENEW_ORDER = 3;
    public static final int EXP_FINISH_BUIDLING = 3;
    public static final int TIME_WAIT_FREE_AD = 60 * 30;
    public static final int MAX_MACHINE_QUEUE = 9;

    public static final int NPC_ID_SASHA = 2;
    public static final int NPC_ID_GENIE = 1;

    public static final int TIME_RENEW_NPC_SHOP = 60;
}
