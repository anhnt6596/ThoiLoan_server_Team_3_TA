package cmd.send.admin;

import bitzero.server.extensions.data.BaseMsg;

import cmd.CmdDefine;

public class ResponseReloadConfig extends BaseMsg {
    public String response;

    public ResponseReloadConfig() {
        super(CmdDefine.RELOAD_CONFIG);
    }
}
