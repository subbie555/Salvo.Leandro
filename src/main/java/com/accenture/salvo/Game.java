package com.accenture.salvo;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collector;


import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

// en base a la clase person se pide que se agregue el userName a la clase Player
@Entity// Crear tabla Game en la BD---Entity class is equivalent to a row of a database
public class Game {

    @Id// Indica a la BD que la variable id es la que contiene la PK de la tabla
    @GeneratedValue(strategy=GenerationType.AUTO)// Para que el DBMS le mande la ID a JPA
    private long id;//declaracion de la variable id, lo asigna automatico a la tabla Id de la BD
    private Date creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayerSet;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> scoreSet;

    public Game() { }

    public Game(Date date) {
        this.creationDate = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<GamePlayer> getGamePlayerSet() {
        return gamePlayerSet;
    }

    public void setGamePlayerSet(Set<GamePlayer> gamePlayerSet) {
        this.gamePlayerSet = gamePlayerSet;
    }

    public Set<Score> getScoreSet() {
        return scoreSet;
    }

    public void setScoreSet(Set<Score> scoreSet) {
        this.scoreSet = scoreSet;
    }

    public List<Player> getPlayers() {
        return gamePlayerSet.stream().map(sub -> sub.getPlayer()).collect(toList());
    }

    public List<Object> getGamePlayerFromGameDTO(){
        return gamePlayerSet.stream()
                .flatMap(gp -> gp.getSalvoSet()
                        .stream().map(salvo -> salvo.salvoToDTO())).collect(toList());
    }

    public Object getGamesScores(){
        return scoreSet.stream().map(score -> score.getScoresDTO()).collect(toList());
    }

    public Object getGamePlayerThisGame (){
        return gamePlayerSet.stream().map(gamePlayer -> gamePlayer.getGamePlayersDTO()).collect(toList());
    }

    public Set<GamePlayer> getGamePlayers (){
        return this.gamePlayerSet;
    }

    public Map<String, Object> getGameDTO (){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("created", this.creationDate);
        dto.put("gamePlayers", getGamePlayerThisGame());
        dto.put("scores", getGamesScores());
        return dto;
    }



}

