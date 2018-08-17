package model;

import util.database.DataModel;

public class TroopGuild extends DataModel {
    String name;
    short level;
    
    public TroopGuild(String _name, short _level) {
        super();
        name = _name;
        level = _level;
    }
}
