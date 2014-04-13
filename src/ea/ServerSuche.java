package ea;

import ea.internal.net.DiscoveryClient;

public class ServerSuche {
	private ServerSuche () {

	}

	public static void start (ServerGefundenReagierbar listener) {
		new DiscoveryClient(listener).start();
	}
}