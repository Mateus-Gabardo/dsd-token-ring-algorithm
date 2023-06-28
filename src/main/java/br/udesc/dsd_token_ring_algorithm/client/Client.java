package br.udesc.dsd_token_ring_algorithm.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import br.udesc.dsd_token_ring_algorithm.utils.CommunicationUtils;

public class Client {
    private String serverIP;
    private int serverPort;
    private Socket clientSocket;

    public Client(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void start() {
        try {
            clientSocket = new Socket(serverIP, serverPort);

            String receivedData = CommunicationUtils.receive(clientSocket);
            String[] dataParts = receivedData.split(" ");
            int secretNumber = Integer.parseInt(dataParts[1]);

            System.out.println("Adivinhe o número entre 1 e 10!");

            while (true) {
                System.out.print("Digite um número: ");
                int guess = new Scanner(System.in).nextInt();

                CommunicationUtils.send(clientSocket, "GUESS " + guess);

                receivedData = CommunicationUtils.receive(clientSocket);

                if (receivedData.equals("CORRECT")) {
                    System.out.println("Parabéns! Você adivinhou corretamente o número.");
                    break;
                } else if (receivedData.equals("INCORRECT")) {
                    System.out.println("Tente novamente.");
                }

                CommunicationUtils.send(clientSocket, "TOKEN"); // Passa o token para o próximo jogador
            }

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

