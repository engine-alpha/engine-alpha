package ea;

import ea.internal.net.DiscoveryClient;

public class ServerDiscovery {
	private ServerDiscovery() {

	}

	public static void startDiscovery(ConnectListener listener) {
		new DiscoveryClient(listener).start();
	}
}
