package app.Minefield;

import app.PublicDefinitions;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.IOException;

import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastString.String;

public class MultiplayerMinefieldController extends MinefieldController {

    int[] scores;
    int[] mistakes;

    int clicksPerMove = 1;
    int numberOfPlayers = 2;

    public MultiplayerMinefieldController(int rows, int columns, int mines, int numberOfPlayers, int clicksPerMove) throws IOException {
        super(rows, columns, mines);
        this.clicksPerMove = clicksPerMove;
        this.numberOfPlayers = numberOfPlayers;
        initializeRightBorderPane();
        scores = new int[numberOfPlayers];
        mistakes = new int[numberOfPlayers];
    }

    @Override
    void clickedOnLabel(PublicDefinitions.MouseClickType type, int row, int column) {

    }

    public void initializeRightBorderPane() {
        playerInformationGridPane.setGridLinesVisible(false);

        Label iconLabel1 = new Label("\uDBC0\uDC05");
        iconLabel1.setFont(new Font("SF Pro Display Regular",80));
        Label scoreNameLabel1 = new Label("Score: ");
        scoreNameLabel1.setFont(new Font("SF Mono Regular",18));
        Label scoreLabel1 = new Label("");
        scoreLabel1.setFont(new Font("SF Mono Regular",18));
        HBox scoreHBox1 = new HBox(scoreNameLabel1,scoreLabel1);
        scoreHBox1.setAlignment(Pos.CENTER);
        Label mistakesNameLabel1 = new Label("Mistakes: ");
        mistakesNameLabel1.setFont(new Font("SF Mono Regular",18));
        Label mistakesLabel1 = new Label("");
        mistakesLabel1.setFont(new Font("SF Mono Regular",18));
        HBox mistakesHBox1 = new HBox(mistakesNameLabel1,mistakesLabel1);
        mistakesHBox1.setAlignment(Pos.CENTER);
        VBox playerInformationVBox1 = new VBox(4,iconLabel1,scoreHBox1,mistakesHBox1);
        playerInformationVBox1.setAlignment(Pos.CENTER);

        Label iconLabel2 = new Label("\uDBC0\uDC07");
        iconLabel2.setFont(new Font("SF Pro Display Regular",80));
        Label scoreNameLabel2 = new Label("Score: ");
        scoreNameLabel2.setFont(new Font("SF Mono Regular",18));
        Label scoreLabel2 = new Label("");
        scoreLabel2.setFont(new Font("SF Mono Regular",18));
        HBox scoreHBox2 = new HBox(scoreNameLabel2,scoreLabel2);
        scoreHBox2.setAlignment(Pos.CENTER);
        Label mistakesNameLabel2 = new Label("Mistakes: ");
        mistakesNameLabel2.setFont(new Font("SF Mono Regular",18));
        Label mistakesLabel2 = new Label("");
        mistakesLabel2.setFont(new Font("SF Mono Regular",18));
        HBox mistakesHBox2 = new HBox(mistakesNameLabel2,mistakesLabel2);
        mistakesHBox2.setAlignment(Pos.CENTER);
        VBox playerInformationVBox2 = new VBox(4,iconLabel2,scoreHBox2,mistakesHBox2);
        playerInformationVBox2.setAlignment(Pos.CENTER);

        Label iconLabel3 = new Label("\uDBC0\uDC09");
        iconLabel3.setFont(new Font("SF Pro Display Regular",80));
        Label scoreNameLabel3 = new Label("Score: ");
        scoreNameLabel3.setFont(new Font("SF Mono Regular",18));
        Label scoreLabel3 = new Label("");
        scoreLabel3.setFont(new Font("SF Mono Regular",18));
        HBox scoreHBox3 = new HBox(scoreNameLabel3,scoreLabel3);
        scoreHBox3.setAlignment(Pos.CENTER);
        Label mistakesNameLabel3 = new Label("Mistakes: ");
        mistakesNameLabel3.setFont(new Font("SF Mono Regular",18));
        Label mistakesLabel3 = new Label("");
        mistakesLabel3.setFont(new Font("SF Mono Regular",18));
        HBox mistakesHBox3 = new HBox(mistakesNameLabel3,mistakesLabel3);
        mistakesHBox3.setAlignment(Pos.CENTER);
        VBox playerInformationVBox3 = new VBox(4,iconLabel3,scoreHBox3,mistakesHBox3);
        playerInformationVBox3.setAlignment(Pos.CENTER);

        Label iconLabel4 = new Label("\uDBC0\uDC0B");
        iconLabel4.setFont(new Font("SF Pro Display Regular",80));
        Label scoreNameLabel4 = new Label("Score: ");
        scoreNameLabel4.setFont(new Font("SF Mono Regular",18));
        Label scoreLabel4 = new Label("");
        scoreLabel4.setFont(new Font("SF Mono Regular",18));
        HBox scoreHBox4 = new HBox(scoreNameLabel4,scoreLabel4);
        scoreHBox4.setAlignment(Pos.CENTER);
        Label mistakesNameLabel4 = new Label("Mistakes: ");
        mistakesNameLabel4.setFont(new Font("SF Mono Regular",18));
        Label mistakesLabel4 = new Label("");
        mistakesLabel4.setFont(new Font("SF Mono Regular",18));
        HBox mistakesHBox4 = new HBox(mistakesNameLabel4,mistakesLabel4);
        mistakesHBox4.setAlignment(Pos.CENTER);
        VBox playerInformationVBox4 = new VBox(4,iconLabel4,scoreHBox4,mistakesHBox4);
        playerInformationVBox4.setAlignment(Pos.CENTER);

        RowConstraints rowConstraints = new RowConstraints();
        ColumnConstraints columnConstraints = new ColumnConstraints();
        switch (numberOfPlayers) {
            case 2:
                rowConstraints.setPercentHeight(50.0);
                columnConstraints.setPercentWidth(100.0);
                playerInformationGridPane.add(playerInformationVBox1,0,0);
                playerInformationGridPane.add(playerInformationVBox2,0,1);
                break;
            case 3:
                rowConstraints.setPercentHeight(50.0);
                columnConstraints.setPercentWidth(50.0);
                playerInformationGridPane.add(playerInformationVBox1,0,0);
                playerInformationGridPane.add(playerInformationVBox2,1,0);
                playerInformationGridPane.add(playerInformationVBox3,0,1);
                break;
            case 4:
                playerInformationGridPane.add(playerInformationVBox1,0,0);
                playerInformationGridPane.add(playerInformationVBox2,1,0);
                playerInformationGridPane.add(playerInformationVBox3,0,1);
                playerInformationGridPane.add(playerInformationVBox4,1,1);
                rowConstraints.setPercentHeight(50.0);
                columnConstraints.setPercentWidth(50.0);
                break;
        }

        playerInformationGridPane.setPrefSize(300, 520);
        playerInformationGridPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        playerInformationGridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        playerInformationGridPane.setAlignment(Pos.CENTER);

        playerInformationGridPane.getRowConstraints().add(rowConstraints);
        playerInformationGridPane.getColumnConstraints().add(columnConstraints);

    }

    /**
     * <p>A UI method to update informative labels on the right side of the minefield.</p>
     * <p><i>startTime</i> and <i>stopTime</i> are used to calculate elapsed time.</p>
     * <p><i>discoveredMines</i> is used to calculate remaining mines.</p>
     */
    @Override
    public void updateInformativeLabels() {
        /*
        long duration = stopTime - startTime;
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        timerLabel.setText(time);
        */
        mineLabel.setText(String(mines - discoveredMines));
    }

    @Override
    void closeStage() {

    }
}
