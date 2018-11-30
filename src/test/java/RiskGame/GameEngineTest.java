package RiskGame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.Assert.*;

public class GameEngineTest {

    //Name of the file contains list of all territories
    final String territoriesDataFileName = "TerritoryList";

    //The path to the Data folder which contains territories list and their adjacent territories
    final String territoriesDataPath = System.getProperty("user.dir") + File.separator + "Data" + File.separator;

    final String mapPath = System.getProperty("user.dir") + File.separator + "map.jpg";

    private TelegramBot getBot() {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        TelegramBot telegramBot = new TelegramBot(true);
        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return telegramBot;
    }

    private GameEngine getGameEngine() {
        GameEngine gameEngine = new GameEngine();
        return gameEngine;
    }

    @Test
    public void createPlayers() {
        List<Player> players;

        players = getGameEngine().createPlayers(null);

        assertEquals(players.size(), 2);
        assertEquals(players.get(0).getPlayerName(), "Player 1");
        assertEquals(players.get(1).getPlayerName(), "Player 2");

        players = getGameEngine().createPlayers(getBot());
    }

    @Test
    public void readTerritoriesData() {
        List<String> territoryNames = getGameEngine()
                .readTerritoriesData(territoriesDataFileName, ".list");

        assertEquals(territoryNames.size(), 42);
        assertEquals(territoryNames.get(0), "Alaska");
    }

    @Test
    public void checkDuplicationsInTerritoryNames() {
        List<String> territoryNames = getGameEngine().readTerritoriesData(territoriesDataFileName, ".list");

        assertFalse(getGameEngine().checkDuplicationsInTerritoryNames(territoryNames));
        assertFalse(getGameEngine().checkDuplicationsInTerritoryNames(null));
    }

    @Test
    public void filterOutTerritoryNamesDuplications() {
        List<String> territoryNames = getGameEngine().readTerritoriesData(territoriesDataFileName, ".list");

        assertEquals(getGameEngine().filterOutTerritoryNamesDuplications(territoryNames).size(), 42);
        assertNull(getGameEngine().filterOutTerritoryNamesDuplications(null));
    }

    @Test
    public void createTerritoriesFromNames() {
        List<String> territoryNames = getGameEngine().readTerritoriesData(territoriesDataFileName, ".list");

        assertEquals(getGameEngine().createTerritoriesFromNames(territoryNames).size(), 42);
    }

    @Test
    public void findAdjacentTerritoryNames() {
        String territoryName = "Alaska";
        List<String> territoryNames = getGameEngine().readTerritoriesData(territoryName, ".adj");
        assertEquals(territoryNames.size(), 3);
    }

    @Test
    public void createTerritoryAdjacents() {
        Territory territory = new Territory("Alaska", 0);

        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        List<String> adjTerritoryNames = gameEngine.readTerritoriesData(territory.getTerritoryName(), ".adj");
        gameEngine.createTerritoryAdjacents(territory, adjTerritoryNames);

        assertEquals(territory.getAdjTerritories().size(), 3);
        assertEquals(territory.getAdjTerritories().get(0).getTerritoryName(), "Northwest");
        assertEquals(territory.getAdjTerritories().get(1).getTerritoryName(), "Alberta");
        assertEquals(territory.getAdjTerritories().get(2).getTerritoryName(), "Kamchatka");
    }

    @Test
    public void getNumberOfArmyEachPlayer() {
        assertEquals(getGameEngine().getNumberOfArmyEachPlayer(1), -1);
        assertEquals(getGameEngine().getNumberOfArmyEachPlayer(2), 35);
        assertEquals(getGameEngine().getNumberOfArmyEachPlayer(3), 35);
        assertEquals(getGameEngine().getNumberOfArmyEachPlayer(4), 30);
        assertEquals(getGameEngine().getNumberOfArmyEachPlayer(5), 25);
        assertEquals(getGameEngine().getNumberOfArmyEachPlayer(6), 20);
    }

    @Test
    public void checkWinCondition() {
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        assertFalse(gameEngine.checkWinCondition());
    }

