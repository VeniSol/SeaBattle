package com.example.seabattle;

import com.example.seabattle.Model.Player;
import com.example.seabattle.Repositories.PlayerRepository;
import com.example.seabattle.Repositories.ShipsRepository;
import com.example.seabattle.Services.SeaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

@SpringBootTest
class SeaBattleApplicationTests {
    @Autowired
    SeaService seaService;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ShipsRepository shipsRepository;
    @Test
    void saveShip() {
        shipsRepository.deleteAll();
        Player player = playerRepository.findByPlayerName("Cyxaruk");
        boolean isSave = seaService.savingShipLocation(4,2,1,1,player);
        Assertions.assertTrue(isSave);
    }
    @Test
    void setShip()
    {
        shipsRepository.deleteAll();
        Player player = playerRepository.findByPlayerName("Cyxaruk");
        Player player2 = playerRepository.findByPlayerName("Boda");

        seaService.savingShipLocation(1,2,1,1,player);
        seaService.savingMovesLocation(1,1,player2);
        seaService.PaintSunkShip(1,1,player);
        for (int i=0;i<10;i++){
            for (int j=0;j<10;j++){
                System.out.println(seaService.fieldForFirstPlayer[i][j]);
            }
        }


    }
}
