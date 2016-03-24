package com.server;
import java.net.*;
import java.io.*;
import java.util.*;

import com.common.*;
import com.db.SqlHelper;
import com.server.ServerClientThread;
import com.server.ServerThreadManager;

public class Server extends ServerSocket{
	private ServerSocket ss;
	String [] res;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Server myserver = new Server();
	}
	public Server() throws IOException {
		try {
			//监听3346端口
			System.out.println("服务器监听3346");
			ss = new ServerSocket(3346);
			while(true){
				Socket s = ss.accept();
				System.out.println("got a connection!");
				//get InputStream
				DataInputStream din = new DataInputStream(s.getInputStream());
				DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				
				byte[] temp = new byte[64];
				//get socket byte[] data, and create Message
				din.read(temp);
				MyMessage revm = new MyMessage(temp);
				int type = revm.getType();
				
				MyMessage retm = new MyMessage();
				
				if(0==type){ // login request msg[] contains name and password 
					System.out.println("收到客户端"+res[0]+"type=0的登陆请求");
					String tempString = revm.getString();
					res = tempString.split("\\|");
					if(ServerThreadManager.isOnline(res[0])){
						retm.setType(0); // 0 represent login result
						retm.setInt(0);
						dout.write(retm.getBytes());
						dout.flush();
						continue;
					}
					if(SqlHelper.checkLogin(res[0], res[1])){
						retm.setType(0); // 0 represent login result
						retm.setInt(1);  //send the confirmation
						dout.write(retm.getBytes());
						dout.flush();
						// login successful, start serverclientThread and store it in hashmap
						ServerClientThread sct = new ServerClientThread(s,res[0]);
						//ServerThreadManager.addThread(res[0], sct);
						sct.start();
					} else {
						retm.setType(0); // 0 represent login result
						retm.setInt(0);
						dout.write(retm.getBytes());
						dout.flush();
						continue;
					}
				 } else if (1 == type) {
					System.out.println("MyServer收到客户端的注册请求");
					retm.setType(1); // 0 represent login result
					
					if(SqlHelper.register(res[0], res[1])) {
						retm.setInt(1);
					} else {
						retm.setInt(0);
					}
					dout.write(retm.getBytes());
					dout.flush();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(!ss.isClosed()) {
				try {
					ss.close();
					ss = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}
	}
}
