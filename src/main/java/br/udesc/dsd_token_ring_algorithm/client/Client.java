package br.udesc.dsd_token_ring_algorithm.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import br.udesc.dsd_token_ring_algorithm.utils.CommunicationUtils;

public class Client {
	private String serverIP;
	private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;
	private final int PORT = 80;

	public Client(String serverIP) {
		this.serverIP = serverIP;
	}

	public void start() {
		try {
			clientSocket = new Socket(serverIP, PORT);
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			writer = new PrintWriter(clientSocket.getOutputStream(), true);

			System.out.println("Conexão estabelecida com o servidor " + serverIP);
			System.out.println("Aguardando instruções do servidor...");

			while (true) {
				String receivedData = reader.readLine();
				String[] dataParts = receivedData.split(" ");

				if (dataParts[0].equals("START")) {
					int secretNumber = Integer.parseInt(dataParts[1]);
					handleStart(secretNumber);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
    private void handleStart(int secretNumber) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite seu palpite: ");
        int guess = scanner.nextInt();
        sendGuess(guess);
    }

    private void sendGuess(int guess) {
        writer.println("GUESS " + guess);
    }

    private void closeConnection() {
        try {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
            if (clientSocket != null)
                clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
