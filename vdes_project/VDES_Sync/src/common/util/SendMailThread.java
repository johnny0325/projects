package common.util;


public class SendMailThread extends Thread{
	public MailEntity mailEntity = null;
	public void setMailEntity(MailEntity mailEntity){
		this.mailEntity = mailEntity;
	}
	public void run(){
			System.out.println("�ʼ����Ϳ�ʼ��");
			Mail.send(mailEntity.getSubject(), mailEntity.getContent(), mailEntity.getToAddress());
			System.out.println("�ʼ����ͽ�����");			
	  }
}