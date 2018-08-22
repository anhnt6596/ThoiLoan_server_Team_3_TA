package cmd.receive.map;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import model.Building;
import model.Wall;


public class RequestMoveMultiWall extends BaseCmd{
    public int length_wall;
    public List<Wall> listWall = new ArrayList<Wall>();
    
    public RequestMoveMultiWall(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }
    
    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            this.length_wall = readInt(bf);            
            for(int i=0;i<this.length_wall;i++){                
                listWall.add(new Wall(readInt(bf),readInt(bf),readInt(bf)));
            }
            
        }catch (Exception e) {
            CommonHandle.writeErrLog(e);}
    }
}

