package br.udesc.dsd_token_ring_algorithm.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.udesc.dsd_token_ring_algorithm.utils.CommunicationUtils;

public class Server {
    private static final int PORT = 1234;
    private static final int NUM_PLAYERS = 2; // Número de jogadores

    private ServerSocket serverSocket;
    private List<PlayerHandler> players;
    private int secretNumber;
    private int currentPlayer;

    public Server() {
        players = new ArrayList<PlayerHandler>();
        secretNumber = new Random().nextInt(10) + 1; // Número aleatório entre 1 e 10
        currentPlayer = 0;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado na porta " + PORT);

            for (int i = 0; i < NUM_PLAYERS; i++) {
                Socket clientSocket = serverSocket.accept();
                PlayerHandler player = new PlayerHandler(clientSocket, i);
                players.add(player);
                player.start();
            }

            // Inicia o jogo
            System.out.println("Número a ser adivinhado: " + secretNumber);
            players.get(currentPlayer).send("START " + secretNumber);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PlayerHandler extends Thread {
        private Socket clientSocket;
        private int playerId;
        private boolean hasToken;

        public PlayerHandler(Socket clientSocket, int playerId) {
            this.clientSocket = clientSocket;
            this.playerId = playerId;
            this.hasToken = (playerId == 0); // O primeiro jogador recebe o token inicialmente
        }

        @Override
        public void run() {
            try {
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                System.out.println("Novo jogador conectado: " + clientAddress);

                while (true) {
                    // Aguarda pelo token
                    while (!hasToken) {
                        sleep(100); // Aguarda um tempo antes de verificar novamente
                    }

                    String receivedData = CommunicationUtils.receive(clientSocket);
                    String[] dataParts = receivedData.split(" ");

                    if (dataParts[0].equals("GUESS")) {
                        int guess = Integer.parseInt(dataParts[1]);
                        String response;

                        if (guess == secretNumber) {
                            response = "CORRECT";
                            System.out.println("Jogador " + playerId + " adivinhou corretamente o número!");
                        } else {
                            response = "INCORRECT";
                            System.out.println("Jogador " + playerId + " tentou " +
                                    "adivinhar o número " + guess + ", mas está incorreto.");
                        }

                        CommunicationUtils.send(clientSocket, response);

                        // Passa o token para o próximo jogador
                        currentPlayer = (currentPlayer + 1) % NUM_PLAYERS;
                        players.get(currentPlayer).setToken(true);

                    } else if (dataParts[0].equals("TOKEN")) {
                        // Jogador atual passou o token
                        hasToken = false;
                        players.get((playerId + 1) % NUM_PLAYERS).setToken(true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

        public void send(String message) throws IOException {
            CommunicationUtils.send(clientSocket, message);
        }

        public void setToken(boolean hasToken) {
            this.hasToken = hasToken;
        }
    }
}