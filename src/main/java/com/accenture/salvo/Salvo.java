package com.accenture.salvo;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int turn;
    private String salvo;

    @ManyToOne(fetch = FetchType.EAGER)//tipo de relacion
    @JoinColumn(name="gameplayerID")//nombre columna FK
    private GamePlayer gamePlayer;//entidada c que se relaciona

    @ElementCollection
    @Column(name="salvoLocations")
    private List<String> salvoLocations;

    public Salvo (){

    }

    public Salvo (int turn, GamePlayer gamePlayer, List<String> salvoLocations){
        this.turn = turn;
        this.gamePlayer = gamePlayer;
        this.salvoLocations = salvoLocations;

    }

    public String getSalvo() {
        return salvo;
    }

    public void setSalvo(String salvo) {
        salvo = salvo;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> salvoToDTO (){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("player", gamePlayer.getPlayer().getId());//gameplayers del game
        dto.put("locations", this.salvoLocations);
        return dto;
    }

    public int hits(Ship ship) {
        int hit = 0;
        for (int i = 0; i < this.getSalvoLocations().size(); ++i) {
            for (int j = 0; j < ship.getLocations().size(); ++j) {
                if (this.getSalvoLocations().get(i) == ship.getLocations().get(j)) {
                    hit  = +1; }
            }
        }
        return hit;
    }

}