    @Test
    public void setTerritory() {
        List<Territory> territories = new ArrayList<>();
        territories.add(new Territory("Alaska", 0));
        territories.add(new Territory("Northwest", 1));
        territories.add(new Territory("Alberta", 2));
        territories.add(new Territory("Kamchatka", 3));

        Player player = new Player("Ton", 3);
        String tName = "Alaska";
        int tIndex = 0;

        //Case 1
        Territory foundTerritory = getGameEngine().setTerritory(player,tName, tIndex, territories);

        if (foundTerritory != null) {
            assertEquals(foundTerritory.getTerritoryName(), "Alaska");
            assertEquals(foundTerritory.getTerritoryIndex(), 0);
            assertEquals(foundTerritory.getOccupiedBy().getPlayerName(), "Ton");
        }

        //Case 2
        tName = null;
        tIndex = 0;
        foundTerritory = getGameEngine().setTerritory(player,tName, tIndex, territories);

        if(foundTerritory != null) {
            assertEquals(foundTerritory.getTerritoryName(), "Alaska");
            assertEquals(foundTerritory.getTerritoryIndex(), 0);
            assertEquals(foundTerritory.getOccupiedBy().getPlayerName(), "Ton");
        }
    }

    @Test
    public void addTroopsToOwnedTerritory() {
        Player player = new Player("Ton");
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        String tName = "Alaska";
        int tIndex = 0;

        //Case 1
        Territory foundTerritory = gameEngine.addTroopsToOwnedTerritory(player, tName, tIndex);

        if (foundTerritory != null) {
            assertEquals(foundTerritory.getTerritoryName(), "Alaska");
            assertEquals(foundTerritory.getTerritoryIndex(), 0);
            assertEquals(foundTerritory.getOccupiedBy().getPlayerName(), "Ton");
            assertEquals(foundTerritory.getNumbOfArmy(), 3);
        }

        //Case 2
        tName = null;
        tIndex = 0;
        foundTerritory = gameEngine.addTroopsToOwnedTerritory(player, tName, tIndex);

        if(foundTerritory != null) {
            assertEquals(foundTerritory.getTerritoryName(), "Alaska");
            assertEquals(foundTerritory.getTerritoryIndex(), 0);
            assertEquals(foundTerritory.getOccupiedBy().getPlayerName(), "Ton");
            assertEquals(foundTerritory.getNumbOfArmy(), 4);
        }
    }

    @Test
    public void findTerritory() {
        List<Territory> territories = new ArrayList<>();
        territories.add(new Territory("Alaska", 0));
        territories.add(new Territory("Northwest", 1));
        territories.add(new Territory("Alberta", 2));
        territories.add(new Territory("Kamchatka", 3));
        String tName = "Alaska";

        Territory foundTerritory = getGameEngine().findTerritory(tName, territories);

        assertEquals(foundTerritory.getTerritoryName(), "Alaska");
        assertEquals(foundTerritory.getTerritoryIndex(), 0);
    }

    @Test
    public void findTerritory1() {
        List<Territory> territories = new ArrayList<>();
        territories.add(new Territory("Alaska", 0));
        territories.add(new Territory("Northwest", 1));
        territories.add(new Territory("Alberta", 2));
        territories.add(new Territory("Kamchatka", 3));
        int tIndex = 0;

        Territory foundTerritory = getGameEngine().findTerritory(tIndex, territories);

        assertEquals(foundTerritory.getTerritoryName(), "Alaska");
        assertEquals(foundTerritory.getTerritoryIndex(), 0);
    }

    @Test
    public void findTerritory2() {
        List<Territory> territories = new ArrayList<>();
        territories.add(new Territory("Alaska", 0));
        territories.add(new Territory("Northwest", 1));
        territories.add(new Territory("Alberta", 2));
        territories.add(new Territory("Kamchatka", 3));
        int tIndex = 0;
        String tName = "Alaska";

        Territory foundTerritory = getGameEngine().findTerritory(tName, tIndex, territories);

        assertEquals(foundTerritory.getTerritoryName(), "Alaska");
        assertEquals(foundTerritory.getTerritoryIndex(), 0);
    }

    @Test
    public void printTerritory1() {
        List<Territory> territories = new ArrayList<>();
        territories.add(new Territory("Alaska", 0));
        territories.add(new Territory("Northwest", 1));
        territories.add(new Territory("Alberta", 2));
        territories.add(new Territory("Kamchatka", 3));

        List<Player> players = new ArrayList<>();
        players.add(new Player("Ton"));
        players.add(new Player("AI"));

        getGameEngine().printTerritory(players, territories, null);
    }

