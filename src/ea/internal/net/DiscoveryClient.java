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
		int bufferSize = 8192;

		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			socket.setSoTimeout(1000);

			try {
				while (!isInterrupted()) {
					byte[] sendData = "EA_DISCOVERY_REQ".getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 15035);
					socket.send(sendPacket);

					Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

					while (interfaces.hasMoreElements()) {
						NetworkInterface networkInterface = interfaces.nextElement();

						if (networkInterface.isLoopback() || !networkInterface.isUp()) {
							continue; // Don't want to broadcast to the loopback interface
						}

						for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
							InetAddress broadcast = interfaceAddress.getBroadcast();
							if (broadcast == null) {
								continue;
							}

							sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 15035);
							socket.send(sendPacket);
						}
					}

					try {
						byte[] recvBuf = new byte[bufferSize];
						DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
						socket.receive(receivePacket);

						String message = new String(receivePacket.getData()).trim();

						if (message.startsWith("EA_DISCOVERY_RESP")) {
							listener.serverGefunden(receivePacket.getAddress().getHostAddress());
						}

						System.out.println("rcvd..." + message);

						Thread.sleep(1000);
					} catch (SocketTimeoutException e) {
						// don't care, we want that
					}
				}
			} catch (InterruptedException ie) {
				// don't care
			}
		} catch (IOException ex) {
			// don't care, may be closed by interrupt
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	@Override
	public void interrupt () {
		if (socket != null) {
			this.socket.close();
		}

		super.interrupt();
	}
}
