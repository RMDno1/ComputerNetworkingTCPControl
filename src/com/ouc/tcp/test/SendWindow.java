package com.ouc.tcp.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.TimerTask;

import com.ouc.tcp.client.Client;
import com.ouc.tcp.client.UDT_RetransTask;
import com.ouc.tcp.client.UDT_Timer;
import com.ouc.tcp.message.TCP_PACKET;

public class SendWindow extends Window {
	private UDT_Timer[] timers = new UDT_Timer[windowSize];
	
	public class TaskPacketsRetrans extends TimerTask {
		/*���캯��*/
		public TaskPacketsRetrans() {
			super();
		}	
		@Override
		/*�ش�TCP���ݱ�*/
		public void run() {
			for (int i=begin;i< now;i++) {
				int index = i%windowSize;
				if(packets[index]!=null) {
					try {
						timers[index].cancel();
//						timers[index] = new UDT_Timer();
//						TaskPacketsRetrans reTrans = new TaskPacketsRetrans();
//						timers[index].schedule(reTrans, 5000, 1500);
						client.send(packets[index].clone());
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
		}
	}
	/*���캯��*/
	public SendWindow(Client client) {
		super(client);
	}		
	
	public void sendPacket_select(TCP_PACKET packet) {	//�򴰿��м����°�
		int index = now%windowSize;
		packets[index] = packet;
		checkAck[index] = false;
		timers[index] = new UDT_Timer();
		UDT_RetransTask reTrans = new UDT_RetransTask(client, packet);
		timers[index].schedule(reTrans, 5000, 1500);
		now++;
		client.send(packet);
		
	}


	
	public void sendPacket_GBN(TCP_PACKET packet) throws CloneNotSupportedException {	//�򴰿��м����°�
		//�ڴ��ڵ�idnex
		int index = now%windowSize;
		packets[index] = packet;
		timers[index] = new UDT_Timer();
		TaskPacketsRetrans reTrans = new TaskPacketsRetrans();
		timers[index].schedule(reTrans, 5000, 1500);
		setNowLog(packet.getTcpH().getTh_seq());
		now++;
		client.send(packet);
	}
	
	public void  ackPacket_select(TCP_PACKET packet) {	//�յ�ack���󽫷��صİ�ȷ��
		int i = packet.getTcpH().getTh_ack()/100;
		if(i >= 0) {
			int index = i%windowSize;
			if(packets[index]!= null) {	
				timers[index].cancel();
				checkAck[index] = true;
				if(i == begin) {	//���ӵ��İ��Ǵ��ڵ�һ��ֵʱ
					int j = begin;
					for(;j<= now && checkAck[j%windowSize];j++) {
						//�ƺ�
						packets[j%windowSize] = null;
						checkAck[j%windowSize] = false;
						timers[index] = null;
					}
					begin = Math.min(j,now);
					end = begin + windowSize -1;
					sequence = begin * 100 + 1;
				}
			}
			
		}
		receiveWindowlog_receive(i*100+1);
	}
	
	public void  ackPacket_GBN(TCP_PACKET packet) {	//�յ�ack���󽫷��صİ�ȷ��
		int seq = packet.getTcpH().getTh_ack();
		if(seq == sequence) {
			int index = (seq/100)%windowSize;
			if(packets[index]!= null) {	
				timers[index].cancel();
				begin++;
				end++;
				sequence += 100;
			}	
		}
	}
	
	public void receiveWindowlog_send(int seq) {
		//���dataQueue��������д���ļ�
		File fw = new File("sendLog.txt");
		BufferedWriter writer;
		
		try {
			writer = new BufferedWriter(new FileWriter(fw, now != 0));
			writer.write("\n***************************************************\n");
			writer.write("����seqΪ"+seq+"�İ�ʱ\t" +"��ʼ����Ϊ"+begin + "\t\t��ǰ���ʹ���Ϊ"+now+"\t��ֹ����Ϊ:"+end+"\n");
			for(int i = begin; i <= now;i++) {
				int index = i%windowSize;
				if(packets[index]!= null) {
					if(checkAck[index]) {
						writer.write("����"+(index+begin)+": sequence "+ packets[index].getTcpH().getTh_seq() +" is rereived!\n");
					}
					else{
						writer.write("����"+(index+begin)+": sequence "+ packets[index].getTcpH().getTh_seq() +" is not rereived!\n");
					}
				}
				
			}
			writer.write("***************************************************\n");
			writer.flush();		//����������
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//���Ͱ�ʱ���log�ļ�
	public void receiveWindowlog_receive(int seq) {
		//���dataQueue��������д���ļ�
		File fw = new File("ackLog.txt");
		BufferedWriter writer;
		
		try {
			writer = new BufferedWriter(new FileWriter(fw, now != 0));
			writer.write("\n=======================================================\n");
			writer.write("�յ�seqΪ"+seq+"�İ�ʱ\t" +"��ʼ����Ϊ"+begin+ "\t\t��ǰ���ʹ���Ϊ"+now+"\t��ֹ����Ϊ:"+end+"\n");
			for(int i = begin; i <= now;i++) {
				int index = i%windowSize;
				if(packets[index]!= null) {
					if(checkAck[index]) {
						writer.write("����"+(index+begin)+": sequence "+ packets[index].getTcpH().getTh_seq() +" is rereived!\n");
					}
					else{
						writer.write("����"+(index+begin)+": sequence "+ packets[index].getTcpH().getTh_seq() +" is not rereived!\n");
					}
				}
				
			}
			writer.write("=======================================================\n");
			writer.flush();		//����������
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setNowLog(int seq) {
		//���dataQueue��������д���ļ�
		File fw = new File("nowLog.txt");
		BufferedWriter writer;
		
		try {
			writer = new BufferedWriter(new FileWriter(fw, now != 0));
			writer.write("����seqΪ"+seq+"�İ�ʱ��ǰ���ʹ���Ϊ"+now+"\n");
			writer.flush();		//����������
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean  isFull() {
		System.out.println("begin:"+begin);
		System.out.println("now:"+now);
		System.out.println("end:"+end);
		return now == end;
	}
	
}
