package ea.internal.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

// http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
public class DiscoveryServer extends Thread {
	private static DiscoveryServer server;
	private DatagramSocket socket;

	private DiscoveryServer() {

	}

	public static void startServer() {
		if(server == null) {
			server = new DiscoveryServer();
			server.start();
		}
	}

	public static void stopServer() {
		if(server == null) {
			return;
		}

		server.interrupt();
	}

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(15035, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (!isInterrupted()) {
				byte[] recvBuf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				String cmd = new String(packet.getData()).trim();
				if (cmd.equals("EA_DISCOVERY_REQUEST")) {
					if(!getLocalAddresses().contains(packet.getAddress().getHostAddress())) {
						byte[] sendData = "EA_DISCOVERY_RESPONSE".getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
						socket.send(sendPacket);
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if(socket != null) {
				socket.close();
			}
		}
	}

	private static ArrayList<String> getLocalAddresses() {
		ArrayList<String> addrs = new ArrayList<>();

		addrs.add("127.0.0.1");

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()){
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.isVirtual()) {
					continue;
				}

				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

				while (addresses.hasMoreElements()){
					InetAddress currentAddress = addresses.nextElement();

					if (currentAddress.isLoopbackAddress()) {
						continue;
					}

					addrs.add(currentAddress.getHostAddress());
				}
			}
		} catch(Exception e) {
			// use default 127.0.0.1
		}

		return addrs;
	}
}
