package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired//inyeccion de dependencia, saca objetos para usarlos donde se necesita
    private GameRepository gameRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private ShipRepository shipRepo;

    /*---------------------------------------------------------------------------------------*/

    @RequestMapping("/games")
    public Object getGameId(Authentication authentication) {
        Map<String, Object> gamesDTO = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            gamesDTO.put("player", "Guest");
        } else {
            gamesDTO.put("player", playerAuthenticated(authentication));
        }
        gamesDTO.put("games", gameRepo.findAll().stream().map(Game::getGameDTO).collect(Collectors.toList()));
        return gamesDTO;
    }

    private Object playerAuthenticated (Authentication authentication){
        Map<String, Object> playerDTO = new LinkedHashMap<>();
        playerDTO.put ("id", playerRepo.findByUserName(authentication.getName()).getId());
        playerDTO.put("email", playerRepo.findByUserName(authentication.getName()).getUserName());
        return playerDTO;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    /*--GAME VIEW-------------------------------------------------------------------------------------*/

    @RequestMapping("/game_view/{id}")
    public Object getGameView(@PathVariable long id, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepo.findOne(id);
            if(gamePlayer != null){
                if (gamePlayer.getPlayer().getUserName() == authentication.getName()) {
                    return gamePlayer.getGamePlayerGameView(gamePlayer);
                }
                return new ResponseEntity<>(this.makeMap("error", "Trying to sneak a peak? Shame on you!"), HttpStatus.UNAUTHORIZED);

            }else{

            return new ResponseEntity<>(this.makeMap("error", "NOT LOGGED!!"), HttpStatus.BAD_REQUEST);}
    }


    /*------LEADERBOARD---------------------------------------------------------------------------------*/

    @RequestMapping("/leaderBoard")
    public List<Object> getLeaderBoard (){
        List<Player> score = playerRepo.findAll();
        return score.stream()
                .map(Player::getPlayerScoresDTO)
                .collect(Collectors.toList());
    }

    /*------POST PLAYERS---------------------------------------------------------------------------------*/

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username, String password) {
        if (username.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Enter a name"), HttpStatus.BAD_REQUEST);
        }
        Player user = playerRepo.findByUserName(username);
        if (user != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        Player newUser = playerRepo.save(new Player(username, password));
        return new ResponseEntity<>(makeMap("id", newUser.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /*---------------------------------------------------------------------------------------*/

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame (Authentication authentication) {
        Player player = playerRepo.findByUserName((authentication.getName()));
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "NOT LOGGED"), HttpStatus.UNAUTHORIZED);
        } else if (player != null) // uso objeto creado para el if y no usar directamente el llamado
        {
            Game newGame = new Game(new Date());// objeto Date directo en constructor
            gameRepo.save(newGame);
            GamePlayer newGamePlayer = new GamePlayer(new Date(), newGame, player);
            gamePlayerRepo.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(makeMap("error", "Not exists"), HttpStatus.NOT_FOUND);

    }

    /*---------------------------------------------------------------------------------------*/

    @RequestMapping(path = "/game/{id}/players", method = RequestMethod.POST)
    public Object joinGames(@PathVariable long id){
        Player playerAuth = getAuthPlayer();

        if (playerAuth == null){
            return new ResponseEntity<>(makeMap("error", "Not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Game game = gameRepo.findOne(id);
        if (game == null){
            return new ResponseEntity<>(makeMap("error", "Game no exists"), HttpStatus.FORBIDDEN);
        }

        if (game.getPlayers().size() == 2){
            return new ResponseEntity<>(makeMap("error", "Game full"), HttpStatus.FORBIDDEN);
        }

        Date newDate = new Date();
        GamePlayer newGamePlayer = new GamePlayer(newDate, game, playerAuth);
        gamePlayerRepo.save(newGamePlayer);
        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);

    }

    private Player getAuthPlayer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return playerRepo.findByUserName(authentication.getName());
        }
    }

    /*----SHIPS GET-----------------------------------------------------------------------------------*/

    @RequestMapping(value="/games/players/{id}/ships", method=RequestMethod.GET)
    public Object getShipLocations(@PathVariable long id){
        Map<String, Object> shipPlacementDTO = new LinkedHashMap<>();

        GamePlayer gamePlayer = gamePlayerRepo.findById(id);
        Player playerAuth = getAuthPlayer();

        if (playerAuth == null || playerAuth.getId() != gamePlayer.getPlayer().getId()){
            return new ResponseEntity<>(makeMap("error", "ARRRRRR! Not Authorized"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer == null){
            return new ResponseEntity<>(makeMap("error", "Sailor not found SIR!"), HttpStatus.UNAUTHORIZED);
        }

        shipPlacementDTO.put("ships", gamePlayer.getGamePlayerShipsDTO());
        shipPlacementDTO.put("gpid", gamePlayer.getId());
        return shipPlacementDTO;

    }

    /*---SHIPS POST------------------------------------------------------------------------------------*/

    @RequestMapping(path = "/games/players/{id}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> setShips(@PathVariable long id, @RequestBody List<Ship> ships) {

        GamePlayer gamePlayer = gamePlayerRepo.findById(id);
        Player playerAuth = getAuthPlayer();
        if (gamePlayer == null){
            if (playerAuth == null || playerAuth.getId() != gamePlayer.getPlayer().getId()){
                return new ResponseEntity<>(makeMap("error", "ARRRRRR! Not Authorized"), HttpStatus.UNAUTHORIZED);
            }

            if (gamePlayer.getShipSet().size() >= 5) {
                return new ResponseEntity<>(makeMap("error", "Aye capt', fleet complete!"), HttpStatus.FORBIDDEN);
            }

            gamePlayer.addShips(ships);
            gamePlayerRepo.save(gamePlayer);

            return new ResponseEntity<>(makeMap("Success", "Ships created master chief"),HttpStatus.CREATED);

        }
        return new ResponseEntity<>(makeMap("error", "ARRRRRR! Not Authorized"), HttpStatus.UNAUTHORIZED);
    }

    /*----SALVO GET-----------------------------------------------------------------------------------*/

    @RequestMapping(value="/games/players/{id}/salvoes", method=RequestMethod.GET)
    public Object getSalvos (@PathVariable long id){
        Map<String, Object> salvoLocationsDTO = new LinkedHashMap<>();

        GamePlayer gamePlayer = gamePlayerRepo.findById(id);
        Player playerAuth = getAuthPlayer();

        if (playerAuth == null || playerAuth.getId() != gamePlayer.getPlayer().getId()){
            return new ResponseEntity<>(makeMap("error", "ARRRRRR! Not Authorized"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer == null){
            return new ResponseEntity<>(makeMap("error", "Sailor not found SIR!"), HttpStatus.UNAUTHORIZED);
        }

        salvoLocationsDTO.put("salvos", gamePlayer.getGamePlayerSalvoesDTO());
        salvoLocationsDTO.put("gpid", gamePlayer.getId());
        return salvoLocationsDTO;

    }

    /*----SALVO POST-----------------------------------------------------------------------------------*/

    @RequestMapping(path = "/games/players/{id}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> setSalvoes(@PathVariable long id, @RequestBody Salvo salvo) {

        GamePlayer gamePlayer = gamePlayerRepo.findById(id);
        Player playerAuth = getAuthPlayer();
        if (gamePlayer == null){
            if (playerAuth == null || playerAuth.getId() != gamePlayer.getPlayer().getId()){
                return new ResponseEntity<>(makeMap("error", "ARRRRRR! Not Authorized"), HttpStatus.UNAUTHORIZED);
            }

            if (gamePlayer.getSalvoSet().size() >= 5) {//posible cagada
                return new ResponseEntity<>(makeMap("error", "Aye capt', rounds completed!"), HttpStatus.FORBIDDEN);
            }

            gamePlayer.addSalvo(salvo);
            gamePlayerRepo.save(gamePlayer);

            return new ResponseEntity<>(makeMap("Success", "FIRE!!"),HttpStatus.CREATED);

        }
        return new ResponseEntity<>(makeMap("error", "ARRRRRR! Not Authorized"), HttpStatus.UNAUTHORIZED);
    }







}