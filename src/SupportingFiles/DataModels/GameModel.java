package SupportingFiles.DataModels;

import app.Minefield.*;

import static app.PublicDefinitions.*;

public class GameModel {
    public int numberOfPlayers;
    public MinefieldType[][] minefield;
    public LabelType[][] manipulatedMinefield;
    public long timeUsed;

    public AIDifficulty aiDifficulty;

    public int timeout;
    public int activePlayer;
    public long timeLeftForActivePlayer;
    public int movesLeftForActivePlayer;

    public static class Player {
        public int score;
        public int mistakes;

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
    }

    public GameModel(MultiplayerMinefieldController multiplayerMinefieldController) {
        numberOfPlayers = multiplayerMinefieldController.numberOfPlayers;
        minefield = multiplayerMinefieldController.minefield;
        manipulatedMinefield = multiplayerMinefieldController.manipulatedMinefield;
        timeUsed = System.currentTimeMillis() - multiplayerMinefieldController.startTime;
        timeout = multiplayerMinefieldController.timeout;
        activePlayer = multiplayerMinefieldController.currentPlayerIndex;
        timeLeftForActivePlayer = System.currentTimeMillis() - multiplayerMinefieldController.playerStartTime;
        movesLeftForActivePlayer = multiplayerMinefieldController.clicksPerMove - multiplayerMinefieldController.stepsNum;
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
        aiDifficulty = againstAIController.aiDifficulty;
    }
}
