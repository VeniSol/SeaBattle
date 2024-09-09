package com.example.seabattle.Repositories;

import com.example.seabattle.Model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Player findByPlayerId(int id);
    Player findByPlayerName(String name);
}
