package model;

import bitzero.server.entities.User;

import bitzero.util.ExtensionUtility;

import java.awt.print.Book;

import java.util.HashMap;
import java.util.Map;

import java.util.PriorityQueue;
import java.util.Queue;

import util.database.DataModel;

public class ListGuild extends DataModel{
    public int numberOfGuild = 0;
    public Map <Integer, String> list_guild = new HashMap<Integer, String>();
    
    public void addGuild(String name){
        numberOfGuild++;
        this.list_guild.put(numberOfGuild, name);
    }
    public Queue<Guild> searchNameGuild(String string){
        // Init PriorityQueue
        Queue<Guild> queue = new PriorityQueue<Guild>(50);
        for(Map.Entry<Integer, String> guild : this.list_guild.entrySet()) {
            Integer guild_id = guild.getKey();
            String guild_name = guild.getValue();
            if (checkSubString(string,guild_name)){
                try {
                    Guild suggest_guild = (Guild) Guild.getModel(guild_id, Guild.class);
                    boolean offer = queue.offer(suggest_guild);
                    if (!offer){
                        return queue;
                    }
                } catch (Exception e) {
                }
            }
        }
        return queue;
    }

    private boolean checkSubString(String string, String guild_name) {
        if (guild_name != null && guild_name.toLowerCase().contains(string.toLowerCase())){
            return true;
        }
        return false;
    }
}

