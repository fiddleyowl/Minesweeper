package SupportingFiles;

import static app.PublicDefinitions.*;

public class Robot {
    public enum Difficulty {EASY, MEDIUM, HARD;}

    private MinefieldType[][] clonedMinefield;
    private Difficulty difficulty;

    public Robot(MinefieldType[][] minefieldTypes, Difficulty difficulty) {
        clonedMinefield = minefieldTypes.clone();
        this.difficulty = difficulty;
    }
}
