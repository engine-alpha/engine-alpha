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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
			socket = new DatagramSocket(15035, InetAddress.getByName("0.0.0.0"));

			while (!isInterrupted()) {
				byte[] recvBuf = new byte[bufferSize];
				DatagramPacket receivePacket = new DatagramPacket(recvBuf, bufferSize);
				socket.receive(receivePacket);
				String cmd = new String(receivePacket.getData()).trim();

				if (cmd.equals("EA_DISCOVERY")) {
					listener.serverGefunden(receivePacket.getAddress().getHostAddress());
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
		this.socket.close();
		super.interrupt();
	}
}
