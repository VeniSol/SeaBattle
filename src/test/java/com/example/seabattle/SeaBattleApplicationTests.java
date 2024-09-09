package com.example.seabattle;

import com.example.seabattle.Model.Player;
import com.example.seabattle.Repositories.PlayerRepository;
import com.example.seabattle.Repositories.ShipsRepository;
import com.example.seabattle.Services.SeaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
