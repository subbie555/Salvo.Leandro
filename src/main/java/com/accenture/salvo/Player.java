package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

// en base a la clase person se pide que se agregue el userName a la clase Player
@Entity//para trabajar con BD
public class Player {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)//configuracion del hibernate
    private long id;//declaracion de la variable id, lo asigna automatico a la tabla Id de la BD

    private String userName;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayerSet;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scoreSet;

    public Player() { }

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Set<GamePlayer> getGamePlayerSet() {
        return gamePlayerSet;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayerSet.add(gamePlayer);
    }
    @JsonIgnore
    public List<Game> getGames() {
        return gamePlayerSet.stream().map(sub -> sub.getGame()).collect(toList());
    }

    public long getId() {
        return id;
    }

    public String getUserName (){
        return this.userName;
    }

    public void setUserName (String userName){
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Score> getScoreSet(){
        return scoreSet;
    }

    public List<Object> getScore() {
        return scoreSet.stream().filter(s -> s.getScore() != -1).collect(toList());
    }

    public Map<String, Object> getPlayerDTO (){//metodo para obtener id y email de player
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("email", this.userName);
        return dto;
    }

    public double getTotalPoints(){
        return scoreSet.stream().filter(score -> score.getScore() != -1).mapToDouble(score -> score.getScore()).sum();
    }

    public double getTotalWins(){
        return scoreSet.stream().filter(score -> score.getScore() == 1).count();
    }

    public double getTotalLost(){
        return scoreSet.stream().filter(score -> score.getScore() == 0).count();
    }

    public double getTotalTies(){
        return scoreSet.stream().filter(score -> score.getScore() == 0.5).count();
    }

    public Map<String, Object> getAllPlayerScores(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("total", getTotalPoints());
        dto.put("won", getTotalWins());
        dto.put("lost", getTotalLost());
        dto.put("tied", getTotalTies());
        return dto;
    }

    public Map<String, Object> getPlayerScoresDTO (){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("name", this.userName);
        dto.put("score", this.getAllPlayerScores());
        return dto;
    }
}