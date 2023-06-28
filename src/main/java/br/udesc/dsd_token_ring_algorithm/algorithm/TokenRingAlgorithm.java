package br.udesc.dsd_token_ring_algorithm.algorithm;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import br.udesc.dsd_token_ring_algorithm.server.PlayerHandler;

public class TokenRingAlgorithm {
    private int currentPlayer;
    private List<PlayerHandler> players;
    private ReentrantLock lock;

    public TokenRingAlgorithm(int initialPlayer, int numPlayers, ReentrantLock lock) {
        this.currentPlayer = initialPlayer;
        this.lock = lock;
        initializePlayers(numPlayers);
    }

    private void initializePlayers(int numPlayers) {
        for (int i = 0; i < numPlayers; i++) {
            players.add(null);
        }
    }

    public void passToken() {
        lock.lock();
        try {
            currentPlayer = (currentPlayer + 1) % players.size();
            players.get(currentPlayer).setToken(true);
            players.get((currentPlayer - 1 + players.size()) % players.size()).setToken(false);
        } finally {
            lock.unlock();
        }
    }

    public void receiveToken() {
        lock.lock();
        try {
            players.get(currentPlayer).setToken(true);
        } finally {
            lock.unlock();
        }
    }
}

