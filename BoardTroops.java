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
		troopMap = Collections.emptyMap();
		leaderPosition = TilePos.OFF_BOARD;
		guards = 0;
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
		Optional<TroopTile> whatIsThat;
		if (troopMap.containsKey(pos)){
			whatIsThat = Optional.ofNullable(troopMap.get(pos));
		} else {
			whatIsThat = Optional.empty();
		}
		return whatIsThat;
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
		return isLeaderPlaced() && guards < 2;
	}

	public Set<BoardPos> troopPositions() {
		return troopMap.keySet();
	}

	public BoardTroops placeTroop(Troop troop, BoardPos target) {
		TroopTile newTroopTile = new TroopTile(troop, playingSide, TroopFace.AVERS);
		if (troopMap.containsKey(target)){
			throw new IllegalArgumentException();
		}
		else {

			Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);

			newTroops.put(target, newTroopTile);

			if (leaderPosition == TilePos.OFF_BOARD) {
				return new BoardTroops(playingSide, newTroops, target, guards);
			} else if (isPlacingGuards()){
				return new BoardTroops(playingSide, newTroops, leaderPosition, guards + 1);
			} else {
				return new BoardTroops(playingSide, newTroops, leaderPosition, guards);
			}
		}

	}
	
	public BoardTroops troopStep(BoardPos origin, BoardPos target) {
		if (leaderPosition == TilePos.OFF_BOARD || isPlacingGuards()) {
			throw new IllegalStateException();
		}
		if (!troopMap.containsKey(origin) || troopMap.containsKey(target)) {
			throw new IllegalArgumentException();
		}

		TroopTile flippedTroopTile = troopMap.get(origin).flipped();
		Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);

		newTroops.remove(origin);
		newTroops.put(target, flippedTroopTile);

		if (origin.i() == leaderPosition.i() && origin.j() == leaderPosition.j()) {
			return new BoardTroops(playingSide, newTroops, target, guards);
		} else {
			return new BoardTroops(playingSide, newTroops, leaderPosition, guards);
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
		
		if(!at(origin).isPresent())
			throw new IllegalArgumentException();
		
		Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
		TroopTile tile = newTroops.remove(origin);
		newTroops.put(origin, tile.flipped());

		return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
	}
	
	public BoardTroops removeTroop(BoardPos target) {
		if (leaderPosition == TilePos.OFF_BOARD || isPlacingGuards()) {
			throw new IllegalStateException();
		}
		if (!troopMap.containsKey(target)) {
			throw new IllegalArgumentException();
		}

		troopMap.remove(target);

		if (target.i() == leaderPosition.i() && target.j() == leaderPosition.j()) {
			return new BoardTroops(playingSide, troopMap, TilePos.OFF_BOARD, guards);
		} else {
			return new BoardTroops(playingSide, troopMap, leaderPosition, guards);
		}
	}

	@Override
	public void toJSON(PrintWriter writer) {
		writer.printf("{");

		writer.printf("\"side\":");
		playingSide.toJSON(writer);
		writer.printf(",\"leaderPosition\":");
		leaderPosition.toJSON(writer);
		writer.printf(",\"guards\":%s", guards);


		writer.printf(",\"troopMap\":");
		writer.printf("{");
		Set<BoardPos> keys = troopPositions();
		List<BoardPos> keysArray = new ArrayList<>(keys);
		keysArray.sort((p1, p2) -> p1.toString().compareTo(p2.toString()));

		int count = 0;
		for (BoardPos pos: keysArray) {
			pos.toJSON(writer);
			writer.printf(":");
			troopMap.get(pos).toJSON(writer);
			++count;
			if (count < keysArray.size())
				writer.printf(",");
		}
		writer.printf("}");

		writer.printf("}");
	}
}
