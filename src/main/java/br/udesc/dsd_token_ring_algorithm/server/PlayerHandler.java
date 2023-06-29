package br.udesc.dsd_token_ring_algorithm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import br.udesc.dsd_token_ring_algorithm.utils.CommunicationUtils;

public class PlayerHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean hasToken;
    
    private int playerId;
    private Server server;

    public PlayerHandler(Socket clientSocket, int playerId, int secretNumber, Server server) {
        this.clientSocket = clientSocket;
        this.playerId = playerId;
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
                	/// entra na zona critica
                    handleToken(dataParts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleToken(String[] dataParts) {
        if (hasToken) {
            // Executa a região crítica
            System.out.println("Jogador " + playerId + " executando a região crítica.");
            if (dataParts[0].equals("GUESS")) {
                int guess = Integer.parseInt(dataParts[1]);
                server.checkGuess(playerId, guess);
            }

            // Passa o token para o próximo jogador
            int numPlayers = this.server.getPlayers().size();
            int nextPlayer = (playerId + 1) % numPlayers;
            this.server.getPlayers().get(nextPlayer).sendToken();
        }
    }

    public void send(String message) throws IOException {
        CommunicationUtils.send(clientSocket, message);
    }
    public void setToken(boolean hasToken) {
        this.hasToken = hasToken;
    }
    
    public void sendToken() {
        writer.println("TOKEN");
        hasToken = true;
    }
}

