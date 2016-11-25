package pt.upa.broker.ws;

import pt.upa.broker.ws.cli.BrokerClient;
import pt.upa.broker.ws.cli.BrokerClientException;

public class Alive implements Runnable{
	
	private Thread t;
	private String _threadName;
	
	public Alive(String name){
		_threadName = name;
	}
	
	@Override
	public void run() {
		String wsURL = "http://localhost:8084/broker-ws/endpoint";
		try {
			BrokerClient client = new BrokerClient(wsURL);
			while(true){				
				client.ping("alive");
				Thread.sleep(3000);
			}			
		} catch (BrokerClientException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void start(){
		if(t==null){
			t = new Thread(this, _threadName);
			t.start();
		}
	}
	
	
}
