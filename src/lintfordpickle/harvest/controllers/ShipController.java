package lintfordpickle.harvest.controllers;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.actionevents.GameActionEventController;
import lintfordpickle.harvest.controllers.camera.CameraShipChaseController;
import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.data.scene.physics.ShipPhysicsData;
import lintfordpickle.harvest.data.scene.ships.Ship;
import lintfordpickle.harvest.data.scene.ships.ShipManager;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.assets.ResourceGroupProvider;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.core.particles.ParticleFrameworkController;
import net.lintfordlib.controllers.physics.PhysicsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ShipController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Ship Controller";

	public static final int DAMAGE_TOP_THREASHOLD = 2;
	public static final int DAMAGE_BOTTOM_THREASHOLD = 10;

	protected static final int MAX_SHIELDS_COMPONENTS = 40;

	private static final boolean DEBUG_DISABLE_PARTICLES = false;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SceneController mSceneController;
	private GameStateController mGameStateController;
	private GameActionEventController mActionEventController;
	private PhysicsController mPhysicsController;
	private AudioController mAudioController;

	private PlayerManager mPlayerManager;
	private ShipManager mShipManager;

	private ParticleSystemInstance mFireParticleSystem;
	private ParticleSystemInstance mSmokeParticleSystem;

	private ParticleSystemInstance mJetParticleSystem;
	private ParticleSystemInstance mJetIntenseParticleSystem;

	private ParticleSystemInstance mSparkParticleSystem;
	private ParticleSystemInstance mEngineSparkParticleSystem;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ShipManager shipManager() {
		return mShipManager;
	}

	public Ship getShipByEntityUid(int entityUid) {
		final var lShips = mShipManager.ships();
		final int lNumShips = lShips.size();
		for (int i = 0; i < lNumShips; i++) {
			if (lShips.get(i).uid == entityUid)
				return lShips.get(i);
		}

		return null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ShipController(ControllerManager controllerManager, PlayerManager playerManager, int entityGroupID) {
		super(controllerManager, CONTROLLER_NAME, entityGroupID);

		mPlayerManager = playerManager;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();

		mSceneController = (SceneController) lControllerManager.getControllerByNameRequired(SceneController.CONTROLLER_NAME, entityGroupUid());
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupUid());
		mActionEventController = (GameActionEventController) lControllerManager.getControllerByNameRequired(GameActionEventController.CONTROLLER_NAME, entityGroupUid());
		mPhysicsController = (PhysicsController) lControllerManager.getControllerByNameRequired(PhysicsController.CONTROLLER_NAME, entityGroupUid());
		mAudioController = (AudioController) lControllerManager.getControllerByNameRequired(AudioController.CONTROLLER_NAME, entityGroupUid());

		mShipManager = mSceneController.sceneData().shipManager();

		createShipsFromPlayerManager();

		setupPlayerCamera(core, lControllerManager);

		setupParticleSystems(core, lControllerManager);
	}

	private void setupParticleSystems(LintfordCore core, final ControllerManager lControllerManager) {
		final var lParticleController = (ParticleFrameworkController) lControllerManager.getControllerByNameRequired(ParticleFrameworkController.CONTROLLER_NAME, entityGroupUid());

		mFireParticleSystem = lParticleController.particleFrameworkData().particleSystemManager().createNewParticleSystemFromDefinitionName("PARTICLESYSTEM_FIRE");
		mSmokeParticleSystem = lParticleController.particleFrameworkData().particleSystemManager().createNewParticleSystemFromDefinitionName("PARTICLESYSTEM_SMOKE");

		mJetParticleSystem = lParticleController.particleFrameworkData().particleSystemManager().createNewParticleSystemFromDefinitionName("PARTICLESYSTEM_JET");
		mJetIntenseParticleSystem = lParticleController.particleFrameworkData().particleSystemManager().createNewParticleSystemFromDefinitionName("PARTICLESYSTEM_JET_INTENSE");

		mSparkParticleSystem = lParticleController.particleFrameworkData().particleSystemManager().createNewParticleSystemFromDefinitionName("PARTICLESYSTEM_SPARK");
		mEngineSparkParticleSystem = lParticleController.particleFrameworkData().particleSystemManager().createNewParticleSystemFromDefinitionName("PARTICLESYSTEM_ENGINESPARK");
	}

	private void setupPlayerCamera(LintfordCore core, final ControllerManager lControllerManager) {
		final var lCameraChaseController = (CameraShipChaseController) lControllerManager.getControllerByNameRequired(CameraShipChaseController.CONTROLLER_NAME, entityGroupUid());
		if (lCameraChaseController != null) {
			final var lDefaultShip = mShipManager.ships().get(0);
			lCameraChaseController.setTrackedEntity(core.gameCamera(), lDefaultShip);
		}
	}

	private void createShipsFromPlayerManager() {
		final var lPhysicsWorld = mPhysicsController.world();

		// loop over the players in the playermanager (player and ghosts), and create an action session for them

		final int lNumPlayers = mPlayerManager.numActivePlayers();
		for (int i = 0; i < lNumPlayers; i++) {
			final var playerSession = mPlayerManager.getPlayer(i);

			final var ship = new Ship(ResourceGroupProvider.getRollingEntityNumber());
			ship.owningPlayerSessionUid = playerSession.playerUid();
			ship.isPlayerControlled = playerSession.isPlayerControlled();
			ship.isGhostShip = playerSession.isGhostMode();

			final float lShipPositionInUnitsX = ConstantsPhysics.toUnits(0.f);
			final float lShipPositioninUnitsY = ConstantsPhysics.toUnits(0.f);

			ship.body().moveTo(lShipPositionInUnitsX, lShipPositioninUnitsY);

			mShipManager.ships().add(ship);

			if (ship.isGhostShip) {
				ship.body().categoryBits(ConstantsGame.PHYSICS_WORLD_MASK_GHOST);
				ship.body().maskBits(ConstantsGame.PHYSICS_WORLD_MASK_WALL);
			} else {
				ship.body().categoryBits(ConstantsGame.PHYSICS_WORLD_MASK_SHIP);
				ship.body().maskBits(ConstantsGame.PHYSICS_WORLD_MASK_WALL);

				initializeShipAudio(ship);
			}

			lPhysicsWorld.addBody(ship.body());
		}

	}

	private void initializeShipAudio(Ship ship) {
		if (mAudioController == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't load ship audio - no AudioController found");
			return;
		}

		final var lAudioManager = mAudioController.audioManager();
		ship.audio.initialize(lAudioManager);

	}

	@Override
	public void unloadController() {
		final var lPhysicsWorld = mPhysicsController.world();

		final var lShips = mShipManager.ships();
		final var lShipCount = lShips.size();
		for (int i = 0; i < lShipCount; i++) {
			final var lShipInstance = lShips.get(0);
			if (lShipInstance == null)
				continue;

			lShipInstance.audio.unloadResources();
			lShipInstance.unloadPhysicsBody();

		}
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		final var ships = mShipManager.ships();
		final var numShips = ships.size();
		for (int i = 0; i < numShips; i++) {
			final var shipInst = ships.get(i);
			final var playerSessions = mPlayerManager.getPlayer(shipInst.owningPlayerSessionUid);
			final var actionManager = mActionEventController.actionEventPlayer(playerSessions.actionEventUid());

			shipInst.inputs.isLeftThrottle = actionManager.currentActionEvents.isThrottleLeftDown;
			shipInst.inputs.isRightThrottle = actionManager.currentActionEvents.isThrottleRightDown;
			shipInst.inputs.isUpThrottle = actionManager.currentActionEvents.isThrottleDown;

			final float throttleRollingAmt = 0.5f;
			if (shipInst.inputs.isUpThrottle) {
				shipInst.rollingThrottle += core.gameTime().elapsedTimeMilli() * throttleRollingAmt;
				if (shipInst.rollingThrottle >= shipInst.rollingThrottleMax)
					shipInst.rollingThrottle = shipInst.rollingThrottleMax;

			} else {
				shipInst.rollingThrottle -= core.gameTime().elapsedTimeMilli() * throttleRollingAmt;
				if (shipInst.rollingThrottle <= shipInst.rollingThrottleMin)
					shipInst.rollingThrottle = shipInst.rollingThrottleMin;

			}

		}

		return super.handleInput(core);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lShips = mShipManager.ships();
		final var lNumShips = lShips.size();
		for (int i = 0; i < lNumShips; i++) {
			final var lShip = lShips.get(i);

			updateShip(core, lShip);

			if (lShip.isDead() && lShip.isPlayerControlled) {
				mGameStateController.setPlayerDied(lShip.owningPlayerSessionUid);
				continue;
			}

			if (lShip.audio.audioEnabled())
				lShip.audio.update(core, lShip);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void updateShip(LintfordCore core, Ship ship) {
		if (ship.isPlayerControlled == false)
			return;

		ship.update(core);

		if (ship.isDead()) {
			ship.body().setAngularVelocity(ship.body().angularVelocity() * .99f);
			return;
		}

		final var body = ship.body();
		final var lShipInput = ship.inputs;

		final float lThrustUpForce = 100.f;
		final float lAngularTorque = .003f;

		final float lJetForce = 50.f;
		final float lSparkForce = 20.f;

		final float lUnitsToPixels = ConstantsPhysics.UnitsToPixels();

		final float lAngle = body.transform.angle;
		final float lUpAngleX = body.transform.q.c;
		final float lUpAngleY = body.transform.q.s;

		final float lAdjustedAngle = body.transform.angle + (float) Math.toRadians(90.f);
		final float lAdjustedAngleX = (float) Math.cos(lAdjustedAngle);
		final float lAdjustedAngleY = (float) Math.sin(lAdjustedAngle);

		final float lAdjustedVx = body.vx * lUnitsToPixels * .5f;
		final float lAdjustedVy = body.vy * lUnitsToPixels * .5f;

		final float lSparkChange = 32.8f;

		if (lShipInput.isUpThrottle) {
			body.accX += -lUpAngleY * -lThrustUpForce * body.invMass();
			body.accY += lUpAngleX * -lThrustUpForce * body.invMass();
			body.setAngularVelocity(ship.body().angularVelocity() * .99f);

			if (DEBUG_DISABLE_PARTICLES == false) {
				final var lFrontEnginePositionX = ship.frontEngine.x * lUnitsToPixels;
				final var lFrontEnginePositionY = ship.frontEngine.y * lUnitsToPixels;
				mJetParticleSystem.spawnParticle(lFrontEnginePositionX, lFrontEnginePositionY, -.2f, lAdjustedVx + lAdjustedAngleX * lJetForce, lAdjustedVy + lAdjustedAngleY * lJetForce);
				mJetIntenseParticleSystem.spawnParticle(lFrontEnginePositionX, lFrontEnginePositionY, -.2f, lAdjustedVx + lAdjustedAngleX * lJetForce, lAdjustedVy + lAdjustedAngleY * lJetForce);

				{
					if (RandomNumbers.getRandomChance(lSparkChange)) {
						final float lMaxAngle = 10.f;
						final float lAngleOffset = RandomNumbers.random(-lMaxAngle, lMaxAngle);

						final float lSparkAngleX = (float) Math.cos(lAngle + lAngleOffset);
						final float lSparkAngleY = (float) Math.sin(lAngle + lAngleOffset);

						mEngineSparkParticleSystem.spawnParticle(lFrontEnginePositionX, lFrontEnginePositionY, -.2f, (lSparkAngleX) * lSparkForce, (lSparkAngleY) * lSparkForce);
					}

				}

				final var lRearEnginePositionX = ship.rearEngine.x * lUnitsToPixels;
				final var lRearEnginePositionY = ship.rearEngine.y * lUnitsToPixels;
				mJetParticleSystem.spawnParticle(lRearEnginePositionX, lRearEnginePositionY, -.2f, lAdjustedVx + lAdjustedAngleX * lJetForce, lAdjustedVy + lAdjustedAngleY * lJetForce);
				mJetIntenseParticleSystem.spawnParticle(lRearEnginePositionX, lRearEnginePositionY, -.2f, lAdjustedVx + lAdjustedAngleX * lJetForce, lAdjustedVy + lAdjustedAngleY * lJetForce);

				{
					if (RandomNumbers.getRandomChance(lSparkChange)) {
						final float lMaxAngle = 10.f;
						final float lAngleOffset = RandomNumbers.random(-lMaxAngle, lMaxAngle);

						final float lSparkAngleX = (float) Math.cos(lAngle + lAngleOffset);
						final float lSparkAngleY = (float) Math.sin(lAngle + lAngleOffset);

						mEngineSparkParticleSystem.spawnParticle(lRearEnginePositionX, lRearEnginePositionY, -.2f, (lSparkAngleX) * lSparkForce, (lSparkAngleY) * lSparkForce);
					}
				}
			}
		}

		{
			if (lShipInput.isLeftThrottle) {
				body.transform.angle -= lAngularTorque * core.gameTime().elapsedTimeMilli();
				if (body.angularVelocity() > 0.f)
					body.setAngularVelocity(body.angularVelocity() * .9f);

				if (DEBUG_DISABLE_PARTICLES == false) {
					final var lFrontEnginePositionX = ship.frontEngine.x * lUnitsToPixels;
					final var lFrontEnginePositionY = ship.frontEngine.y * lUnitsToPixels;
					mJetParticleSystem.spawnParticle(lFrontEnginePositionX, lFrontEnginePositionY, -.2f, lAdjustedVx + lAdjustedAngleX * lJetForce, lAdjustedVy + lAdjustedAngleY * lJetForce);
					mJetIntenseParticleSystem.spawnParticle(lFrontEnginePositionX, lFrontEnginePositionY, -.2f, lAdjustedVx + lAdjustedAngleX * lJetForce, lAdjustedVy + lAdjustedAngleY * lJetForce);

					if (RandomNumbers.getRandomChance(lSparkChange)) {
						final float lMaxAngle = 10.f;
						final float lAngleOffset = RandomNumbers.random(-lMaxAngle, lMaxAngle);

						final float lSparkAngleX = (float) Math.cos(lAngle + lAngleOffset);
						final float lSparkAngleY = (float) Math.sin(lAngle + lAngleOffset);

						mEngineSparkParticleSystem.spawnParticle(lFrontEnginePositionX, lFrontEnginePositionY, -.2f, (lSparkAngleX) * lSparkForce, (lSparkAngleY) * lSparkForce);
					}
				}
			}

			if (lShipInput.isRightThrottle) {
				body.transform.angle += lAngularTorque * core.gameTime().elapsedTimeMilli();
				if (body.angularVelocity() < 0.f)
					body.setAngularVelocity(body.angularVelocity() * .9f);

				if (DEBUG_DISABLE_PARTICLES == false) {
					final var lRearEnginePositionX = ship.rearEngine.x * lUnitsToPixels;
					final var lRearEnginePositionY = ship.rearEngine.y * lUnitsToPixels;
					mJetParticleSystem.spawnParticle(lRearEnginePositionX, lRearEnginePositionY, -.2f, lAdjustedVx + lAdjustedAngleX * lJetForce, lAdjustedVy + lAdjustedAngleY * lJetForce);
					mJetIntenseParticleSystem.spawnParticle(lRearEnginePositionX, lRearEnginePositionY, -.2f, lAdjustedVx + lAdjustedAngleX * lJetForce, lAdjustedVy + lAdjustedAngleY * lJetForce);

					if (RandomNumbers.getRandomChance(lSparkChange)) {
						final float lMaxAngle = 10.f;
						final float lAngleOffset = RandomNumbers.random(-lMaxAngle, lMaxAngle);

						final float lSparkAngleX = (float) Math.cos(lAngle + lAngleOffset);
						final float lSparkAngleY = (float) Math.sin(lAngle + lAngleOffset);

						mEngineSparkParticleSystem.spawnParticle(lRearEnginePositionX, lRearEnginePositionY, -.2f, (lSparkAngleX) * lSparkForce, (lSparkAngleY) * lSparkForce);
					}
				}
			}
		}

		// TODO: Particles in graphics options
		if (DEBUG_DISABLE_PARTICLES == false) {
			final var lStep = ship.maxHealth / 10.f;
			final var lLowSmokeLevel = ship.maxHealth - lStep;
			final var lMidSmokeLevel = ship.maxHealth - lStep * 2;
			final var lHighSmokeLevel = ship.maxHealth - lStep * 3;

//			final float lWorldX = body.transform.p.x * lUnitsToPixels;
//			final float lWorldY = body.transform.p.y * lUnitsToPixels;

			var lWorldX = ship.rearEngine.x * lUnitsToPixels;
			var lWorldY = ship.rearEngine.y * lUnitsToPixels;
			if (RandomNumbers.getRandomChance(50)) {
				lWorldX = ship.frontEngine.x * lUnitsToPixels;
				lWorldY = ship.frontEngine.y * lUnitsToPixels;
			}

			if (ship.health < lHighSmokeLevel && RandomNumbers.getRandomChance(30.f)) {
				mSmokeParticleSystem.spawnParticle(lWorldX, lWorldY, -.2f, 0, 0);
				mFireParticleSystem.spawnParticle(lWorldX, lWorldY, -.2f, 0, 0);
			}

			else if (ship.health < lMidSmokeLevel && RandomNumbers.getRandomChance(20.f)) {
				mSmokeParticleSystem.spawnParticle(lWorldX, lWorldY, -.2f, 0, 0);
				mFireParticleSystem.spawnParticle(lWorldX, lWorldY, -.2f, 0, 0);
			}

			else if (ship.health < lLowSmokeLevel && RandomNumbers.getRandomChance(10.f)) {
				mSmokeParticleSystem.spawnParticle(lWorldX, lWorldY, -.2f, 0, 0);
			}
		}

		// this assumes we can only collide with the level
		final var lShipUserData = (ShipPhysicsData) ship.body().userData();
		if (lShipUserData.lastCollisionHandled == false) {

			final float dot = Vector2f.dot(lAdjustedAngleX, lAdjustedAngleY, lShipUserData.lastCollisionNormalX, lShipUserData.lastCollisionNormalY);

			if (dot <= 0) { // top end of ship
				final int mag = (int) Math.sqrt(lShipUserData.lastCollisionMagnitude2);
				if (mag > DAMAGE_TOP_THREASHOLD) {
					ship.applyDamage(mag);
				}
			} else {
				final int mag = (int) Math.sqrt(lShipUserData.lastCollisionMagnitude2);
				if (mag > DAMAGE_BOTTOM_THREASHOLD) {
					ship.applyDamage(mag - DAMAGE_BOTTOM_THREASHOLD);
				}
			}

			final float lWorldX = lShipUserData.lastCollisionContactX * lUnitsToPixels;
			final float lWorldY = lShipUserData.lastCollisionContactY * lUnitsToPixels;

			final var lLen = 20.f + (float) Math.sqrt(body.vx * body.vx + body.vy * body.vy);
			final int lNumSparks = RandomNumbers.random(2, 7);
			for (int i = 0; i < lNumSparks; i++) {
				final float t = 1.f;
				final float lOffsetX = RandomNumbers.random(-t, t);
				final float lOffsetY = RandomNumbers.random(-t, t);

				mSparkParticleSystem.spawnParticle(lWorldX, lWorldY, -.2f, (lShipUserData.lastCollisionNormalX + lOffsetX) * lLen, (lShipUserData.lastCollisionNormalY + lOffsetY) * lLen);
			}

			lShipUserData.lastCollisionHandled = true;
			lShipUserData.lastCollisionMagnitude2 = 0.f;
		}

	}

	// DAMAGE ---------------------------------------------

	public void dealDamageToShip(int shipEntityUid, int damageAmount, float hitAngle) {
		final var lShipToDamage = getShipByEntityUid(shipEntityUid);
		if (lShipToDamage == null)
			return;
	}
}
