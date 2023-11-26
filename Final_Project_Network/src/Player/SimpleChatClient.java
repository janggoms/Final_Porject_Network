//2131339 장도윤
package Player;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class SimpleChatClient {
	private Socket socket;
	private Writer out;
	private Reader in;


	public SimpleChatClient(String serverAddress, int serverPort) {
		try {
			socket = new Socket();
			SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
			socket.connect(sa, 3000);

			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			startListening();
		} catch (IOException e) {
			System.err.println("Connection error: " + e.getMessage());
		}

	}


	private void startListening() {
		Thread receiveThread = new Thread(() -> {
			try {
				while (true) {
					String message = ((BufferedReader) in).readLine();
					if (message == null) {
						disconnect();
						System.out.println("Server connection closed");
						break;
					}

					System.out.println(message);
				}

			} catch (IOException e) {
				System.err.println("Receiving error: " + e.getMessage());
			}

		});
		receiveThread.start();
	}


	private void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Error while closing client: " + e.getMessage());
		}

	}


	public void sendMessage(String message) {
		try {
			((BufferedWriter) out).write(message + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println("Error sending message: " + e.getMessage());
		}

	}
}