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
			InetAddress address = InetAddress.getByName("255.255.255.255");
			socket = new DatagramSocket();
			socket.setBroadcast(true);

			try {
				while (!isInterrupted()) {
					byte[] sendData = "EA_DISCOVERY".getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, 15035);
					socket.send(sendPacket);
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				// don't care
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
