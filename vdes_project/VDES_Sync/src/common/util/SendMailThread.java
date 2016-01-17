package common.util;


public class SendMailThread extends Thread{
	public MailEntity mailEntity = null;
	public void setMailEntity(MailEntity mailEntity){
		this.mailEntity = mailEntity;
	}
	public void run(){
			System.out.println("邮件发送开始！");
			Mail.send(mailEntity.getSubject(), mailEntity.getContent(), mailEntity.getToAddress());
			System.out.println("邮件发送结束！");			
	  }
}