package com.server;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ServerThreadManager {
	static ServerClientThread sct;
	public static HashMap<String,ServerClientThread> threadPool = new HashMap<String, ServerClientThread>();
	//向hashmap hm添加通讯线程
	public static void addThread(String userId, ServerClientThread sct) {
		threadPool.put(userId, sct);
	}
	//获取通讯线程
	public static ServerClientThread getThread(String userId) {
		return (ServerClientThread)threadPool.get(userId);
	}
	
	public static String makeMatch() throws IOException{
		if(threadPool.size()<=4){
			return null;
		}
		String[] players = new String[4];
		Iterator<Entry<String, ServerClientThread>> it = threadPool.entrySet().iterator();
		int i=0;
		String firstName="";
		String name;
		while(it.hasNext()){
			
			Map.Entry<String, ServerClientThread> entry = (Map.Entry<String, ServerClientThread>)it.next();
			if(0==i){
				firstName = entry.getKey();
			} 
			name  = entry.getKey();
			sct = entry.getValue();
			sct.setControllerId(firstName);// set firstName as controllerId for 4 players
			players[i] = name;
			i++;
			if(3<=i) {
				break;
			}
		}
		//match made, then create ControllerThread and put it in Manager's container
		ControllerThread ct = new ControllerThread(players);
		ControllerManager.addThread(firstName, ct);
		ct.broadcastNames();//broadcast the player names to all players
		return firstName;
	}
	//返回某用户是否在线
	public static boolean isOnline(String username) {
		if(threadPool.containsKey(username)) {
			return true;
		} else {
			return false;
		}
	}
	public static void remove(String name) {
		threadPool.remove(name);
	}
	
}
