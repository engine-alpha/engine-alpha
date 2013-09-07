package ea.edu.net;

import ea.*;

public class SimplerServer 
extends SimplerNetzwerkAdapter {

	/**
	 * Der Server.
	 */
	private final Server server;
	
	public SimplerServer(int port) {
		this.server = new Server(port);
		server.globalenEmpfaengerSetzen(messageUpdater);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void senden(String string) {
		server.sendeString(string);
	}

}
