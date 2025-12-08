package lintfordpickle.harvest.data.game;

import java.util.HashMap;
import java.util.Map;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import net.lintfordlib.core.debug.Debug;

public class GameState extends BaseInstanceManager {

	public enum GameMode {
		TimeTrial, Survival,
	}

	public class PlayerScoreCard {

		public int playerUid;

		public short foodDelivered;

		public boolean isPlayerDead;

		public boolean platform1Harvested;
		public boolean platform2Harvested;
		public boolean platform3Harvested;
		public boolean platform4Harvested;

		public boolean platform1Watered;
		public boolean platform2Watered;
		public boolean platform3Watered;
		public boolean platform4Watered;

		public boolean platform1Delivered;
		public boolean platform2Delivered;
		public boolean platform3Delivered;
		public boolean platform4Delivered;

		public boolean isPlatformDelivered(int platformUid) {
			switch (platformUid) {
			case 1:
				return platform1Delivered;

			case 2:
				return platform2Delivered;

			case 3:
				return platform3Delivered;

			case 4:
				return platform4Delivered;

			default:
				throw new IllegalArgumentException("Unexpected value: " + platformUid);
			}
		}

		public boolean isPlatformHarvested(int platformUid) {
			switch (platformUid) {
			case 1:
				return platform1Harvested;

			case 2:
				return platform2Harvested;

			case 3:
				return platform3Harvested;

			case 4:
				return platform4Harvested;

			default:
				throw new IllegalArgumentException("Unexpected value: " + platformUid);
			}
		}

		public boolean isPlatformWatered(int platformUid) {
			switch (platformUid) {
			case 1:
				return platform1Watered;

			case 2:
				return platform2Watered;

			case 3:
				return platform3Watered;

			case 4:
				return platform4Watered;

			default:
				throw new IllegalArgumentException("Unexpected value: " + platformUid);
			}
		}

		public void setPlatformDelivered(int platformUid) {
			switch (platformUid) {
			case 1:
				platform1Delivered = true;
				return;

			case 2:
				platform2Delivered = true;
				return;

			case 3:
				platform3Delivered = true;
				return;

			case 4:
				platform4Delivered = true;
				return;
			}
		}

		public boolean allPlatformsDelivered() {
			return platform1Delivered && platform2Delivered && platform3Delivered && platform4Delivered;
		}
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public float gameTimer;

	public int timeAliveInMs;
	public boolean isGameRunning;

	private GameMode mGameMode = GameMode.Survival;
	private Map<Integer, PlayerScoreCard> mPlayerScoreCards = new HashMap<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public GameMode gameMode() {
		return mGameMode;
	}

	@Override
	public void initializeInstanceCounter() {
		// ignored
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startNewGame(GameMode mode) {
		mGameMode = mode;

		if (mode == GameMode.Survival) {
			gameTimer = ConstantsGame.SURVIVAL_STARTING_TIME_IN_MS;
		} else {
			gameTimer = 0;
		}

		isGameRunning = true;
	}

	public PlayerScoreCard getScoreCard(int playerUid) {
		return mPlayerScoreCards.get(playerUid);
	}

	public boolean addPlayerScoreCard(int playerUid) {
		if (mPlayerScoreCards.containsKey(playerUid)) {
			Debug.debugManager().logger().w(null, null);
			return false;
		}

		mPlayerScoreCards.put(playerUid, new PlayerScoreCard());

		return true;
	}

	@Override
	public void initializeManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {

	}

	@Override
	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {

	}

	@Override
	public void finalizeAfterLoading(SceneData sceneData) {

	}

}
