package ea.internal.net;

import ea.ConnectListener;

import java.net.*;
import java.io.*;
import java.util.*;

// http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
public class DiscoveryClient extends Thread {
	private DatagramSocket socket;
	private ConnectListener listener;

	public DiscoveryClient(ConnectListener listener) {
		this.listener = listener;
	}

	public void run() {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);

			byte[] sendData = "EA_DISCOVERY_REQUEST".getBytes();

			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 15035);
				socket.send(sendPacket);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.isVirtual()) {
					continue;
				}

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();

					if (broadcast == null) {
						continue;
					}

					try {
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 15035);
						socket.send(sendPacket);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			byte[] recvBuf = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			socket.receive(receivePacket);

			String cmd = new String(receivePacket.getData()).trim();

			if (cmd.equals("EA_DISCOVERY_RESPONSE")) {
				listener.onConnect(receivePacket.getAddress().getHostAddress());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if(socket != null) {
				socket.close();
			}
		}
	}
}
