package com.server;

import java.io.DataOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;



import com.common.MyMessage;

public class ControllerThread {
	int ju;
	String lastHuName=null;
	boolean pengGangFlag =false;
	boolean huFlag = false;
	String id;
	String[] players;
	ArrayList<Integer> pool;
	Iterator<Integer> it;
	public boolean getPengGangFlag(){
		return pengGangFlag;
	}
	public void setPengGangFlag(boolean a){
		 pengGangFlag = a;
	}
	public boolean getHuFlag(){
		return huFlag;
	}
	public void setHuFlag(boolean a){
		 huFlag = a;
	}
	public void poolReset(){
		pool.clear();
		initPool();
	}
	public void initPool(){
		for(int i =0; i<136;i++){
			pool.add(i);
		}
		Collections.shuffle(pool); 
	}
	
	public ControllerThread(String[] _players){
		lastHuName = _players[0];
		pengGangFlag =false;
		ju = 0;
		players = _players;
		id = players[0];
		pool = new ArrayList<Integer>();
		initPool();
	}
	public int getNum(String name){
		for(int i=0;i<4;i++){
			if(players[i].equals(name)){
				return i;
			}
		}
		return -1;
	}
	public void broadcastTurn(int i) throws IOException{
		ServerClientThread sct;
		DataOutputStream dout;
		MyMessage retm = new MyMessage();
		retm.setType(10); //10 decides turn to discard
		retm.setInt(i);
		for(int j=0;j<4;j++){
			sct = ServerThreadManager.getThread(players[j]);
			dout = sct.getDout();
			dout.write(retm.getBytes());
			dout.flush();
		}
	}
	public void broadcastNames() throws IOException{
		ServerClientThread sct;
		DataOutputStream dout;
		MyMessage retm = new MyMessage();
		retm.setType(11); 
		String temp = players[0]+"|"+players[1]+"|"+players[2]+"|"+players[3];
		retm.setString(temp);
		for(int j=0;j<4;j++){
			sct = ServerThreadManager.getThread(players[j]);
			dout = sct.getDout();
			dout.write(retm.getBytes());
			dout.flush();
		}
	}
	public void broadcastPlace(int i) throws IOException{//3 decides the place
		ServerClientThread sct;
		DataOutputStream dout;
		MyMessage retm = new MyMessage();
		retm.setType(3);
		retm.setIntToPos(i, 4);
		retm.setIntToPos(ju, 8);
		for(int j=0;j<4;j++){
			sct = ServerThreadManager.getThread(players[j]);
			dout = sct.getDout();
			dout.write(retm.getBytes());
			dout.flush();
		}
	}
	public void broadcastDiscard(String name, int mahjong) throws IOException{
		ServerClientThread sct;
		DataOutputStream dout;
		int sender = -1;
		for(int i=0;i<4;i++){
			if(players[i].equals(name)){
				sender = i;
				break;
			}
		}
		MyMessage retm = new MyMessage();
		retm.setType(5); //discard mahjong :5
		String temp = name+'|' + String.valueOf(mahjong);
		retm.setString(temp);
		for(int i=0;i<4;i++){
			if(i!=sender){
				sct = ServerThreadManager.getThread(players[i]);
				dout = sct.getDout();
				dout.write(retm.getBytes());
				dout.flush();
			}
		}
		
	}
	public void broadcastChi(String name,int choice) throws IOException {
		ServerClientThread sct;
		DataOutputStream dout;
		int sender = -1;
		for(int i=0;i<4;i++){
			if(players[i].equals(name)){
				sender = i;
				break;
			}
		}
		MyMessage retm = new MyMessage();
		retm.setType(6); //discard mahjong :6
		retm.setInt(choice);
		for(int i=0;i<4;i++){
			if(i!=sender){
				sct = ServerThreadManager.getThread(players[i]);
				dout = sct.getDout();
				dout.write(retm.getBytes());
				dout.flush();
			}
		}
		//set turn to the player
		broadcastTurn(sender);
	}
	public void broadcastPengGang(String name, int value) throws IOException{
		ServerClientThread sct;
		DataOutputStream dout;
		int sender = -1;
		for(int i=0;i<4;i++){
			if(players[i].equals(name)){
				sender = i;
				break;
			}
		}
		MyMessage retm = new MyMessage();
		retm.setType(7); //peng gang :7
		retm.setInt(value);
		for(int i=0;i<4;i++){
			if(i!=sender){
				sct = ServerThreadManager.getThread(players[i]);
				dout = sct.getDout();
				dout.write(retm.getBytes());
				dout.flush();
			}
		}
		//set turn to the player
		//broadcastTurn(sender);
	}
	public void broadcastHu(String name, String hand) throws IOException{
		
		ServerClientThread sct;
		DataOutputStream dout;
		int sender = -1;
		for(int i=0;i<4;i++){
			if(players[i].equals(name)){
				sender = i;
				break;
			}
		}
		MyMessage retm = new MyMessage();
		retm.setType(8); //hu 8
		retm.setString(hand);
		for(int i=0;i<4;i++){
			if(i!=sender){
				sct = ServerThreadManager.getThread(players[i]);
				dout = sct.getDout();
				dout.write(retm.getBytes());
				dout.flush();
			}
		}
	}
	public void broadcastFanpai() throws IOException{
		ServerClientThread sct;
		DataOutputStream dout;
		
		MyMessage retm = new MyMessage();
		retm.setType(8); // fanpai
		retm.setInt(pool.get(0));
		pool.remove(0);
		for(int i=0;i<4;i++){
			sct = ServerThreadManager.getThread(players[i]);
			dout = sct.getDout();
			dout.write(retm.getBytes());
			dout.flush();
		}
	}
	public void sendMahjongWithoutTurn(int i) throws IOException{
		ServerClientThread sct = ServerThreadManager.getThread(players[i]);
		DataOutputStream dout = sct.getDout();
		MyMessage retm = new MyMessage();
		retm.setType(4);
		retm.setInt(pool.get(0));
		pool.remove(0);
		dout.write(retm.getBytes());
		dout.flush();
	}
	
	public void sendMahjong(int i) throws IOException{
		ServerClientThread sct = ServerThreadManager.getThread(players[i]);
		DataOutputStream dout = sct.getDout();
		MyMessage retm = new MyMessage();
		retm.setType(4);
		retm.setInt(pool.get(0));
		pool.remove(0);
		dout.write(retm.getBytes());
		dout.flush();
		
		broadcastTurn(i);
	}
	public void broadcastHand(String name){
		if(!lastHuName.equals(name)){
			ju++;
		}
		lastHuName = name;
		poolReset();
		try {
			broadcastPlace(ju%4);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//deals 13 mahjongs to 4 players
		for(int i=0; i<4;i++){
			for(int j=0;j<14;j++){
				try {
					sendMahjongWithoutTurn(i);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//deals mahjong to first player
		try {
			sendMahjong(ju%4);//zhuang gets the first mahjong
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run(){
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
//	public static void main(String[] args) {
//		String temp = "123"+'|'+String.valueOf(345)+"|"+"heheh";
//		System.out.println(temp);
//		String [] res = temp.split("\\|");
//		for(int i=0;i<res.length;i++){
//			System.out.println(res[i]);
//		}
//		
//	}
}
