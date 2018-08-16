package model;

import util.database.DataModel;

public class MessageGuild extends DataModel {
    public short type;
    public int id_user;
    public String content;                  //Toi da 50 ky tu
    public long timeStamp;
    
    public MessageGuild(short _type, int _id_user, String _content, long _timeStamp) {
        super();
        type = _type;
        id_user = _id_user;
        content = _content;
        timeStamp = _timeStamp;
    }
}
