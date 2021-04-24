package SupportingFiles;

import app.PublicDefinitions;

public class Robot {
    public enum Difficulty {EASY, MEDIUM, HARD;}

    private PublicDefinitions.MinefieldType[][] clonedMinefield;
    private Difficulty difficulty;

    public Robot(PublicDefinitions.MinefieldType[][] minefieldTypes, Difficulty difficulty) {
        clonedMinefield = minefieldTypes.clone();
        this.difficulty = difficulty;
    }
}
