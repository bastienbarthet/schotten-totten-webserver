package server;

import java.io.IOException;

public class RunLanGameServer {

	private final static LanGameServer lGameServer = new LanGameServer(8080);
	
	public static void main(String[] args) throws IOException {
		lGameServer.start();
		System.out.println("Server running");
		while (lGameServer.isAlive()) {
			//System.out.println("Server running");
		}
		System.out.println("Server stopped");
	}

}
