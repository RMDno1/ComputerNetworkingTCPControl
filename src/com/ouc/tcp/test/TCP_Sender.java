package com.ouc.tcp.test;


import com.ouc.tcp.client.TCP_Sender_ADT;
import com.ouc.tcp.message.*;


public class TCP_Sender extends TCP_Sender_ADT {
	
	private TCP_PACKET tcpPack;	//�����͵�TCP���ݱ�
	SendWindow window = new SendWindow(client);	//ʵ��������
	
	/*���캯��*/
	public TCP_Sender() {
		super();	//���ó��๹�캯��
		super.initTCP_Sender(this);		//��ʼ��TCP���Ͷ�
	}
	
	@Override
	//�ɿ����ͣ�Ӧ�ò���ã�����װӦ�ò����ݣ�����TCP���ݱ�
	public void rdt_send(int dataIndex, int[] appData) {
		//����TCP���ݱ���������ź������ֶ�/У���),ע������˳��
		tcpH.setTh_seq(dataIndex * appData.length + 1);//���������Ϊ�ֽ����ţ���Ҳ����ʹ��������ŷ�ʽ��ע���޸Ķ�Ӧ�Ľ��շ��ж���ŵĲ���
		tcpH.setTh_eflag((byte)7);
		tcpH.setTh_sum((short)0);//�Ƚ�У������Ϊ0�����ں����ļ���
		tcpS.setData(appData);		
		tcpPack = new TCP_PACKET(tcpH, tcpS, destinAddr);		
		//����У���룻��Ҫ���½�tcpH���뵽tcpPack				
		tcpH.setTh_sum(CheckSum.computeChkSum(tcpPack));
		tcpPack.setTcpH(tcpH);
		//����TCP���ݱ�
		while(window.isFull());
		try {
			TCP_PACKET packet = new TCP_PACKET(tcpH.clone(), tcpS.clone(), destinAddr);	
			window.sendPacket_GBN(packet);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	//���ɿ����ͣ�������õ�TCP���ݱ�ͨ�����ɿ������ŵ�����
	public void udt_send(TCP_PACKET tcpPack) {
	}
	
	@Override
	//����ACK���ģ�������ACK�봦��ACK�ֿ�
	public void waitACK() {
		//ѭ�����ackQueue;


	}

	@Override
	//���յ�ACK���ģ����У��ͣ���ȷ�ϺŲ���ack����
	public void recv(TCP_PACKET recvPack) {
		//��Ҫ���У���
		if(CheckSum.computeChkSum(recvPack) != 0)
			return;
		window.ackPacket_GBN(recvPack);
	}
	
}
