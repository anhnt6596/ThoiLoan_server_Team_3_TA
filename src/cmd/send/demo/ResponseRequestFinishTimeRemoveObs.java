package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseRequestFinishTimeRemoveObs extends BaseMsg{
    short validate ;
  
    public ResponseRequestFinishTimeRemoveObs(short validate) {
        super(CmdDefine.FINISH_TIME_REMOVE_OBS);
        this.validate = validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        return packBuffer(bf);
    }
}
