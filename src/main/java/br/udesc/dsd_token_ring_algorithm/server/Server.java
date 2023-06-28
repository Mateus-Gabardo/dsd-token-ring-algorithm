package br.udesc.dsd_token_ring_algorithm.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
	private static final int PORT = 80;

	private ServerSocket serverSocket;
	private List<PlayerHandler> players;
	private int secretNumber;
	private int currentPlayer;
	private ReentrantLock lock;

	public Server(int numPlayers) {
        players = new ArrayList<PlayerHandler>();
        secretNumber = new Random().nextInt(10) + 1; // Número aleatório entre 1 e 10
        currentPlayer = numPlayers;
        lock = new ReentrantLock();
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(PORT);
			serverSocket.setReuseAddress(true);
			InetAddress ipCon = InetAddress.getLocalHost();
			System.out.println("Servidor iniciado. IP:"+ ipCon.getHostAddress() +". Aguardando conexão...");

			Condition tokenPassed = lock.newCondition();

			for (int i = 0; i < currentPlayer; i++) {
				Socket clientSocket = serverSocket.accept();
				PlayerHandler player = new PlayerHandler(clientSocket, i, currentPlayer, lock, tokenPassed, secretNumber);
				players.add(player);
				player.start();
			}

			// Inicia o jogo
			System.out.println("Número a ser adivinhado: " + secretNumber);
			players.get(0).send("START " + secretNumber);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}