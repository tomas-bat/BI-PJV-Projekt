package thedrake.ui;

import thedrake.GameResult;

public class StartController {

    public void onVersusMode() {
        GameResult.changeStateTo(GameResult.IN_PLAY);
    }

    public void onExit() {
        System.exit(0);
    }
}
