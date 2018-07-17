package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);

	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of customers
			Player JBauer = new Player("j.bauer@ctu.gov", "24");
			Player CObrian = new Player("c.obrian@ctu.gov", "42");
			Player KBauer = new Player("kim_bauer@gmail.com", "kb");
			Player TAlmeida = new Player("t.almeida@ctu.gov", "mole");
			//Player DPalmer = new Player("David", "Palmer", "DPal@yahoo.com");
			//Player MDessler = new Player("Michelle", "Dessler", "MDess@gmail.com");

			playerRepository.save(JBauer);
			playerRepository.save(CObrian);
			playerRepository.save(KBauer);
			playerRepository.save(TAlmeida);
			//playerRepository.save(DPalmer);
			//playerRepository.save(MDessler);

			Date date1 = new Date();
			Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
			Date date3 = Date.from(date2.toInstant().plusSeconds(3600));
			Date date4 = Date.from(date3.toInstant().plusSeconds(3600));
			Date date5 = Date.from(date4.toInstant().plusSeconds(3600));
			Date date6 = Date.from(date5.toInstant().plusSeconds(3600));
			Date date7 = Date.from(date6.toInstant().plusSeconds(3600));
			Date date8 = Date.from(date7.toInstant().plusSeconds(3600));

			Game game1 = new Game(date1);// Game tiene ID y fecha inicio
			Game game2 = new Game(date2);
			Game game3 = new Game(date3);
			Game game4 = new Game(date4);
			Game game5 = new Game(date5);
			Game game6 = new Game(date6);
			Game game7 = new Game(date7);
			Game game8 = new Game(date8);

			gameRepository.save(game1); // se guardan en el repositorio
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);

			GamePlayer gamePlayer1 = new GamePlayer(date1, game1, JBauer);
			GamePlayer gamePlayer2 = new GamePlayer(date1, game1, CObrian);

			GamePlayer gamePlayer3 = new GamePlayer(date2, game2, JBauer);
			GamePlayer gamePlayer4 = new GamePlayer(date2, game2, CObrian);

			GamePlayer gamePlayer5 = new GamePlayer(date3, game3, CObrian);
			GamePlayer gamePlayer6 = new GamePlayer(date3, game3, TAlmeida);

			GamePlayer gamePlayer7 = new GamePlayer(date4, game4, CObrian);
			GamePlayer gamePlayer8 = new GamePlayer(date4, game4, JBauer);

			GamePlayer gamePlayer9 = new GamePlayer(date5, game5, TAlmeida);
			GamePlayer gamePlayer10 = new GamePlayer(date5, game5, JBauer);

			GamePlayer gamePlayer11 = new GamePlayer(date6, game6, KBauer);

			GamePlayer gamePlayer12 = new GamePlayer(date7, game7, TAlmeida);

			GamePlayer gamePlayer13 = new GamePlayer(date8, game8, KBauer);
			GamePlayer gamePlayer14 = new GamePlayer(date8, game8, TAlmeida);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);
			gamePlayerRepository.save(gamePlayer7);
			gamePlayerRepository.save(gamePlayer8);
			gamePlayerRepository.save(gamePlayer9);
			gamePlayerRepository.save(gamePlayer10);
			gamePlayerRepository.save(gamePlayer11);
			gamePlayerRepository.save(gamePlayer12);
			gamePlayerRepository.save(gamePlayer13);
			gamePlayerRepository.save(gamePlayer14);

			//naves j.bauer en game1
			List<String> shipPos1 = Arrays.asList("H2", "H3", "H4");
			List<String> shipPos2 = Arrays.asList("E1", "F1", "G1");
			List<String> shipPos3 = Arrays.asList("B4", "B5");
			Ship ship1 = new Ship("destroyer", gamePlayer1, shipPos1);
			Ship ship2 = new Ship("submarine", gamePlayer1, shipPos2);
			Ship ship3 = new Ship("patrolboat", gamePlayer1,  shipPos3);
			//naves c.obrian en game1
			List<String> shipPos4 = Arrays.asList("B5", "C5", "D5");
			List<String> shipPos5 = Arrays.asList("F1", "F2");
			Ship ship4 = new Ship("destroyer", gamePlayer2, shipPos4);
			Ship ship5 = new Ship("patrolboat", gamePlayer2, shipPos5);

			//naves j.bauer en game2
			List<String> shipPos6 = Arrays.asList("B5", "C5", "D5");
			List<String> shipPos7 = Arrays.asList("C6", "C7");
			Ship ship6 = new Ship("destroyer", gamePlayer3, shipPos6);
			Ship ship7 = new Ship("patrolboat", gamePlayer3, shipPos7);
			//naves c.obrian en game2
			List<String> shipPos8 = Arrays.asList("A2", "A3", "A4");
			List<String> shipPos9 = Arrays.asList("G6", "H6");
			Ship ship8 = new Ship("destroyer", gamePlayer4, shipPos8);
			Ship ship9 = new Ship("patrolboat", gamePlayer4, shipPos9);

			//naves c.obrian game3
			List<String> shipPos10 = Arrays.asList("B5", "C5", "D5");
			List<String> shipPos11 = Arrays.asList("C6", "C7");
			Ship ship10 = new Ship("destroyer", gamePlayer5, shipPos10);
			Ship ship11 = new Ship("patrolboat", gamePlayer5, shipPos11);
			//naves t.almeida game3
			List<String> shipPos12 = Arrays.asList("A2", "A3", "A4");
			List<String> shipPos13 = Arrays.asList("G6", "H6");
			Ship ship12 = new Ship("destroyer", gamePlayer6, shipPos12);
			Ship ship13 = new Ship("submarine", gamePlayer6, shipPos13);

			//naves c.obrian game4
			List<String> shipPos14 = Arrays.asList("B5", "C5", "D5");
			List<String> shipPos15 = Arrays.asList("C6", "C7");
			Ship ship14 = new Ship("destroyer", gamePlayer7, shipPos14);
			Ship ship15 = new Ship("patrolboat", gamePlayer7, shipPos15);
			//naves j.bauer game4
			List<String> shipPos16 = Arrays.asList("A2", "A3", "A4");
			List<String> shipPos17 = Arrays.asList("G6", "H6");
			Ship ship16 = new Ship("submarine", gamePlayer8, shipPos16);
			Ship ship17 = new Ship("patrolboat", gamePlayer8, shipPos17);

			//naves t.almeida game5
			List<String> shipPos18 = Arrays.asList("B5", "C5", "D5");
			List<String> shipPos19 = Arrays.asList("C6", "C7");
			Ship ship18 = new Ship("Destroyer", gamePlayer9, shipPos18);
			Ship ship19 = new Ship("Patrol Boat", gamePlayer9, shipPos19);
			//naves j.bauer game5
			List<String> shipPos20 = Arrays.asList("A2", "A3", "A4");
			List<String> shipPos21 = Arrays.asList("G6", "H6");
			Ship ship20 = new Ship("submarine", gamePlayer10, shipPos20);
			Ship ship21 = new Ship("patrolboat", gamePlayer10, shipPos21);

			//naves k.bauer game6
			List<String> shipPos22 = Arrays.asList("B5", "C5", "D5");
			List<String> shipPos23 = Arrays.asList("C6", "C7");
			Ship ship22 = new Ship("destroyer", gamePlayer11, shipPos22);
			Ship ship23 = new Ship("patrolboat", gamePlayer11, shipPos23);

			// naves k.bauer game 8
			List<String> shipPos24 = Arrays.asList("B5", "C5", "D5");
			List<String> shipPos25 = Arrays.asList("C6", "C7");
			Ship ship24 = new Ship("destroyer", gamePlayer13, shipPos24);
			Ship ship25 = new Ship("patrolboat", gamePlayer13, shipPos25);
			// naves t.almeida game 8
			List<String> shipPos26 = Arrays.asList("A2", "A3", "A4");
			List<String> shipPos27 = Arrays.asList("G6", "H6");
			Ship ship26 = new Ship("submarine", gamePlayer14, shipPos26);
			Ship ship27 = new Ship("patrolboat", gamePlayer14, shipPos27);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);
			shipRepository.save(ship10);
			shipRepository.save(ship11);
			shipRepository.save(ship12);
			shipRepository.save(ship13);
			shipRepository.save(ship14);
			shipRepository.save(ship15);
			shipRepository.save(ship16);
			shipRepository.save(ship17);
			shipRepository.save(ship18);
			shipRepository.save(ship19);
			shipRepository.save(ship20);
			shipRepository.save(ship21);
			shipRepository.save(ship22);
			shipRepository.save(ship23);
			shipRepository.save(ship24);
			shipRepository.save(ship25);
			shipRepository.save(ship26);
			shipRepository.save(ship27);

			// tiros game1
			List<String> salvoPos1 = Arrays.asList("B5", "C5", "F1");
			List<String> salvoPos2 = Arrays.asList("F2", "D5");
			Salvo salvo1 = new Salvo(1, gamePlayer1, salvoPos1);
			Salvo salvo2 = new Salvo(2, gamePlayer1, salvoPos2);
			List<String> salvoPos3 = Arrays.asList("B4", "B5", "B6");
			List<String> salvoPos4 = Arrays.asList("E1", "H3", "A2");
			Salvo salvo3 = new Salvo(1, gamePlayer2, salvoPos3);
			Salvo salvo4 = new Salvo(2, gamePlayer2, salvoPos4);

			//tiros game 2
			List<String> salvoPos5 = Arrays.asList("A2", "A4", "G6");
			List<String> salvoPos6 = Arrays.asList("A3", "H6");
			Salvo salvo5 = new Salvo(1, gamePlayer3, salvoPos5);
			Salvo salvo6 = new Salvo(2, gamePlayer3, salvoPos6);
			List<String> salvoPos7 = Arrays.asList("B5", "D5", "D7");
			List<String> salvoPos8 = Arrays.asList("C5", "C6");
			Salvo salvo7 = new Salvo(1, gamePlayer4, salvoPos7);
			Salvo salvo8 = new Salvo(2, gamePlayer4, salvoPos8);

			//tiros game3
			List<String> salvoPos9 = Arrays.asList("G6", "H6", "A4");
			List<String> salvoPos10 = Arrays.asList("A2", "A3","D8");
			Salvo salvo9 = new Salvo(1, gamePlayer5, salvoPos9);
			Salvo salvo10 = new Salvo(2, gamePlayer5, salvoPos10);
			List<String> salvoPos11 = Arrays.asList("B5", "D5", "D7");
			List<String> salvoPos12 = Arrays.asList("C5", "C6");
			Salvo salvo11 = new Salvo(1, gamePlayer6, salvoPos11);
			Salvo salvo12 = new Salvo(2, gamePlayer6, salvoPos12);

			//tiros game4
			List<String> salvoPos13 = Arrays.asList("A3", "A4", "F7");
			List<String> salvoPos14 = Arrays.asList("A2", "G6","H6");
			Salvo salvo13 = new Salvo(1, gamePlayer7, salvoPos13);
			Salvo salvo14 = new Salvo(2, gamePlayer7, salvoPos14);
			List<String> salvoPos15 = Arrays.asList("B5", "C6", "H1");
			List<String> salvoPos16 = Arrays.asList("C5", "C7", "D5");
			Salvo salvo15 = new Salvo(1, gamePlayer8, salvoPos15);
			Salvo salvo16 = new Salvo(2, gamePlayer8, salvoPos16);

			//tiros game5
			List<String> salvoPos17 = Arrays.asList("A1", "A2", "A3");
			List<String> salvoPos18 = Arrays.asList("G6", "G7","G8");
			Salvo salvo17 = new Salvo(1, gamePlayer9, salvoPos17);
			Salvo salvo18 = new Salvo(2, gamePlayer9, salvoPos18);
			List<String> salvoPos19 = Arrays.asList("B5", "B6", "C7");
			List<String> salvoPos20 = Arrays.asList("C6", "D6", "E6");
			List<String> salvoPos21 = Arrays.asList("H1", "H8");
			Salvo salvo19 = new Salvo(1, gamePlayer10, salvoPos19);
			Salvo salvo20 = new Salvo(2, gamePlayer10, salvoPos20);
			Salvo salvo21 = new Salvo(3, gamePlayer10, salvoPos21);


			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
			salvoRepository.save(salvo5);
			salvoRepository.save(salvo6);
			salvoRepository.save(salvo7);
			salvoRepository.save(salvo8);
			salvoRepository.save(salvo9);
			salvoRepository.save(salvo10);
			salvoRepository.save(salvo11);
			salvoRepository.save(salvo12);
			salvoRepository.save(salvo13);
			salvoRepository.save(salvo14);
			salvoRepository.save(salvo15);
			salvoRepository.save(salvo16);
			salvoRepository.save(salvo17);
			salvoRepository.save(salvo18);
			salvoRepository.save(salvo19);
			salvoRepository.save(salvo20);
			salvoRepository.save(salvo21);

			Score scoreJBauer1 = new Score (game1, JBauer, 1);
			Score scoreCobrian1 = new Score (game1, CObrian, 0);
			Score scoreJBauer2 = new Score (game2, JBauer, 0.5);
			Score scoreCobrian2 = new Score (game2, CObrian, 0.5);
			Score scoreCobrian3 = new Score (game3, CObrian, 1);
			Score scoreTAlmeida1 = new Score (game3, TAlmeida, 0);
			Score scoreCobrian4 = new Score (game4, CObrian, 0.5);
			Score scoreJBauer3 = new Score (game4, JBauer, 0.5);
			Score scoreJBauer4 = new Score (game5, JBauer, -1);
			Score scoreTAlmeida2 = new Score (game5, TAlmeida, -1);
			Score scoreKBauer1 = new Score (game6, KBauer, -1);
			Score scoreNULL1 = new Score (game6, null, -1);
			Score scoreTAlmeida3 = new Score (game7, TAlmeida, -1);
			Score scoreNULL2 = new Score (game7, null, -1);
			Score scoreKBauer2 = new Score (game8, KBauer, -1);
			Score scoreTAlmeida4 = new Score (game8, TAlmeida, -1);

			scoreRepository.save(scoreJBauer1);
			scoreRepository.save(scoreCobrian1);
			scoreRepository.save(scoreJBauer2);
			scoreRepository.save(scoreCobrian2);
			scoreRepository.save(scoreCobrian3);
			scoreRepository.save(scoreTAlmeida1);
			scoreRepository.save(scoreCobrian4);
			scoreRepository.save(scoreJBauer3);
			scoreRepository.save(scoreJBauer4);
			scoreRepository.save(scoreTAlmeida2);
			scoreRepository.save(scoreKBauer1);
			scoreRepository.save(scoreNULL1);
			scoreRepository.save(scoreTAlmeida3);
			scoreRepository.save(scoreNULL2);
			scoreRepository.save(scoreKBauer2);
			scoreRepository.save(scoreTAlmeida4);




		};
	}


}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepo;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(inputUserName-> {
			Player player = playerRepo.findByUserName(inputUserName);
			if (player != null){
				return new User(player.getUserName(), player.getPassword(),
				AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Who the bloody hell are ya mate?: "+inputUserName);
			}
		});
	}


}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/player").hasAuthority("USER")
				.antMatchers("/api/game_view/**").hasAuthority("USER")
				.antMatchers("/web/game.html").hasAuthority("USER")
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/**").permitAll()
				.antMatchers("/rest/**").permitAll()

				.anyRequest().fullyAuthenticated();
				//.and();
		http.formLogin()

						.usernameParameter("username")
						.passwordParameter("password")
						.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		//sirve para configurar seguridad web services
		http.csrf().disable();

		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}