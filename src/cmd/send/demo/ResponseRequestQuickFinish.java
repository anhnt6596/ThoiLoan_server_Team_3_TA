package cmd.send.demo;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.common.business.CommonHandle;

import cmd.CmdDefine;

import java.nio.ByteBuffer;




public class ResponseRequestQuickFinish extends BaseMsg{
    short validate ;
  
    public ResponseRequestQuickFinish(short validate) {
        super(CmdDefine.QUICK_FINISH);
        this.validate = validate;
    }
    
    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        
        bf.putShort(this.validate);
        System.out.println("ResponseRequestQuickFinish"+ this.validate);
        return packBuffer(bf);
    }
}

