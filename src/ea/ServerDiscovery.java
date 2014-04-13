package ea;

import ea.internal.net.DiscoveryClient;

public class ServerDiscovery {
	private ServerDiscovery() {

	}

	public static void startDiscovery(DiscoveryListener listener) {
		new DiscoveryClient(listener).start();
	}
}
