package com.accenture.salvo;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    //public enum shipType{Aircraft_carrier, Battleship, Submarine, Destroyer, Patrol_Boat}

    private String ship;



    @ManyToOne(fetch = FetchType.EAGER)//tipo de relacion
    @JoinColumn(name="gameplayerID")//nombre columna FK
    private GamePlayer gamePlayer;//entidada c que se relaciona

    @ElementCollection
    @Column(name="locations")
    private List<String> locations;

    public Ship(){
    }

    public Ship (String ship, GamePlayer gamePlayer, List<String> locations){
        this.ship = ship;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShip() {
        return ship;
    }

    public void setShip(String ship) {
        this.ship = ship;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public Map<String, Object> shipToDTO (){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.ship);
        dto.put("locations", this.locations);
        return dto;
    }


}
