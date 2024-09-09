package com.example.seabattle.Services;

import com.example.seabattle.Model.Fields;
import com.example.seabattle.Model.Moves;
import com.example.seabattle.Model.Player;
import com.example.seabattle.Model.Ships;
import com.example.seabattle.Repositories.MovesRepository;
import com.example.seabattle.Repositories.ShipsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeaService {
    @Autowired
    ShipsRepository shipsRepository;
    @Autowired
    MovesRepository movesRepository;
    int[][] fieldForFirstPlayer = new int[10][10];
    int[][] fieldForSecondPlayer = new int[10][10];

    public boolean savingShipLocation(int shipSize, int orientation, int move_x, int move_y, Player player) {
        if (isActiveShip(shipSize, player) ) {
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
            if(isBusyShipLocation(move_x,move_y,end_x,end_y,player))return false;
            shipsRepository.save(new Ships(shipSize + 1, move_x, move_y, end_x, end_y, false, player));
            return true;
        }
        return false;
    }

    private boolean isActiveShip(int shipSize, Player player) {
        int[] ships = new int[]{4, 3, 2, 1};
        return shipsRepository.findAllByPlayerAndShipSize(player, shipSize).size() < ships[shipSize-1];
    }

    private boolean isBusyShipLocation(int move_x,int move_y,int end_x,int end_y,Player player) {
        return shipsRepository.findShipByLocationAndPlayer(move_x,move_y,end_x,end_y,player)!=null;
    }

    public boolean savingMovesLocation(int move_x, int move_y,Player player) {
        if(isBusyMoveLocation(move_x,move_y,player)) return false;
        movesRepository.save(new Moves(player,move_x, move_y));
        return true;
    }

    private boolean isBusyMoveLocation(int move_x,int move_y,Player player) {
        return movesRepository.findByPlayerAndMoveXAndMoveY(player,move_x,move_y)!=null;
    }

    public Fields generateFields(){

    }
}
