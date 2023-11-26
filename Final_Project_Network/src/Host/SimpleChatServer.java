package Host;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class SimpleChatServer {

	private int port;
	private ServerSocket serverSocket = null;
	private Thread acceptThread = null;
	private Vector<ClientHandler> users = new Vector<>();


	public SimpleChatServer(int port) {
		this.port = port;
	}


	// public void startServer() {
	// Socket clientSocket;
	// try {
	// serverSocket = new ServerSocket(port);
	// System.out.println("서버가 시작되었습니다.");
	//
	// while (true) {
	// clientSocket = serverSocket.accept();
	// ClientHandler cHandler = new ClientHandler(clientSocket);
	// users.add(cHandler);
	// cHandler.start();
	// }
	//
	// } catch (IOException e) {
	// System.err.println("서버 오류: " + e.getMessage());
	// } finally {
	// try {
	// if (serverSocket != null)
	// serverSocket.close();
	// } catch (IOException e) {
	// System.err.println("서버 닫기 오류> " + e.getMessage());
	// System.exit(-1);
	// }
	//
	// }
	//
	// }
	public void startServer() {
		// 기존 startServer() 메소드 내용을 별도의 스레드로 실행
		Thread serverThread = new Thread(() -> {
			Socket clientSocket;
			try {
				serverSocket = new ServerSocket(port);
				System.out.println("서버가 시작되었습니다.");

				while (true) {
					clientSocket = serverSocket.accept();
					ClientHandler cHandler = new ClientHandler(clientSocket);
					users.add(cHandler);
					cHandler.start();
				}

			} catch (IOException e) {
				System.err.println("서버 오류: " + e.getMessage());
			} finally {
				// 예외 처리 및 마무리 작업
			}

		});
		serverThread.start();
	}


	private class ClientHandler extends Thread {
		private Socket clientSocket;
		private BufferedWriter out;
		private String uid;


		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}


		private void receiveMessages(Socket cs) {
			try {
				BufferedReader in = new BufferedReader(
				    new InputStreamReader(cs.getInputStream(), "UTF-8"));
				out = new BufferedWriter(
				    new OutputStreamWriter(cs.getOutputStream(), "UTF-8"));

				String message;
				while ((message = in.readLine()) != null) {
					if (message.contains("/uid:")) {
						String[] tok = message.split(":");
						uid = tok[1];
						System.out.println("새 참가자: " + uid);
						continue;
					}

					message = uid + ": " + message;
					System.out.println(message);
					broadcasting(message);
				}

				users.removeElement(this);
				System.out.println(uid + "퇴장. 현재 참가자 수: " + users.size());
			} catch (IOException e) {
				users.removeElement(this);
				System.out.println(uid + " 연결 끊김. 현재 참가자 수: " + users.size());
			} finally {
				try {
					cs.close();
				} catch (IOException e) {
					System.err.println("서버 닫기 오류> " + e.getMessage());
					System.exit(-1);
				}

			}

		}


		private void sendMessage(String msg) {
			try {
				out.write(msg + "\n");
				out.flush();
			} catch (IOException e) {
				System.err.println("서버 방향 전송 오류>" + e.getMessage());
			}

		}


		private void broadcasting(String msg) {
			for (ClientHandler c : users) {
				c.sendMessage(msg);
			}

		}


		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
	}
}