    @Test
    public void printTerritory2() {
        List<Territory> territories = new ArrayList<>();
        territories.add(new Territory("Alaska", 0));
        territories.add(new Territory("Northwest", 1));
        territories.add(new Territory("Alberta", 2));
        territories.add(new Territory("Kamchatka", 3));

        List<Player> players = new ArrayList<>();
        Player player = new Player("Ton");
        players.add(player);
        player.getOwnedTerritories().addAll(territories);
        players.add(new Player("AI"));

        getGameEngine().printTerritory(player, players, territories, null);
    }

    @Test
    public void play() {
        Player atk = new Player("Ton");
        Player def = new Player("Ton");

        Territory atkTerritory = new Territory("Alaska", 0);
        Territory defTerritory = new Territory("Northwest", 1);
        atkTerritory.getAdjTerritories().add(defTerritory);
        defTerritory.getAdjTerritories().add(atkTerritory);

        atk.getOwnedTerritories().add(atkTerritory);
        def.getOwnedTerritories().add(defTerritory);

        atkTerritory.setNumbOfArmy(2);
        defTerritory.setNumbOfArmy(2);

        atkTerritory.setOccupiedBy(atk);
        defTerritory.setOccupiedBy(def);

        getGameEngine().play(atk, atkTerritory, def, defTerritory, null);
        getGameEngine().play(atk, atkTerritory, def, defTerritory, getBot());
    }

    @Test
    public void battleStage() {
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        gameEngine.battleStage(null);
        gameEngine.battleStage(getBot());
    }

    @Test
    public void askTerritoryToAttackTo() {
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        Player atk = new Player("Ton");
        Territory atkTerritory = new Territory("Alaska", 0);

        gameEngine.askTerritoryToAttackTo(atk, atkTerritory, null);
        gameEngine.askTerritoryToAttackTo(atk, atkTerritory, getBot());
    }

    @Test
    public void setAllTerritory() {
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        Player player = new Player("Ton");
        player.setNumOfAvailableArmy(35);
        List<Territory> territories = new ArrayList<>();
        territories.addAll(gameEngine.finalTerritories);

        getGameEngine().setAllTerritory(player, territories, null);
        getGameEngine().setAllTerritory(player, territories, getBot());

        territories.clear();
        getGameEngine().setAllTerritory(player, territories, null);
        getGameEngine().setAllTerritory(player, territories, getBot());
    }

    @Test
    public void createTerritory() {
        getGameEngine().createTerritories(territoriesDataFileName);
    }

    @Test
    public void executeSpecialCommand() {
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        gameEngine.executeSpecialCommand("-la", gameEngine.players.get(0), gameEngine.finalTerritories, null);
        gameEngine.executeSpecialCommand("-la", gameEngine.players.get(0), gameEngine.finalTerritories, getBot());

        gameEngine.executeSpecialCommand("-lm", gameEngine.players.get(0), gameEngine.finalTerritories, null);
        gameEngine.executeSpecialCommand("-lm", gameEngine.players.get(0), gameEngine.finalTerritories, getBot());

        gameEngine.executeSpecialCommand("-lav", gameEngine.players.get(0), gameEngine.finalTerritories, null);
        gameEngine.executeSpecialCommand("-lav", gameEngine.players.get(0), gameEngine.finalTerritories, getBot());

        gameEngine.executeSpecialCommand("1", gameEngine.players.get(0), gameEngine.finalTerritories, null);
        gameEngine.executeSpecialCommand("1", gameEngine.players.get(0), gameEngine.finalTerritories, getBot());
    }

    @Test
    public void startGame() {
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();
        gameEngine.startGame(null);
    }

    @Test
    public void getWinner() {
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        assertNotNull(gameEngine.getWinner());
    }

    @Test
    public void initBattle() {
        GameEngine gameEngine = getGameEngine();
        gameEngine.createTestData();

        gameEngine.initBattle(null);
        gameEngine.initBattle(getBot());

        gameEngine.players.get(0).setLost(false);
        gameEngine.initBattle(null);
        gameEngine.initBattle(getBot());
    }
}