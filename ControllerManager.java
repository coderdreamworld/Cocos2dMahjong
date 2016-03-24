package com.server;

import java.util.HashMap;

public class ControllerManager {
	public static HashMap<String,ControllerThread> threadPool = new HashMap<String, ControllerThread>();
	
	public static void addThread(String userId, ControllerThread sct) {
		threadPool.put(userId, sct);
	}
	public static ControllerThread getThread(String userId) {
		return (ControllerThread)threadPool.get(userId);
	}
	public static void remove(String name) {
		threadPool.remove(name);
	}
}