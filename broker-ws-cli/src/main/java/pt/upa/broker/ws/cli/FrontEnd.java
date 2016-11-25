package pt.upa.broker.ws.cli;


import java.util.List;
import java.util.Map;

import example.ws.handler.DigitalMarkHandler;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class FrontEnd {
	
	Map<String, Object> _requestContext = null;
	
	public FrontEnd(BrokerPortType port, Map<String, Object> requestContext){
		setPort(port);
		setrequestContext(requestContext);
	}
	
	private BrokerPortType _port;
	
	public BrokerPortType getPort(){return _port;}
	
	public void setPort(BrokerPortType port){_port = port;}
	
	private void setrequestContext(Map<String, Object> requestContext){_requestContext = requestContext;}
	
	private Map<String, Object> getrequestContext(){return _requestContext;}
	
	private void putonrequestContext(String initialValue){
		getrequestContext().put(DigitalMarkHandler.REQUEST_PROPERTY, initialValue);
	}
	
	private String getNonce(){
		return getPort().getNonce();
	}
	
	public String ping(String message){
		try {
			String result = getPort().ping(message);
			Thread.sleep(2500);
			
			for(int i = 0; i<3 ; i++){
				if(result == null){
					result = getPort().ping(message);
					Thread.sleep(2500);
				}
				else{
					break;
				}
			}
			return result;			
		} catch (InterruptedException e) {
			return "[FAILED]";
		}

	}	
	
	public String requestTransport(String origin, String destination, int price) throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		try {
			putonrequestContext(getNonce());
			String result = getPort().requestTransport(origin, destination, price);
			Thread.sleep(2500);
			
			for(int i = 0; i<3 ; i++){
				if(result == null){
					result = getPort().requestTransport(origin, destination, price);
					Thread.sleep(2500);
				}
				else{
					break;
				}
			}
			return result;			
		} catch (InterruptedException e) {
			return "[FAILED]";
		}
		
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		try {
			TransportView result = getPort().viewTransport(id);
			Thread.sleep(2500);
			
			for(int i = 0; i<3 ; i++){
				if(result == null){
					result = getPort().viewTransport(id);
					Thread.sleep(2500);
				}
				else{
					break;
				}
			}
			return result;			
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	public String clearTransports(){
		getPort().clearTransports();
		return "All transports have been cleared";
	}
	
	public List<TransportView> listTransports(){
		return getPort().listTransports();
	}
}
