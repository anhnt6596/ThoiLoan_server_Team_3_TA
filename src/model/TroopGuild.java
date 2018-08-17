package model;

import util.database.DataModel;

public class TroopGuild extends DataModel {
    public String name;
    public short level;
    
    public TroopGuild(String _name, short _level) {
        super();
        name = _name;
        level = _level;
    }
}
