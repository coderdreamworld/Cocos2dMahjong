package com.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

import com.common.MyMessage;


public class ServerClientThread extends Thread{
	private String controllerId;
	public String getControllerId() {
		return controllerId;
	}

	public void setControllerId(String _controllerId) {
		controllerId = _controllerId;
	}
	private int data;
	private Socket sc;
	private String username;
	private DataInputStream din;		
	private DataOutputStream dout;
	boolean pengGangFlag = false;
	public ServerClientThread(Socket s,String username) throws IOException{
		controllerId = null;
		this.username = username;
		this.sc = s;
		din = new DataInputStream(sc.getInputStream());
		dout = new DataOutputStream(sc.getOutputStream());
	}
	
	public Socket getSc() {
		return sc;
	}
	public void setSc(Socket sc) {
		this.sc = sc;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public DataInputStream getDin() {
		return din;
	}
	public void setDin(DataInputStream din) {
		this.din = din;
	}
	public DataOutputStream getDout() {
		return dout;
	}
	public void setDout(DataOutputStream dout) {
		this.dout = dout;
	}
	public void run() {
		try{
			while(true){
				byte[] temp = new byte[64];
				din.read(temp);
				MyMessage revm = new MyMessage(temp);
				int type = revm.getType();
				
				if(3==type){ // matchmaking request
					ServerThreadManager.addThread(username, this);
					ServerThreadManager.makeMatch();
				} else if (5 == type){
					data = revm.getInt();// get the mahjong (int)
					if(this.getControllerId().equals(null)){
						System.out.println("no controllerId!");
						continue;
					}
					ControllerManager.getThread(this.getControllerId()).broadcastDiscard(username, data);
				} else if (6 == type){//-1,0,1for 3 kinds of chi; type(int)  choice(int)
					data = revm.getInt();
					if(this.getControllerId().equals(null)){
						System.out.println("no controllerId!");
						continue;
					}
					sleep(3000);
					ControllerThread ct = ControllerManager.getThread(controllerId);
					if(!ct.getPengGangFlag() && !ct.getHuFlag()){
						ct.broadcastChi(username,data);
					}
				} else if(7 == type){// 1 for peng, 2 for gang, 3 for self-gang
					data = revm.getInt();
					if(controllerId.equals(null)){
						System.out.println("no controllerId!");
						continue;
					}
					ControllerThread ct = ControllerManager.getThread(controllerId);
					sleep(3000);
					int num = ct.getNum(username);
					if(!ct.getHuFlag()){
						ct.broadcastPengGang(username, data);
					}
					if(data == 3|| data == 2){
						System.out.println("gang");
						ct.sendMahjong(num);
					} else if(data == 1){
						System.out.println("beng");
						ct.broadcastTurn(num);
					}
					
				} else if(8 == type){// hupai
					System.out.println("received type=8, hupai");
					String temps = revm.getString();
					if(controllerId.equals(null)){
						System.out.println("no controllerId!");
						continue;
					}
					ControllerThread ct = ControllerManager.getThread(controllerId);
					ct.setHuFlag(true);
					ct.broadcastHu(username, temps);
				} else if(9==type){
					System.out.println("received type=9,xipai");
					if(controllerId.equals(null)){
						System.out.println("no controllerId!");
						continue;
					}
					ControllerThread ct = ControllerManager.getThread(controllerId);
					ct.broadcastHand(username);
				}
			}
		} catch( Exception e) {
				e.printStackTrace();
		} finally {
				ServerThreadManager.remove(username);
		}
	}
}

