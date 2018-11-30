package com.ouc.tcp.test;

import com.ouc.tcp.message.TCP_PACKET;
import com.ouc.tcp.message.TCP_HEADER;
public class CheckSum {
	/*����TCP���Ķ�У��ͣ�ֻ��У��TCP�ײ��е�seq��ack��sum���Լ�TCP�����ֶ�*/
	public static short computeChkSum(TCP_PACKET tcpPack) {
		int checkSum = 0;
		TCP_HEADER header = tcpPack.getTcpH();
		int[] data = tcpPack.getTcpS().getData();
		int len = data.length,
			flag = 0xffff,
			flagmod = 65536;
		checkSum = header.getTh_ack();		//��ֹseq�ֶγ���0xffff
		if(checkSum>flag)
			checkSum = checkSum%flagmod+checkSum/flagmod;
		checkSum +=header.getTh_seq();
		if(checkSum>flag)
			checkSum = checkSum%flagmod+checkSum/flagmod;
		for(int i = 0;i < len;i++) {
			checkSum += data[i];
			if(checkSum>flag)
				checkSum = checkSum%flagmod+checkSum/flagmod;
		}
		checkSum += header.getTh_sum();		
		if(checkSum>flag)
			checkSum = checkSum%flagmod+checkSum/flagmod;
		checkSum = ~checkSum;
		return (short) checkSum;
	}
	
}
