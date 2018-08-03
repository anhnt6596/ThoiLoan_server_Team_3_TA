package cmd.send.demo;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import cmd.CmdDefine;

import java.nio.ByteBuffer;




public class ResponseRequestRemoveObs extends BaseMsg{
    short validate ;
  
    public ResponseRequestRemoveObs(short validate) {
        super(CmdDefine.REMOVE_OBS);
        this.validate = validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        return packBuffer(bf);
    }
}

