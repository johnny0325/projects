package com.test.socket;
/**
 * 
 * @author chenjd
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class NBServer {
	int port = 8090;
	int BUFFERSIZE = 1024;
	Selector selector = null;
	ServerSocketChannel serverChannel = null;
	HashMap clientChannelMap = null;// �������ÿһ���ͻ����Ӷ�Ӧ���׽��ֺ�ͨ��

	public NBServer(int port) {
		this.clientChannelMap = new HashMap();
		this.port = port;
	}

	public void initialize() throws IOException {
		// ��ʼ�����ֱ�ʵ����һ��ѡ������һ���������˿�ѡ��ͨ��
		this.selector = Selector.open();
		this.serverChannel = ServerSocketChannel.open();
		this.serverChannel.configureBlocking(false);
		InetAddress localhost = InetAddress.getLocalHost();
		InetSocketAddress isa = new InetSocketAddress(localhost, this.port);
		this.serverChannel.socket().bind(isa);// �����׽��ְ󶨵�������ĳһ���ö˿�
	}

	// ����ʱ�ͷ���Դ
	public void finalize() throws IOException {
		this.serverChannel.close();
		this.selector.close();
	}

	// �������ֽڻ������Ϣ����
	public String decode(ByteBuffer byteBuffer) throws CharacterCodingException {
		Charset charset = Charset.forName("ISO-8859-1");
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(byteBuffer);
		String result = charBuffer.toString();
		return result;
	}

	// �����˿ڣ���ͨ��׼����ʱ������Ӧ����
	public void portListening(String data) throws IOException, InterruptedException {
		// ��������ͨ��ע��OP_ACCEPT�¼�
		SelectionKey acceptKey = this.serverChannel.register(this.selector,
				SelectionKey.OP_ACCEPT);
		// ������ע����¼�����ʱ,select()����ֵ������0
		while (acceptKey.selector().select() > 0) {
			System.out.println("event happened");
			// ȡ�������Ѿ�׼���õ�����ѡ���
			Set readyKeys = this.selector.selectedKeys();
			// ʹ�õ�������ѡ���������ѯ
			Iterator i = readyKeys.iterator();
			while (i.hasNext()) {
				SelectionKey key = (SelectionKey) i.next();
				i.remove();// ɾ����ǰ��Ҫ�����ѡ���
				if (key.isAcceptable()) {// ������пͻ�����������
					System.out.println("more client connect in!");
					ServerSocketChannel nextReady = (ServerSocketChannel) key
							.channel();
					// ��ȡ�ͻ����׽���
					Socket s = nextReady.accept().socket();
					// ���ö�Ӧ��ͨ��Ϊ�첽��ʽ��ע�����Ȥ�¼�
					s.getChannel().configureBlocking(false);
					SelectionKey readWriteKey = s.getChannel().register(
							this.selector,
							SelectionKey.OP_READ | SelectionKey.OP_WRITE);
					// ��ע����¼�����׽�����ϵ����
					readWriteKey.attach(s);
					// ����ǰ�������ӵĿͻ����׽��ּ���Ӧ��ͨ������ڹ�ϣ��//clientChannelMap��
					this.clientChannelMap.put(s, new ClientChInstance(s
							.getChannel()));
				} else if (key.isReadable()) {// �����ͨ����׼�����¼�
					System.out.println("Readable");
					// ȡ��ѡ�����Ӧ��ͨ�����׽���
					SelectableChannel nextReady = (SelectableChannel) key
							.channel();
					Socket socket = (Socket) key.attachment();
					// ������¼����������ѷ�װ����ClientChInstance��
					this.readFromChannel(socket.getChannel(),
							(ClientChInstance) this.clientChannelMap
									.get(socket));
				} else if (key.isWritable()) {// �����ͨ��д׼�����¼�
					System.out.println("writeable");
					// ȡ���׽��ֺ�������ͬ��
					Socket socket = (Socket) key.attachment();
					SocketChannel channel = (SocketChannel) socket.getChannel();
//					this.writeToChannel(channel, "This is from server!");
					this.writeToChannel(channel, data);
				}
			}
		}
	}

	// ��ͨ����д����
	public void writeToChannel(SocketChannel channel, String message)
			throws IOException {
		ByteBuffer buf = ByteBuffer.wrap(message.getBytes());
		int nbytes = channel.write(buf);
	}

	// ��ͨ���Ķ�����
	public void readFromChannel(SocketChannel channel,
			ClientChInstance clientInstance) throws IOException,
			InterruptedException {
		ByteBuffer byteBuffer = null;
		try{
			byteBuffer = ByteBuffer.allocate(BUFFERSIZE);
			int nbytes = channel.read(byteBuffer);
		}catch(Exception e){
			clientChannelMap.remove(channel.socket());
			channel.close();
			e=null;
			return;
		}
		byteBuffer.flip();
		String result = this.decode(byteBuffer);
		// ���ͻ��˷�����@exit���˳�����ʱ���ر���ͨ��
		if (result.indexOf("@exit") >= 0||result.indexOf("q")>=0) {
			channel.close();
		}
//		else if(result.indexOf("@close") >= 0){//�رշ���
//			channel.close();
//			this.finalize();
//		}
		else {
			clientInstance.append(result.toString());
			// ����һ����ϣ�ִ����Ӧ����
			if (result.indexOf("\n") >= 0) {
				System.out.println("client input" + result);
				clientInstance.execute();
			}
		}
	}

	// �����װ�������Կͻ��˵�ͨ�����в���������ʵ�ֿ���ͨ������execute()����
	public class ClientChInstance {
		SocketChannel channel;
		StringBuffer buffer = new StringBuffer();

		public ClientChInstance(SocketChannel channel) {
			this.channel = channel;
		}

		public void execute() throws IOException {
			String message = "This is response after reading from channel!";
			writeToChannel(this.channel, message);
			buffer = new StringBuffer();
		}

		// ��һ��û�н���ʱ������ǰ�ִ����ڻ���β
		public void append(String values) {
			buffer.append(values);
		}
	}

	// ������
	public static void main(String[] args) {
		NBServer nbServer = new NBServer(8090);
		try {
			nbServer.initialize();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			nbServer.portListening("This is from server!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
