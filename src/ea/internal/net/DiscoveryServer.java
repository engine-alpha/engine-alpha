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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
public class DiscoveryServer extends Thread {
	private static DiscoveryServer server;
	private DatagramSocket socket;

	private DiscoveryServer () {

	}

	public static void startServer () {
		if (server == null) {
			server = new DiscoveryServer();
			server.start();
		}
	}

	public static void stopServer () {
		if (server == null) {
			return;
		}

		server.interrupt();

		try {
			server.join();
		} catch (InterruptedException e) {
			// don't care
		}

		server = null;
	}

	@Override
	public void run () {
		try {
			int bufferSize = 8192;
			socket = new DatagramSocket(15035, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (!isInterrupted()) {
				byte[] recvBuf = new byte[bufferSize];
				DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(receivePacket);
				String cmd = new String(receivePacket.getData()).trim();

				if (cmd.startsWith("EA_DISCOVERY_REQ")) {
					byte[] sendData = "EA_DISCOVERY_RESP".getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
					socket.send(sendPacket);
					System.out.println("sent...");
				}
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
