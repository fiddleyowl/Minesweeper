package app;

import java.util.Random;

import static app.PublicDefinitions.*;

public class Robot {
    public enum Difficulty {EASY, MEDIUM, HARD;}

    private Difficulty difficulty;
    private Random random = new Random();

    public Robot(MinefieldType[][] minefieldTypes, Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void idiot(MinefieldType[][] minefield){
        int row = random.nextInt(minefield.length);
        int column = random.nextInt(minefield[0].length);
        int mouseClickCode = random.nextInt(3);

    }
}
