package pt.upa.broker.ws;

import java.util.List;
import java.util.Scanner;

import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerApplication{
	
	public static void main(String[] args) throws Exception{
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", BrokerApplication.class.getName());
			return;
		}
		
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		
		if(args.length == 1){
			wsURL = args[0];
		}
		else{
			uddiURL = args[0];
			wsName = args[1];
		}
		
		BrokerClient client = null;
		
		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new BrokerClient(wsURL);
		} else if (uddiURL != null) {
			System.out.printf("Creating client using UDDI at %s for server with name %s%n", uddiURL, wsName);
			client = new BrokerClient(uddiURL, wsName);
		}
		
		//Cleaning the debug info
		final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
        
        //Service start
		System.out.println("=============================================");
		System.out.println("===                                       ===");
		System.out.println("===        Welcome to UPA Transports      ===");
		System.out.println("===                                       ===");
		System.out.println("=============================================");
		
		System.out.println("|----------------------------------------|");
		System.out.println("|           Available commands           |");
		System.out.println("|----------------------------------------|");
		System.out.println("|requestTransport -> Request Transport   |");
		System.out.println("|listTransports -> List Transports       |");
		System.out.println("|clearTransports -> Clear Transports     |");
		System.out.println("|viewTransport -> View Transport         |");
		System.out.println("|ping -> Ping                            |");
		System.out.println("|exit -> Exit                            |");
		System.out.println("|----------------------------------------|");
		
		Scanner scanner = new Scanner(System.in);
		//BrokerClient client = new BrokerClient(port);
		try{
			System.out.print("Command: ");
			String command = scanner.next();
			while(!command.equals("exit")){
				if(command.equals("requestTransport")){
					System.out.print("Origin: ");
					String origin = scanner.next();
					System.out.print("Destination: ");
					String destination = scanner.next();
					System.out.print("Max price: ");
					int price = scanner.nextInt();
					String id = "";
					try {
						id = client.requestTransport(origin, destination, price);
						if(id.equals("[FAILED]")){
							System.out.println("Failed to request transport because server is down");
							continue;
						}
						
					} 
					catch (InvalidPriceFault_Exception | UnavailableTransportFault_Exception
							| UnavailableTransportPriceFault_Exception | UnknownLocationFault_Exception e) {
						System.out.println("[ERROR] " + e.getMessage());
					}
					if(!id.equals("")){
						System.out.println("----ACCEPTED----\n" + "Id: " + id);
					}
				}
				
				else if(command.equals("viewTransport")){
					System.out.print("ID: ");
					String id = scanner.next();
					try {
						TransportView tv = client.viewTransport(id);
						if(tv == null){
							System.out.println("Failed to request transport because server is down");
							continue;
						}
						System.out.println(tv.getState().toString());
					} catch (UnknownTransportFault_Exception e) {
						System.out.println("[ERROR] " + e.getMessage());
					}
				}
				
				else if(command.equals("ping")){
					String result = client.ping("connectivity");
					if(result.equals("[FAILED]")){
						System.out.println("Failed to request transport because server is down");
						continue;
					}
					System.out.println(result);
				}
				
				else if(command.equals("listTransports")){
					List<TransportView> tvl = client.listTransports();
					for(TransportView tv : tvl){
						System.out.println();
						System.out.println(client.toString(tv));
						System.out.println("-------------");
					}
				}
				
				else if(command.equals("clearTransports")){
					System.out.println(client.clearTransports());
				}
				else{
					System.out.println("Invalid command");
				}
				System.out.print("Command: ");
				command = scanner.next();
			}
			
		}
		finally{
			scanner.close();
		}
		
	}
}