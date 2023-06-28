package br.udesc.dsd_token_ring_algorithm;

import java.util.Scanner;

import br.udesc.dsd_token_ring_algorithm.client.Client;
import br.udesc.dsd_token_ring_algorithm.server.Server;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Selecione uma opção:");
        System.out.println("1. Iniciar como servidor");
        System.out.println("2. Iniciar como cliente");
        int option = scanner.nextInt();

        if (option == 1) {
            startServer();
        } else if (option == 2) {
            startClient();
        } else {
            System.out.println("Opção inválida. Encerrando o programa.");
        }
    }

    private static void startServer() {
        Server server = new Server();
        server.start();
    }

    private static void startClient() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o endereço IP do servidor: ");
        String serverIP = scanner.nextLine();

        System.out.print("Digite a porta do servidor: ");
        int serverPort = scanner.nextInt();

        Client client = new Client(serverIP, serverPort);
        client.start();
    }
}
