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
	private String palpite = null;

	public Client(String serverIP) {
		this.serverIP = serverIP;
	}

	public void start() {
		try {
			clientSocket = new Socket(serverIP, PORT);
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			writer = new PrintWriter(clientSocket.getOutputStream(), true);

			System.out.println("Conexão estabelecida com o servidor " + serverIP);
			enviaPalpite();
			
			while (true) {
				String receivedData = reader.readLine();
				String[] dataParts = receivedData.split(" ");

				if (dataParts[0].equals("TOKEN")) {
					if(palpite != null) {
						writer.println("TOKEN " + palpite);
						this.palpite = null;
						System.out.println("Palpite enviado");
						enviaPalpite();
					} else {
						writer.println("TOKEN");
					}
				}
				
				if(dataParts.length > 1) {
					if(dataParts[1] == "STOP") {
						System.out.println("Jogo encerrado!\n O vencedor foi o jogador " + dataParts[2]);
						clientSocket.close();
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
    private void enviaPalpite() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite um palpite: ");
        int guess = scanner.nextInt();
        this.palpite = String.valueOf(guess);
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
