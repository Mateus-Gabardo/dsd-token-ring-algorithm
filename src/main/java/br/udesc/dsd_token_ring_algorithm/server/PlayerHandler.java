package br.udesc.dsd_token_ring_algorithm.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import br.udesc.dsd_token_ring_algorithm.algorithm.TokenRingAlgorithm;

import br.udesc.dsd_token_ring_algorithm.utils.CommunicationUtils;

public class PlayerHandler extends Thread {
    private Socket clientSocket;
    private int playerId;
    private int numPlayers;
    private boolean hasToken;
    private final ReentrantLock lock;
    private final Condition tokenPassed;
    private int secretNumber;

    public PlayerHandler(Socket clientSocket, int playerId, int numPlayers, ReentrantLock lock, Condition tokenPassed, int secretNumber) {
        this.clientSocket = clientSocket;
        this.playerId = playerId;
        this.numPlayers = numPlayers;
        this.hasToken = (playerId == 0); // O primeiro jogador recebe o token inicialmente
        this.lock = lock;
        this.tokenPassed = tokenPassed;
        this.secretNumber = secretNumber;
    }

    @Override
    public void run() {
        try {
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            System.out.println("Novo jogador conectado: " + clientAddress);

            TokenRingAlgorithm tokenRingAlgorithm = new TokenRingAlgorithm(playerId, numPlayers, lock);

            while (true) {
                lock.lock();
                try {
                    while (!hasToken) {
                        tokenPassed.await(); // Aguarda até receber o token
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
                        tokenRingAlgorithm.passToken();

                    } else if (dataParts[0].equals("TOKEN")) {
                        // Jogador atual passou o token
                        hasToken = false;
                        tokenRingAlgorithm.receiveToken();
                        tokenPassed.signalAll(); // Notifica os jogadores esperando pelo token
                    }
                } finally {
                    lock.unlock();
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

