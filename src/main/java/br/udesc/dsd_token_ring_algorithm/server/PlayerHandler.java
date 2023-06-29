package br.udesc.dsd_token_ring_algorithm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import br.udesc.dsd_token_ring_algorithm.utils.CommunicationUtils;

public class PlayerHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    
    private int playerId;
    private Server server;
    private ReentrantLock recurse = new ReentrantLock();

    public PlayerHandler(Socket clientSocket, int playerId, int secretNumber, Server server) {
        this.clientSocket = clientSocket;
        this.playerId = playerId;
        this.server = server;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    	try {
            while (true) {
                String receivedData = reader.readLine();
                String[] dataParts = receivedData.split(" ");

                if (dataParts[0].equals("TOKEN")) {
                	/// Entra na zona critica
                	recurse.lock();
                    handleToken(dataParts);
                    recurse.unlock();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleToken(String[] dataParts) throws IOException {
		// Executa a região crítica
	    System.out.println("Jogador " + playerId + " executando a região crítica.");
	    if(dataParts.length > 1) {
	    	int guess = Integer.parseInt(dataParts[1]);
	    	server.checkGuess(playerId, guess);
	    }
	
	    // Passa o token para o próximo jogador
	    int numPlayers = this.server.getPlayers().size();
	    int nextPlayer = (playerId + 1) % numPlayers;
	    this.server.getPlayers().get(nextPlayer).sendToken();
	}
    
    public int getPlayerId() {
    	return this.playerId;
    }

    public void send(String message) throws IOException {
        CommunicationUtils.send(clientSocket, message);
    }
    
    public void sendToken() {
        writer.println("TOKEN");
    }
}

