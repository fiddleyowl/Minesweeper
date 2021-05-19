package SupportingFiles.DataModels;

import app.Minefield.*;

import static app.PublicDefinitions.*;

public class GameModel {
    public int numberOfPlayers;
    public MinefieldType[][] minefield;
    public LabelType[][] manipulatedMinefield;
    public long timeUsed;
    public int rounds;

    public AIDifficulty aiDifficulty;

    public int timeout;
    public int clicksPerMove;
    public int activePlayer;
    public long timeLeftForActivePlayer;
    public int clicksLeftForActivePlayer;

    public static class Player {
        public int score = -1;
        public int mistakes = -1;

        public Player(int score, int mistakes) {
            this.score = score;
            this.mistakes = mistakes;
        }
    }

    public Player[] players;

    public GameModel(SinglePlayerMinefieldController singlePlayerMinefieldController) {
        numberOfPlayers = 1;
        minefield = singlePlayerMinefieldController.minefield;
        manipulatedMinefield = singlePlayerMinefieldController.manipulatedMinefield;
        timeUsed = System.currentTimeMillis() - singlePlayerMinefieldController.startTime;
        rounds = singlePlayerMinefieldController.rounds;
    }

    public GameModel(MultiplayerMinefieldController multiplayerMinefieldController) {
        numberOfPlayers = multiplayerMinefieldController.numberOfPlayers;
        minefield = multiplayerMinefieldController.minefield;
        manipulatedMinefield = multiplayerMinefieldController.manipulatedMinefield;
        timeUsed = System.currentTimeMillis() - multiplayerMinefieldController.startTime;
        rounds = multiplayerMinefieldController.rounds;
        timeout = multiplayerMinefieldController.timeout;
        clicksPerMove = multiplayerMinefieldController.clicksPerMove;
        activePlayer = multiplayerMinefieldController.currentPlayerIndex;
        timeLeftForActivePlayer = System.currentTimeMillis() - multiplayerMinefieldController.playerStartTime;
        clicksLeftForActivePlayer = multiplayerMinefieldController.clicksPerMove - multiplayerMinefieldController.stepsNum;
        players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = new Player(multiplayerMinefieldController.scores[i],multiplayerMinefieldController.mistakes[i]);
            players[i] = player;
        }
    }

    public GameModel(AgainstAIController againstAIController) {
        numberOfPlayers = -1;
        minefield = againstAIController.minefield;
        manipulatedMinefield = againstAIController.manipulatedMinefield;
        timeUsed = System.currentTimeMillis() - againstAIController.startTime;
        rounds = againstAIController.rounds;
        aiDifficulty = againstAIController.aiDifficulty;
        players = new Player[2];
        for (int i = 0; i < 2; i++) {
            Player player = new Player(againstAIController.scores[i],againstAIController.mistakes[i]);
            players[i] = player;
        }
    }

    public GameModel() {

    }
}
