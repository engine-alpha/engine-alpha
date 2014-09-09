/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea.internal.net;

import ea.ServerGefundenReagierbar;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

// http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
public class DiscoveryClient extends Thread {
	private DatagramSocket socket;

	private ServerGefundenReagierbar listener;

	public DiscoveryClient (ServerGefundenReagierbar listener) {
		this.listener = listener;
	}

	public void run () {
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
				listener.serverGefunden(receivePacket.getAddress().getHostAddress());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
}
