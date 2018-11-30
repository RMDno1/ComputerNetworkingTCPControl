package com.ouc.tcp.test;

import java.util.TimerTask;

import com.ouc.tcp.client.Client;
import com.ouc.tcp.message.TCP_PACKET;

public class Window extends TimerTask {
	
	private Client senderClient;
	private TCP_PACKET[] packets;
	private int windowSize = 14;
	private int begin = 0, end = 0;
	
	/*���캯��*/
	public Window(Client client, TCP_PACKET packet) {
		super();
		senderClient = client;
		packets[end++] = packet;
		
	}		
	@Override
	/*�ش�TCP���ݱ�*/
	public void run() {
		
		for (int i=0;i<packets.length;i++)
			senderClient.send(packets[i]);		
	}
}
