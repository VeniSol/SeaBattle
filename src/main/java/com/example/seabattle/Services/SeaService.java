package com.example.seabattle.Services;

import com.example.seabattle.Model.Moves;
import com.example.seabattle.Model.Player;
import com.example.seabattle.Model.Ships;
import com.example.seabattle.Repositories.MovesRepository;
import com.example.seabattle.Repositories.PlayerRepository;
import com.example.seabattle.Repositories.ShipsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SeaService {
    @Autowired
    ShipsRepository shipsRepository;
    @Autowired
    MovesRepository movesRepository;
    public int[][] fieldForFirstPlayer = new int[10][10];
    public int[][] fieldForSecondPlayer = new int[10][10];
    @Autowired
    private PlayerRepository playerRepository;

    public boolean savingShipLocation(int shipSize, int orientation, int move_x, int move_y, Player player) {
        if (isActiveShip(shipSize, player)) {
            shipSize--;
            int end_x = move_x, end_y = move_y;
            if (orientation == 1) {
                end_x = move_x + shipSize;
                if (move_x + shipSize > 10)
                    return false;
            } else if (orientation == 2) {
                end_y = move_y + shipSize;
                if (move_y + shipSize > 10)
                    return false;
            }
            if (isBusyShipLocation(move_x, move_y, end_x, end_y, player)) return false;
            shipsRepository.save(new Ships(shipSize + 1, move_x, move_y, end_x, end_y, orientation, false, player));
            if (orientation == 1) {
                for (int x = move_x; x <= end_x; x++) {
                    if (player.getPlayerId() == 1) fieldForFirstPlayer[x][move_y] = 1;
                    if (player.getPlayerId() == 2) fieldForSecondPlayer[x][move_y] = 1;
                }
            } else if (orientation == 2) {
                for (int y = move_y; y <= end_y; y++) {
                    if (player.getPlayerId() == 1) fieldForFirstPlayer[move_x][y] = 1;
                    if (player.getPlayerId() == 2) fieldForSecondPlayer[move_x][y] = 1;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isActiveShip(int shipSize, Player player) {
        int[] ships = new int[]{4, 3, 2, 1};
        return shipsRepository.findAllByPlayerAndShipSize(player, shipSize).size() < ships[shipSize - 1];
    }

    private boolean isBusyShipLocation(int move_x, int move_y, int end_x, int end_y, Player player) {
        return shipsRepository.findShipByLocationAndPlayer(move_x, move_y, end_x, end_y, player) != null;
    }

    public boolean savingMovesLocation(int move_x, int move_y, Player player) {
        if (isBusyMoveLocation(move_x, move_y, player)) return false;
        movesRepository.save(new Moves(player, move_x, move_y));
        if (player.getPlayerId() == 1)
            if (fieldForSecondPlayer[move_x][move_y] == 1) {
                fieldForSecondPlayer[move_x][move_y] = 4;
                Player player2 = playerRepository.findByPlayerId(2);
                if (isShipSunk(move_x, move_y, player2)) {
                    PaintSunkShip(move_x, move_y, player2);
                }
            } else fieldForSecondPlayer[move_x][move_y] = 3;
        if (player.getPlayerId() == 2)
            if (fieldForFirstPlayer[move_x][move_y] == 1) {
                fieldForFirstPlayer[move_x][move_y] = 4;
                Player player2 = playerRepository.findByPlayerId(1);
                if (isShipSunk(move_x, move_y, player2)) {
                    PaintSunkShip(move_x, move_y, player2);
                }
            } else fieldForFirstPlayer[move_x][move_y] = 3;
        return true;
    }

    private boolean isBusyMoveLocation(int move_x, int move_y, Player player) {
        return movesRepository.findByPlayerAndMoveXAndMoveY(player, move_x, move_y) != null;
    }

    private boolean isShipSunk(int x, int y, Player player) {
        System.out.println(x);
        System.out.println(y);
        System.out.println(player.getPlayerId());
        Ships ship = shipsRepository.findShipByCellAndPlayer(x, y, player);
        if (ship.getOrientation() == 1) {
            for (int i = ship.getStartX(); i <= ship.getEndX(); i++) {
                if (player.getPlayerId() == 1) {
                    if (fieldForFirstPlayer[i][y] != 4) return false;
                } else if (player.getPlayerId() == 2) {
                    if (fieldForSecondPlayer[i][y] != 4) return false;
                }
            }
        }
        if (ship.getOrientation() == 2) {
            for (int j = ship.getStartY(); j <= ship.getEndY(); j++) {
                if (player.getPlayerId() == 1) {
                    if (fieldForFirstPlayer[x][j] != 4) return false;
                } else if (player.getPlayerId() == 2) {
                    if (fieldForSecondPlayer[x][j] != 4) return false;
                }
            }
        }
        return true;
    }

    private void PaintSunkShip(int x, int y, Player player) {
        Ships ship = shipsRepository.findShipByCellAndPlayer(x, y, player);
        if (ship.getOrientation() == 1) {
            for (int i = ship.getStartX() - 1; i <= ship.getEndX() + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    boolean checkX = i <= 10 && i >= 1;
                    boolean checkY = j <= 10 && j >= 1;
                    boolean checkShip = shipsRepository.findShipByCellAndPlayer(i, j, player) == null;
                    if (checkX && checkY && checkShip) {
                        if (player.getPlayerId() == 1) fieldForFirstPlayer[i][j] = 3;
                        else fieldForSecondPlayer[i][j] = 3;
                    }
                }
            }
        } else if (ship.getOrientation() == 2) {
            for (int i = ship.getStartY() - 1; i <= ship.getEndY() + 1; i++) {
                for (int j = x - 1; j <= x + 1; j++) {
                    boolean checkY = i <= 10 && i >= 1;
                    boolean checkX = j <= 10 && j >= 1;
                    boolean checkShip = shipsRepository.findShipByCellAndPlayer(j, i, player) == null;
                    if (checkX && checkY && checkShip) {
                        if (player.getPlayerId() == 1) fieldForFirstPlayer[j][i] = 3;
                        else fieldForSecondPlayer[j][i] = 3;
                    }
                }
            }
        }
    }

    public void randomShipGeneration() {
        for (int i = 1; i <= 2; i++) {
            Player player = playerRepository.findByPlayerId(i);
            int[] freeShips = new int[]{4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
            int shipSet = 0;
            int allShips = 10;
            while (shipSet <= allShips) {
                Random rnd = new Random();
                int rndX = rnd.nextInt(10);
                int rndY = rnd.nextInt(10);
                int rndOrientation = rnd.nextInt(1, 3);
                if (rndOrientation == 1 && rndX + freeShips[shipSet] <= 10 && checkOneCell(rndX, rndY, player)) {
                    savingShipLocation(freeShips[shipSet], 1, rndX, rndY, player);
                    shipSet++;

                } else if (rndOrientation == 2 && rndY + freeShips[shipSet] <= 10 && checkOneCell(rndX, rndY, player)) {
                    savingShipLocation(freeShips[shipSet], 2, rndX, rndY, player);
                    shipSet++;
                }

            }
        }
    }

    private boolean checkOneCell(int x, int y, Player player) {
        Ships ship = shipsRepository.findShipByCellAndPlayer(x, y, player);
        if (ship == null) return true;
        if (ship.getOrientation() == 1) {
            return presenceShip(x, y, player);
        } else {
            return presenceShip(y, x, player);
        }
    }

    private boolean presenceShip(int a, int b, Player player) {
        for (int i = a - 1; i <= a + 1; i++) {
            for (int j = b - 1; j <= b + 1; j++) {
                boolean checkX = i <= 10 && i >= 1;
                boolean checkY = j <= 10 && j >= 1;
                boolean checkShip = shipsRepository.findShipByCellAndPlayer(i, j, player) != null;
                if (player.getPlayerId() == 1 && checkX && checkY && checkShip) {
                    return false;
                } else if (player.getPlayerId() == 2 && checkX && checkY && checkShip) {
                    return false;
                }
            }
        }
        return true;
    }

}



