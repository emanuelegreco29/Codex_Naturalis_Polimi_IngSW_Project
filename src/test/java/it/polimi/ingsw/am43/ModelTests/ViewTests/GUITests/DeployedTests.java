package it.polimi.ingsw.am43.ModelTests.ViewTests.GUITests;

import it.polimi.ingsw.am43.Model.Cards.BackSide;
import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.View.GUI.DeployedController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeployedTests extends Application {


    private Player player;

    private DeployedController controller;


    @Override
    public void start(Stage stage){
        Platform.runLater(()->{
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Deployed.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                controller = fxmlLoader.getController();

                stage.setTitle("Deployed Test");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.sizeToScene();
                stage.show();


            } catch (IOException e) {
                System.err.println("Errore nel caricamento del file FXML");
                throw new RuntimeException(e);
            }
        });
    }

    @BeforeMethod
    public void setUp() {

        this.player = new Player("john", PawnColor.RED);

        ArrayList<CardSide> deployed = new ArrayList<CardSide>();
        int[][] placements = new int[81][81];

        Corner[] corners = {new Corner(Symbol.NONE),new Corner(Symbol.ANIMAL),new Corner(Symbol.FUNGI),new Corner(Symbol.INKWELL)};
        CardSide back1 = new BackSide(corners, Kingdom.ANIMAL, List.of(Symbol.INKWELL, Symbol.QUILL, Symbol.MANUSCRIPT), "");
        back1.setDeployedID(0);
        back1.setRelativeCoordinates(new int[]{0, 0});
        deployed.add(back1);
        CardSide back2 = new CardSide(corners, "");
        back2.setDeployedID(1);
        back2.setRelativeCoordinates(new int[]{1, -1});
        deployed.add(back2);
        CardSide back3 = new CardSide(corners, "");
        back3.setDeployedID(2);
        back3.setRelativeCoordinates(new int[]{2, 0});
        deployed.add(back3);
        CardSide back4 = new BackSide(corners, Kingdom.PLANT, List.of(Symbol.INKWELL, Symbol.QUILL), "");
        back4.setDeployedID(3);
        back4.setRelativeCoordinates(new int[]{3, 1});
        deployed.add(back4);
        CardSide back5 = new BackSide(corners, Kingdom.FUNGI, List.of( Symbol.MANUSCRIPT), "");
        back5.setDeployedID(4);
        back5.setRelativeCoordinates(new int[]{2, 2});
        deployed.add(back5);
        CardSide back6 = new BackSide(corners, Kingdom.INSECT, List.of(Symbol.INKWELL, Symbol.QUILL, Symbol.MANUSCRIPT, Symbol.QUILL), "");
        back6.setDeployedID(5);
        back6.setRelativeCoordinates(new int[]{1, 3});
        deployed.add(back6);
        CardSide back7 = new CardSide(corners, "");
        back7.setDeployedID(12);
        back7.setRelativeCoordinates(new int[]{0, 2});
        deployed.add(back7);


        for (int i=0;i<81;i++){
            for (int j=0; j<81;j++){
                placements[i][j] = -1;
            }
        }

        placements[40][40] = 0;
        placements[40][42] = 12;
        placements[41][39] = 1;
        placements[41][43] = 5;
        placements[42][40] = 2;
        placements[42][42] = 4;
        placements[43][41] = 3;

        this.player.setDeployed(deployed);
        this.player.setPlacements(placements);

        new Thread(Application::launch).start();
    }
}
