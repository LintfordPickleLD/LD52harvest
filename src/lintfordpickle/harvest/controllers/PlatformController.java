package lintfordpickle.harvest.controllers;

import lintfordpickle.harvest.data.game.GameState.GameMode;
import lintfordpickle.harvest.data.scene.cargo.CargoType;
import lintfordpickle.harvest.data.scene.platforms.PlatformInstance;
import lintfordpickle.harvest.data.scene.platforms.PlatformManager;
import lintfordpickle.harvest.data.scene.ships.Ship;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;

public class PlatformController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Platforms Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;
	private CargoController mCargoController;
	private ShipController mShipController;
	private SceneController mSceneController;
	private PlatformManager mPlatformManager;
	private AudioController mAudioController;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public PlatformManager platformManager() {
		return mPlatformManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PlatformController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();

		mSceneController = (SceneController) lControllerManager.getControllerByNameRequired(SceneController.CONTROLLER_NAME, entityGroupUid());
		mShipController = (ShipController) lControllerManager.getControllerByNameRequired(ShipController.CONTROLLER_NAME, entityGroupUid());
		mCargoController = (CargoController) lControllerManager.getControllerByNameRequired(CargoController.CONTROLLER_NAME, entityGroupUid());
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupUid());
		mAudioController = (AudioController) lControllerManager.getControllerByNameRequired(AudioController.CONTROLLER_NAME, entityGroupUid());

		mPlatformManager = mSceneController.sceneData().platformManager();
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		updatePlatformsState(core);
	}

	private void updatePlatformsState(LintfordCore core) {

		final var lPlatformList = mPlatformManager.platforms();
		final var lNumPlatforms = lPlatformList.size();
		for (int i = 0; i < lNumPlatforms; i++) {
			final var lPlatform = lPlatformList.get(i);

			switch (lPlatform.platformType) {
			case Water:
				updateWaterPlatform(core, lPlatform);

				break;
			case Farm:
				updateFarmPlatform(core, lPlatform);
				break;
			default: // warehouse
				updateWarehousePlatform(core, lPlatform);
				break;
			}

		}

	}

	private Ship getPlayerShip() {
		return mShipController.shipManager().playerShip();
	}

	private boolean isFarmAvailableToWater(LintfordCore core, PlatformInstance platform) {
		final var lGameState = mGameStateController.gameState();
		if (lGameState.gameMode() == GameMode.Survival)
			return true;

		return platform.isWatered == false;
	}

	private boolean isFarmAvailableToHarvest(LintfordCore core, PlatformInstance platform) {
		final var lGameState = mGameStateController.gameState();
		if (lGameState.gameMode() == GameMode.Survival)
			return true;

		return platform.isHarvested == false;
	}

	private boolean isPlayerShipAtPlatform(LintfordCore core, PlatformInstance platform, Ship ship) {
		// TODO: Cache this in the ship class
		final var lUnitsToPx = ConstantsPhysics.UnitsToPixels();
		final var lAABB = ship.body().aabb();
		final var lShipX = lAABB.left() * lUnitsToPx;
		final var lShipY = lAABB.top() * lUnitsToPx;
		final var lShipW = lAABB.width() * lUnitsToPx;
		final var lShipH = lAABB.height() * lUnitsToPx;

		if (platform.intersectsAA(lShipX, lShipY, lShipW, lShipH)) {
			// only register if ships comes to halt
			if (Math.abs(ship.velocityMagnitude) < 0.01) {
				return true;
			}
		}

		return false;
	}

	// ---------------------------------------------

	private void updateWaterPlatform(LintfordCore core, PlatformInstance platform) {
		final var lShip = getPlayerShip();
		final var lIsPlayerAtPlatform = isPlayerShipAtPlatform(core, platform, lShip);

		// stock full
		if (platform.isStockFull) {
			if (lIsPlayerAtPlatform && lShip.cargo.hasFreeSpace()) {
				final var lNewWaterCargo = mCargoController.createNewCargo(platform.uid, CargoType.Water);
				lShip.cargo.addCargo(lNewWaterCargo);

				platform.isStockFull = false;
				platform.stockValueF = 0.f;
			}
		}

		// refill
		if (!platform.isRefillingStock && !platform.isStockFull && lIsPlayerAtPlatform) {
			platform.timeUntilRefillStarts = 200;
		} else {
			if (!platform.isRefillingStock && !platform.isStockFull) {
				platform.timeUntilRefillStarts -= core.gameTime().elapsedTimeMilli();
				if (platform.timeUntilRefillStarts <= 0) {
					platform.isRefillingStock = true;
				}
			}
		}

		// normal
		if (platform.isRefillingStock) {
			platform.stockValueF += 0.05f * core.gameTime().elapsedTimeMilli() * 0.001f;
			if (platform.stockValueF >= 1.f) {
				platform.stockValueF = 1.f;

				mAudioController.playSound("SOUNDWATERPICKUP1");

				platform.isRefillingStock = false;
				platform.isStockFull = true;
			}
		}
	}

	private void updateFarmPlatform(LintfordCore core, PlatformInstance platform) {
		final var lShip = getPlayerShip();
		final var lIsPlayerAtPlatform = isPlayerShipAtPlatform(core, platform, lShip);
		final var lIsPlatformAvailableToWater = isFarmAvailableToWater(core, platform);
		final var lIsPlatformAvailableToHarvest = isFarmAvailableToHarvest(core, platform);

		if (lIsPlatformAvailableToHarvest == false)
			return;

		final var lGameState = mGameStateController.gameState();

		// stock full (wheat)
		if (platform.isStockFull) {
			if (lIsPlayerAtPlatform && lShip.cargo.hasFreeSpace()) {
				final var lNewWheatCargo = mCargoController.createNewCargo(platform.uid, CargoType.Wheat);
				lShip.cargo.addCargo(lNewWheatCargo);

				platform.isStockFull = false;
				platform.stockValueI = 0;
				platform.isHarvested = true;

				final var lPLayerScoreCard = lGameState.getScoreCard(lShip.owningPlayerSessionUid);

				switch (platform.uid) {
				case 1:
					lPLayerScoreCard.platform1Harvested = true;
					break;
				case 2:
					lPLayerScoreCard.platform2Harvested = true;
					break;
				case 3:
					lPLayerScoreCard.platform3Harvested = true;
					break;
				case 4:
					lPLayerScoreCard.platform4Harvested = true;
					break;
				}
			}
		}

		if (lIsPlatformAvailableToWater) {
			// water delivered
			if (!platform.isRefillingStock && !platform.isStockFull && lIsPlayerAtPlatform) {
				if (!platform.refilPrerequisteFulfilled) {
					if (lIsPlayerAtPlatform) {
						// We are now ready to water this platform ...
						final var lPLayerScoreCard = lGameState.getScoreCard(lShip.owningPlayerSessionUid);
						final var lWaterCargo = lShip.cargo.removeCargo(CargoType.Water);

						if (lWaterCargo != null) {
							// The player has the water
							switch (platform.uid) {
							case 1:
								lPLayerScoreCard.platform1Watered = true;
								break;
							case 2:
								lPLayerScoreCard.platform2Watered = true;
								break;
							case 3:
								lPLayerScoreCard.platform3Watered = true;
								break;
							case 4:
								lPLayerScoreCard.platform4Watered = true;
								break;
							}

							platform.isWatered = true;
							platform.refilPrerequisteFulfilled = true;
						}
					}
				}
			}
		}

		if (!platform.isRefillingStock && platform.refilPrerequisteFulfilled) {
			platform.timeUntilRefillStarts -= core.gameTime().elapsedTimeMilli();
			if (platform.timeUntilRefillStarts <= 0) {
				platform.isRefillingStock = true;
			}
		}

		// normal
		if (platform.isRefillingStock) {
			platform.stockValueF += 0.12f * core.gameTime().elapsedTimeMilli() * 0.001f;
			if (platform.stockValueF >= 1.f) {
				platform.stockValueF = 0.f;
				platform.stockValueI = 1;

				platform.isRefillingStock = false;
				platform.refilPrerequisteFulfilled = false;
				platform.isStockFull = true;
			}
		}
	}

	private void updateWarehousePlatform(LintfordCore core, PlatformInstance platform) {
		final var lShip = getPlayerShip();
		final var lIsPlayerAtPlatform = isPlayerShipAtPlatform(core, platform, lShip);

		// check unload of wheat
		if (lIsPlayerAtPlatform) {

			var lWheatCargo = lShip.cargo.removeCargo(CargoType.Wheat);
			while (lWheatCargo != null) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "");

				final var lScoreCard = mGameStateController.gameState().getScoreCard(lShip.owningPlayerSessionUid);
				lScoreCard.foodDelivered++;

				lScoreCard.setPlatformDelivered(lWheatCargo.parentPlatformUid);

				lWheatCargo = lShip.cargo.removeCargo(CargoType.Wheat);
			}
		}
	}
}
