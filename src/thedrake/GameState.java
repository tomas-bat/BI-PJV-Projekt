package thedrake;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Optional;

public class GameState implements JSONSerializable {
	private final Board board;
	private final PlayingSide sideOnTurn;
	private final Army blueArmy;
	private final Army orangeArmy;
	private final GameResult result;
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy) {
		this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
	}
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy, 
			PlayingSide sideOnTurn, 
			GameResult result) {
		this.board = board;
		this.sideOnTurn = sideOnTurn;
		this.blueArmy = blueArmy;
		this.orangeArmy = orangeArmy;
		this.result = result;
	}
	
	public Board board() {
		return board;
	}
	
	public PlayingSide sideOnTurn() {
		return sideOnTurn;
	}
	
	public GameResult result() {
		return result;
	}
	
	public Army army(PlayingSide side) {
		if(side == PlayingSide.BLUE) {
			return blueArmy;
		}
		
		return orangeArmy;
	}
	
	public Army armyOnTurn() {
		return army(sideOnTurn);
	}
	
	public Army armyNotOnTurn() {
		if(sideOnTurn == PlayingSide.BLUE)
			return orangeArmy;
		
		return blueArmy;
	}
	
	public Tile tileAt(TilePos pos) {
		if (orangeArmy.boardTroops().at(pos).isPresent())
			return orangeArmy.boardTroops().at(pos).get();
		else if (blueArmy.boardTroops().at(pos).isPresent())
			return blueArmy.boardTroops().at(pos).get();
		else if (!board.at(pos).hasTroop())
			return board.at(pos);
		return null;
	}
	
	private boolean canStepFrom(TilePos origin) {
		return !origin.equals(TilePos.OFF_BOARD)
				&& result.equals(GameResult.IN_PLAY)
				&& armyOnTurn().boardTroops().at(board.positionFactory().pos(origin.i(), origin.j())).isPresent()
				&& !blueArmy.boardTroops().isPlacingGuards()
				&& !orangeArmy.boardTroops().isPlacingGuards();
	}

	private boolean canStepTo(TilePos target) {
		return !target.equals(TilePos.OFF_BOARD) &&
				result.equals(GameResult.IN_PLAY) &&
				armyNotOnTurn().boardTroops().at(board.positionFactory().pos(target.i(), target.j())).isEmpty() &&
				armyOnTurn().boardTroops().at(board.positionFactory().pos(target.i(), target.j())).equals(Optional.empty()) &&
				board.at(board.positionFactory().pos(target.i(), target.j())).canStepOn();
	}

	private boolean canCaptureOn(TilePos target) {
		return !target.equals(TilePos.OFF_BOARD) &&
				result.equals(GameResult.IN_PLAY) &&
				armyNotOnTurn().boardTroops().at(target).isPresent() &&
				armyOnTurn().boardTroops().at(target).isEmpty();
	}

	public boolean canStep(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canStepTo(target);
	}
	
	public boolean canCapture(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canCaptureOn(target);
	}
	
	public boolean canPlaceFromStack(TilePos target) {
		if(armyOnTurn().stack().isEmpty() ||
				result != GameResult.IN_PLAY ||
				target == TilePos.OFF_BOARD ||
				!board.at((BoardPos)target).canStepOn() ||
				armyOnTurn().boardTroops().at(target).isPresent() ||
				armyNotOnTurn().boardTroops().at(target).isPresent())
			return false;

		else if (!armyOnTurn().boardTroops().isLeaderPlaced() &&
					(armyOnTurn() == blueArmy && target.row() != 1))
			return false;

		else if (!armyOnTurn().boardTroops().isLeaderPlaced() &&
					(armyOnTurn() == orangeArmy && target.row() != board.dimension()))
			return false;

		else if(armyOnTurn().boardTroops().isPlacingGuards())
			return armyOnTurn().boardTroops().leaderPosition().isNextTo(target);

		if (armyOnTurn().boardTroops().isLeaderPlaced()) {
			for (BoardPos thisPosition : armyOnTurn().boardTroops().troopPositions()) {
				if (thisPosition.isNextTo(target))
					return true;
			}
			return false;
		}
		return true;
	}
	
	public GameState stepOnly(BoardPos origin, BoardPos target) {		
		if(canStep(origin, target))		 
			return createNewGameState(
					armyNotOnTurn(),
					armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);
		
		throw new IllegalArgumentException();
	}
	
	public GameState stepAndCapture(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target), 
					armyOnTurn().troopStep(origin, target).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState captureOnly(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target),
					armyOnTurn().troopFlip(origin).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState placeFromStack(BoardPos target) {
		if(canPlaceFromStack(target)) {
			return createNewGameState(
					armyNotOnTurn(), 
					armyOnTurn().placeFromStack(target), 
					GameResult.IN_PLAY);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState resign() {
		return createNewGameState(
				armyNotOnTurn(), 
				armyOnTurn(), 
				GameResult.VICTORY);
	}
	
	public GameState draw() {
		return createNewGameState(
				armyOnTurn(), 
				armyNotOnTurn(), 
				GameResult.DRAW);
	}
	
	private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
		if(armyOnTurn.side() == PlayingSide.BLUE) {
			return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
		}
		
		return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result); 
	}

	@Override
	public void toJSON(PrintWriter writer) {
		writer.print("{\"result\":");
		result.toJSON(writer);
		writer.print(",\"board\":");
		board.toJSON(writer);
		writer.print(",\"blueArmy\":");
		blueArmy.toJSON(writer);
		writer.print(",\"orangeArmy\":");
		orangeArmy.toJSON(writer);
		writer.print("}");
	}
}
