package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;


import java.awt.Point;

import java.nio.ByteBuffer;

import model.Building;
import model.MapInfo;
import cmd.obj.map.Obs;

public class ResponseRequestMapInfo extends BaseMsg {
    MapInfo mapInfo;
    public ResponseRequestMapInfo(MapInfo _mapInfo) {
        super(CmdDefine.GET_MAP_INFO);
        this.mapInfo = _mapInfo;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        int size = mapInfo.listBuilding.size();
        bf.putInt(size);
        for (Building building : mapInfo.listBuilding) {
            bf.putInt(building.id);
            putStr(bf, building.type);
            System.out.print("type = "+ building.type);
            bf.putInt(building.posX);
            bf.putInt(building.posY);
            bf.putInt(building.level);
            System.out.print("level = "+ building.level);
            //bf.putInt(building.timebuild);
            putStr(bf, building.status);
            bf.putLong(building.timeStart);
            
            
        }
        //Obs
        size = mapInfo.listObs.size();
        System.out.println("size" +size);
        bf.putInt(size);
        for (Obs obs : mapInfo.listObs) {
            bf.putInt(obs.id+5000);
            putStr(bf, obs.type);
            bf.putInt(obs.posX);
            bf.putInt(obs.posY);  
//            System.out.println("obs.id="+obs.id);
//            System.out.println("obs.type="+obs.type);
//            System.out.println("obs.posX="+obs.posX);
//            System.out.println("obs.posY="+obs.posY);
            
        }
        
        return packBuffer(bf);
    }
}
