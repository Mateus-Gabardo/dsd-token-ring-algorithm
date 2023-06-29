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

			for (int i = 0; i < currentPlayer; i++) {
				Socket clientSocket = serverSocket.accept();
				PlayerHandler player = new PlayerHandler(clientSocket, i, secretNumber, this);
				players.add(player);
				player.start();
			}

			// Inicia o jogo
			System.out.println("Número a ser adivinhado: " + secretNumber);
			players.get(0).send("TOKEN");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void encerarJogo() throws IOException {
		for(PlayerHandler player : players) {
			player.send("TOKEN" + "STOP" + String.valueOf(player.getPlayerId()));
		}
	}
	
	public List<PlayerHandler> getPlayers(){
		return this.players;
	}
	
	public synchronized boolean checkGuess(int playerId, int guess) throws IOException {
        if (guess == secretNumber) {
            System.out.println("Jogador " + playerId + " acertou o número!");
            this.encerarJogo();
            return true;
        } else {
            System.out.println("Jogador " + playerId + " tentou adivinhar o número " + guess + ", mas está incorreto.");
            return false;
        }
    }
}