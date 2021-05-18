package app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastInt.Int;
import static SupportingFiles.ConfigHelper.*;
import static app.PublicDefinitions.*;

public class PreferencesController {

    private final Stage mainStage;

    @FXML
    VBox lightVBox;

    @FXML
    VBox darkVBox;

    @FXML
    VBox matchSystemVBox;

    @FXML
    CheckBox enableQuestionMarksCheckBox;

    @FXML
    CheckBox enableChordCheckBox;

    @FXML
    CheckBox openSquaresCheckBox;

    @FXML
    CheckBox highlightCheckBox;

    @FXML
    CheckBox markIncorrectCheckBox;

    @FXML
    Slider musicSlider;

    @FXML
    Slider soundEffectsSlider;

    public PreferencesController() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PreferencesController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(PREFERENCES_CONTROLLER_WIDTH);
        mainStage.setMinHeight(PREFERENCES_CONTROLLER_HEIGHT);
        mainStage.setResizable(false);
        mainStage.setTitle("Preferences");

        setupInterfaceStyle(root);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        loadConfig();
        initializeSlider();
    }

    public void showStage() {
        mainStage.show();
    }

    @FXML
    private void closeStage() {
        mainStage.close();
    }

    public void loadConfig() {
        switch (getAppearanceSettings()) {
            case 0 -> lightVBox.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 2;");
            case 1 -> darkVBox.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 2;");
            case 2 -> matchSystemVBox.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 2;");
        }

        enableQuestionMarksCheckBox.setSelected(isQuestionMarksEnabled());
        enableChordCheckBox.setSelected(isChordEnabled());
        openSquaresCheckBox.setSelected(isOpenAllSquaresSurroundingZeroEnabled());
        highlightCheckBox.setSelected(isHighlightComputersMoveEnabled());
        markIncorrectCheckBox.setSelected(isMarkIncorrectSquaresEnabled());
        musicSlider.setValue(getMusicVolume());
        soundEffectsSlider.setValue(getSoundEffectsVolume());
    }

    @FXML
    public void didClickLight() {
        print("didClickLight");
        setAppearance(0);
        lightVBox.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 2;");
        darkVBox.setStyle("-fx-border-radius: 10;-fx-border-color: transparent;-fx-border-width: 2;");
        matchSystemVBox.setStyle("-fx-border-radius: 10;-fx-border-color: transparent;-fx-border-width: 2;");
    }

    @FXML
    public void didClickDark() {
        print("didClickDark");
        setAppearance(1);
        lightVBox.setStyle("-fx-border-radius: 10;-fx-border-color: transparent;-fx-border-width: 2;");
        darkVBox.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 2;");
        matchSystemVBox.setStyle("-fx-border-radius: 10;-fx-border-color: transparent;-fx-border-width: 2;");
    }

    @FXML
    public void didClickMatchSystem() {
        print("didClickMatchSystem");
        setAppearance(2);
        lightVBox.setStyle("-fx-border-radius: 10;-fx-border-color: transparent;-fx-border-width: 2;");
        darkVBox.setStyle("-fx-border-radius: 10;-fx-border-color: transparent;-fx-border-width: 2;");
        matchSystemVBox.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 2;");
    }

    @FXML
    public void toggleEnableQuestionMarks() {
        print("toggleEnableQuestionMarks");
        setQuestionMarksEnabled(enableQuestionMarksCheckBox.isSelected());
    }

    @FXML
    public void toggleEnableChord() {
        print("toggleEnableChord");
        setChordEnabled(enableChordCheckBox.isSelected());
    }

    @FXML
    public void toggleOpenSquares() {
        print("toggleOpenSquares");
        setOpenAllSquaresSurroundingZeroEnabled(openSquaresCheckBox.isSelected());
    }

    @FXML
    public void toggleHighlight() {
        setHighlightComputersMoveEnabled(highlightCheckBox.isSelected());
    }

    @FXML
    public void toggleMarkIncorrect() {
        setMarkIncorrectSquaresEnabled(markIncorrectCheckBox.isSelected());
    }

    public void initializeSlider() {

        musicSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                print(newValue.intValue());
                music.setVolume(newValue.intValue());
            }

        });
        musicSlider.setOnMouseReleased(event -> {
            print("released");
            setMusicVolume(Int(musicSlider.getValue()));
        });

//        musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            setMusicVolume(newValue.intValue());
//        });
        soundEffectsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setSoundEffectsVolume(newValue.intValue());
        });
    }
}
