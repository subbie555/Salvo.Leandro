package com.accenture.salvo;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity

public class Score {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private Date finishDate;
    private double score;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    public Score (){

    }

    public Score (Game game, Player player, double score){
        this.game = game;
        this.player = player;
        if (score != -1){
            this.score = score;
            this.finishDate = Date.from(game.getCreationDate().toInstant().plusSeconds(1800));
        }else{
            this.finishDate = null;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public double getScore() {
        return this.score;
    }

    public void setScore(double score) {
        this.score = score;
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

    public Map<String, Object> getScoresDTO(){
        Map<String, Object> scoreMap = new LinkedHashMap<>();
        scoreMap.put("playerID", this.id);
        scoreMap.put("score", this.score);
        scoreMap.put("finishDate", this.finishDate);
        return scoreMap;
    }
}
