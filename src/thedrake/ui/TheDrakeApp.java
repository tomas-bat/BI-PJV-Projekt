package thedrake.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import thedrake.*;

public class TheDrakeApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("startView.fxml"));
        Scene drakeScene = new Scene(root);

        primaryStage.setTitle("The drake");
        primaryStage.setWidth(primaryStage.getMaxWidth());
        primaryStage.setHeight(primaryStage.getMaxHeight());

        primaryStage.setScene(drakeScene);
        primaryStage.show();

        GameState gameState = createSampleGameState();
        GameView gameView = new GameView(gameState);
        Scene gameScene = new Scene(gameView);

        FXMLLoader victoryLoader = new FXMLLoader(getClass().getResource("victoryView.fxml"));
        BorderPane victory = victoryLoader.load();
        Scene victoryScene = new Scene(victory);

        VictoryController victoryController = victoryLoader.getController();


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (GameResult.getStateChanged()) {
                    GameResult.changeStateChangedTo(false);

                    switch(GameResult.getState()) {
                        case IN_PLAY:
                            primaryStage.setScene(gameScene);
                            primaryStage.show();
                            break;
                        case VICTORY:
                            PlayingSide notOnTurn = gameView.boardView().gameState().armyNotOnTurn().side();
                            victoryController.setLabel(notOnTurn.name() + " is the winner.");
                            primaryStage.setScene(victoryScene);
                            primaryStage.show();
                            break;
                        case DRAW:
                            // TODO: Draw
                            break;
                        case START:
                            primaryStage.setScene(drakeScene);
                            break;
                    }
                }

            }
        }.start();
    }

    private static GameState createSampleGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board);
    }
}
