package thedrake;

import java.io.PrintWriter;
import java.util.*;

public class BoardTroops implements JSONSerializable {
	private final PlayingSide playingSide;
	private final Map<BoardPos, TroopTile> troopMap;
	private final TilePos leaderPosition;
	private final int guards;
	
	public BoardTroops(PlayingSide playingSide) {
		this.playingSide = playingSide;
		this.troopMap = Collections.emptyMap();
		this.leaderPosition = TilePos.OFF_BOARD;
		this.guards = 0;
	}
	
	public BoardTroops(
			PlayingSide playingSide,
			Map<BoardPos, TroopTile> troopMap,
			TilePos leaderPosition, 
			int guards) {
		this.playingSide = playingSide;
		this.troopMap = troopMap;
		this.leaderPosition = leaderPosition;
		this.guards = guards;
	}

	public Optional<TroopTile> at(TilePos pos) {
		return Optional.ofNullable(troopMap.get(pos));
	}
	
	public PlayingSide playingSide() {
		return playingSide;
	}
	
	public TilePos leaderPosition() {
		return leaderPosition;
	}

	public int guards() {
		return guards;
	}
	
	public boolean isLeaderPlaced() {
		return leaderPosition != TilePos.OFF_BOARD;
	}
	
	public boolean isPlacingGuards() {
		return (leaderPosition != TilePos.OFF_BOARD) && (guards < 2);
	}	
	
	public Set<BoardPos> troopPositions() {
		return troopMap.keySet();
	}

	public BoardTroops placeTroop(Troop troop, BoardPos target) {
		Map<BoardPos, TroopTile> newMap = new HashMap<>(troopMap);
		if (newMap.containsKey(target))
		    throw new IllegalArgumentException("Tile is already occupied by another troop.");
		else if (newMap.isEmpty()) {
			TroopTile troopTile = new TroopTile(troop, playingSide, TroopFace.AVERS);
			newMap.put(target, troopTile);
			return new BoardTroops(playingSide, newMap, target, 0);
		}
		else {
			TroopTile troopTile = new TroopTile(troop, playingSide, TroopFace.AVERS);
			newMap.put(target, troopTile);
			int guardCount = guards;
			if (guards < 2)
				guardCount++;
			return new BoardTroops(playingSide, newMap, leaderPosition, guardCount);
		}
	}
	
	public BoardTroops troopStep(BoardPos origin, BoardPos target) {
		if (!isLeaderPlaced() || isPlacingGuards())
			throw new IllegalStateException("In phase: Placing leader.");
		Map<BoardPos, TroopTile> newMap = new HashMap<>(troopMap);
		if (!newMap.containsKey(origin))
			throw new IllegalArgumentException();
		else if (newMap.containsKey(target))
			throw new IllegalArgumentException();
		else {
			TroopTile movedTile = newMap.get(origin).flipped();
			newMap.remove(origin);
			newMap.put(target, movedTile);
			TilePos newLeaderPosition = leaderPosition;
			if (leaderPosition.equalsTo(origin.i(), origin.j()))
				newLeaderPosition = target;
			return new BoardTroops(playingSide, newMap, newLeaderPosition, guards);
		}
	}
	
	public BoardTroops troopFlip(BoardPos origin) {
		if(!isLeaderPlaced()) {
			throw new IllegalStateException(
					"Cannot move troops before the leader is placed.");			
		}
		
		if(isPlacingGuards()) {
			throw new IllegalStateException(
					"Cannot move troops before guards are placed.");			
		}
		
		if(at(origin).isEmpty())
			throw new IllegalArgumentException();
		
		Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
		TroopTile tile = newTroops.remove(origin);
		newTroops.put(origin, tile.flipped());

		return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
	}
	
	public BoardTroops removeTroop(BoardPos target) {
		if (!isLeaderPlaced() || isPlacingGuards())
			throw new IllegalStateException("Leader not placed or placing guards.");
		if (troopMap.containsKey(target)) {
			troopMap.remove(target);
			TilePos newLeaderPosition = leaderPosition;
			if (leaderPosition.equalsTo(target.i(), target.j()))
				newLeaderPosition = TilePos.OFF_BOARD;
			return new BoardTroops(playingSide, troopMap, newLeaderPosition, guards);
		}
		else
			throw new IllegalArgumentException("No tile at this position.");
	}

	@Override
	public void toJSON(PrintWriter writer) {
		writer.print("{\"side\":");
		playingSide.toJSON(writer);
		writer.print(",\"leaderPosition\":");
		leaderPosition.toJSON(writer);
		writer.print(",\"guards\":" + this.guards + ",\"troopMap\":{");

		int count = 0;

		for(BoardPos pos : new TreeSet<>(this.troopMap.keySet())){
			pos.toJSON(writer);
			writer.print(":");
			this.troopMap.get(pos).toJSON(writer);
			count++;
			if (count < troopMap.size())
				writer.print(",");
		}

		writer.print("}}");
	}
}
