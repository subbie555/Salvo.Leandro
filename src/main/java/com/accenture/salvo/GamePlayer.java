
package com.accenture.salvo;


import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private Date joinDate;
    private GameState gameState;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany (mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)//referencia del padre en clase hija
    Set<Ship> shipSet;//propiedad de gameplayer tiene muchos ships

    @OneToMany (mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Salvo> salvoSet;

    public GamePlayer() { }//lo necesita JPA, Spring usa getters y setters

    public GamePlayer (Date date, Game game, Player player) {
        this.joinDate = date;
        this.game = game;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getShipSet() {
        return shipSet;
    }

    public Set<Salvo> getSalvoSet() {
        return salvoSet;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void addShips (List<Ship> ships){
        ships.forEach(ship -> {
          ship.setGamePlayer(this);
          shipSet.add(ship);
        });
    }

    public void addSalvo (Salvo salvo){
            salvo.setGamePlayer(this);
            salvoSet.add(salvo);
    }

    public void setShipSet(Set<Ship> shipSet) {
        this.shipSet = shipSet;
    }

    public List<Object> getGamePlayerShipsDTO(){
        return shipSet.stream()
                .map(s -> s.shipToDTO())
                .collect(toList());
    }

    public List<Object> getGamePlayerSalvoesDTO(){
        return salvoSet.stream()
                .map(s -> s.salvoToDTO())
                .collect(toList());
    }

    public void setSalvoSet(Set<Salvo> salvoSet) {
        this.salvoSet = salvoSet;
    }

    public Map<String, Object> getGamePlayersDTO (){
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", this.id);
            dto.put("player", player.getPlayerDTO());
            dto.put("joinDate", this.joinDate);
            return dto;
    }

    public Map<String, Object> getGamePlayerGameView(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gameState", setGameState());
        dto.put("gamePlayers", game.getGamePlayerThisGame());
        dto.put("ships", getGamePlayerShipsDTO());
        dto.put("salvoes", game.getGamePlayerFromGameDTO());
        dto.put("hits", HitsListPLayers(gamePlayer, gamePlayer.getOpponent()));
        return dto;
    }


    private GameState setGameState (){ //usar eventos para hacer este control
        if (getOpponent() == null){
            return this.gameState = GameState.WAITINGFOROPP;
        }
        if (getSalvoSet().size() == 5){
            return this.gameState = GameState.WAIT;
        }
        if (getShipSet() != null){
            return this.gameState = GameState.PLAY;
        }
        if (getShipSet() == null){
            return this.gameState = GameState.PLACESHIPS;
        }
        if (getOpponent().getShipSet().size() == 0){
            return this.gameState = GameState.WON;
        }
        if (getShipSet().size() == 0){
            return this.gameState = GameState.LOST;
        }
        if (getShipSet().size() == 0 && getOpponent().getShipSet().size() == 0){
            return this.gameState = GameState.TIE;
        }
        return this.gameState = GameState.UNDEFINED;
    }

    public GamePlayer getOpponent(){ // devuelve el gameplayer contrario
        return game.getGamePlayers().stream().filter(gp -> gp.getPlayer().getId() != this.getPlayer().getId()).findFirst().orElse(null);
    }

    public GamePlayer getThisGameplayer(){ //devuelve el gameplayer usuario
        return game.getGamePlayers().stream().filter(gp -> gp.getPlayer().getId() == this.getPlayer().getId()).findFirst().get();
    }

    public Map<String, Object> HitsListPLayers(GamePlayer gamePlayer, GamePlayer opponent){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (getOpponent() == null)
            dto.put("self", hitsList(new HashSet<Salvo>(), gamePlayer));
        else
            dto.put("self", hitsList(opponent.getSalvoSet(), gamePlayer));
        dto.put("opponent", hitsList(gamePlayer.getSalvoSet(), opponent));
        return dto;

    }

    public List<Object> hitsList(Set<Salvo> salvoes, GamePlayer gamePlayer){
        List<Object> list= new ArrayList<>();
        for (int i = 0; i<salvoes.size(); i++) {
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            int turno = 1 + i;

            if (turno == 1){
                Salvo salvo = salvoes.stream().filter(s -> s.getTurn() == turno).findFirst().get();
                dto.put("turn", salvo.getTurn());
                dto.put("hitLocations", salvo.getSalvoLocations());
                dto.put("damages", viewDamageDTO(salvo,gamePlayer,null));
                dto.put("missed", hitsMissed(salvo.getSalvoLocations().size(),createShipsStatusMap()));
            }
            else {
                Salvo anterior = salvoes.stream().filter(s -> s.getTurn() == turno - 1).findFirst().get();
                Salvo actual = salvoes.stream().filter(s -> s.getTurn() == turno).findFirst().get();
                dto.put("turn", actual.getTurn());
                dto.put("hitLocations", actual.getSalvoLocations());
                dto.put("damages", viewDamageDTO(actual, gamePlayer,anterior));
                dto.put("missed", hitsMissed(actual.getSalvoLocations().size(),createShipsStatusMap()));
            }
            list.add(dto);
        }
        return list;
    }

    private Map<String, Object> viewDamageDTO(Salvo salvo, GamePlayer gamePlayer, Salvo salvoAnterior) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        for (Ship ship:gamePlayer.getShipSet()) {

            if (salvoAnterior == null){
                dto.put(ship.getShip() + "Hits", salvo.hits(ship));
                dto.put(ship.getShip(), salvo.hits(ship));
            }
            else {
                dto.put(ship.getShip() + "Hits", salvo.hits(ship));
                dto.put(ship.getShip(), salvo.hits(ship) + salvoAnterior.hits(ship));
            }

        }
        return dto;
    }

    public long hitsMissed(int numberSalvos, Map<String,Integer> shipsStatusMap) {
        for (String key: shipsStatusMap.keySet()) {
            if (key.contains("Hits")) {
                numberSalvos = numberSalvos - shipsStatusMap.get(key);
            }
        }
        return numberSalvos;
    }

    private Map<String,Integer> createShipsStatusMap() {
        Map<String,Integer> shipsStatusMap = new LinkedHashMap<>();
        shipsStatusMap.put("carrier",0);
        shipsStatusMap.put("battleship",0);
        shipsStatusMap.put("submarine",0);
        shipsStatusMap.put("destroyer",0);
        shipsStatusMap.put("patrolboat",0);
        shipsStatusMap.put("carrierHits",0);
        shipsStatusMap.put("battleshipHits",0);
        shipsStatusMap.put("submarineHits",0);
        shipsStatusMap.put("destroyerHits",0);
        shipsStatusMap.put("patrolboatHits",0);
        return shipsStatusMap;
    }




}
