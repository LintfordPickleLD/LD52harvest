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

			// this shit is for the ship's engine sound synth.
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
		final var shipInput = ship.inputs;

		// TODO: extract these somehow and make them part of the game mechanics
		// These two control the responsiveness of the controls almost completely.
		final var thrustUpForce = 15.f;
		final var angularTorque = .0015f;

		final var unitsToPixels = ConstantsPhysics.UnitsToPixels();
		final var shipAngle = body.transform.angle;

		if (shipInput.isUpThrottle) {

			final var lUpAngleX = body.transform.q.c;
			final var lUpAngleY = body.transform.q.s;

			body.accX += -lUpAngleY * -thrustUpForce * body.invMass();
			body.accY += lUpAngleX * -thrustUpForce * body.invMass();
			body.setAngularVelocity(ship.body().angularVelocity() * .99f);

		}

		if (shipInput.isLeftThrottle) {
			body.transform.angle -= angularTorque * core.gameTime().elapsedTimeMilli();
			if (body.angularVelocity() > 0.f)
				body.setAngularVelocity(body.angularVelocity() * .9f);

		}

		if (shipInput.isRightThrottle) {
			body.transform.angle += angularTorque * core.gameTime().elapsedTimeMilli();
			if (body.angularVelocity() < 0.f)
				body.setAngularVelocity(body.angularVelocity() * .9f);

		}

		updateShipCollisions(ship);

		// Particles
		if (DEBUG_DISABLE_PARTICLES)
			return;

		// smoke, fire etc.
		updateShipStatusParticles(ship);

		if (shipInput.isUpThrottle) {
			final var frontEnginePositionX = ship.frontEngine.x * unitsToPixels;
			final var frontEnginePositionY = ship.frontEngine.y * unitsToPixels;

			updateEngineParticles(frontEnginePositionX, frontEnginePositionY, body.vx, body.vy, shipAngle);

			final var rearEnginePositionX = ship.rearEngine.x * unitsToPixels;
			final var rearEnginePositionY = ship.rearEngine.y * unitsToPixels;

			updateEngineParticles(rearEnginePositionX, rearEnginePositionY, body.vx, body.vy, shipAngle);

		}

		if (shipInput.isLeftThrottle) {
			final var frontEnginePositionX = ship.frontEngine.x * unitsToPixels;
			final var frontEnginePositionY = ship.frontEngine.y * unitsToPixels;

			updateEngineParticles(frontEnginePositionX, frontEnginePositionY, body.vx, body.vy, shipAngle);
		}

		if (shipInput.isRightThrottle) {
			final var rearEnginePositionX = ship.rearEngine.x * unitsToPixels;
			final var rearEnginePositionY = ship.rearEngine.y * unitsToPixels;
			updateEngineParticles(rearEnginePositionX, rearEnginePositionY, body.vx, body.vy, shipAngle);
		}

	}

	private void updateShipCollisions(Ship ship) {

		// this assumes we can only collide with the level

		final var body = ship.body();
		final var unitsToPixels = ConstantsPhysics.UnitsToPixels();

		final var shipUserData = (ShipPhysicsData) ship.body().userData();
		if (shipUserData.lastCollisionHandled == false) {

			final float lAdjustedAngle = body.transform.angle + (float) Math.toRadians(90.f);
			final float lAdjustedAngleX = (float) Math.cos(lAdjustedAngle);
			final float lAdjustedAngleY = (float) Math.sin(lAdjustedAngle);

			final float dot = Vector2f.dot(lAdjustedAngleX, lAdjustedAngleY, shipUserData.lastCollisionNormalX, shipUserData.lastCollisionNormalY);

			if (dot <= 0) { // top end of ship
				final int mag = (int) Math.sqrt(shipUserData.lastCollisionMagnitude2);
				if (mag > DAMAGE_TOP_THREASHOLD) {
					ship.applyDamage(mag);
				}
			} else {
				final int mag = (int) Math.sqrt(shipUserData.lastCollisionMagnitude2);
				if (mag > DAMAGE_BOTTOM_THREASHOLD) {
					ship.applyDamage(mag - DAMAGE_BOTTOM_THREASHOLD);
				}
			}

			final float lWorldX = shipUserData.lastCollisionContactX * unitsToPixels;
			final float lWorldY = shipUserData.lastCollisionContactY * unitsToPixels;

			final var lLen = 20.f + (float) Math.sqrt(body.vx * body.vx + body.vy * body.vy);
			final int lNumSparks = RandomNumbers.random(2, 7);
			for (int i = 0; i < lNumSparks; i++) {
				final float t = 1.f;
				final float lOffsetX = RandomNumbers.random(-t, t);
				final float lOffsetY = RandomNumbers.random(-t, t);

				mSparkParticleSystem.spawnParticle(lWorldX, lWorldY, -.2f, (shipUserData.lastCollisionNormalX + lOffsetX) * lLen, (shipUserData.lastCollisionNormalY + lOffsetY) * lLen);
			}

			shipUserData.lastCollisionHandled = true;
			shipUserData.lastCollisionMagnitude2 = 0.f;
		}
	}

	private void updateShipStatusParticles(Ship ship) {
		if (DEBUG_DISABLE_PARTICLES)
			return;

		final var unitsToPixels = ConstantsPhysics.UnitsToPixels();

		final var lStep = ship.maxHealth / 10.f;
		final var lLowSmokeLevel = ship.maxHealth - lStep;
		final var lMidSmokeLevel = ship.maxHealth - lStep * 2;
		final var lHighSmokeLevel = ship.maxHealth - lStep * 3;

		var lWorldX = ship.rearEngine.x * unitsToPixels;
		var lWorldY = ship.rearEngine.y * unitsToPixels;
		if (RandomNumbers.getRandomChance(50)) {
			lWorldX = ship.frontEngine.x * unitsToPixels;
			lWorldY = ship.frontEngine.y * unitsToPixels;
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

	private void updateEngineParticles(float posX, float posY, float velX, float velY, float angle) {
		if (DEBUG_DISABLE_PARTICLES)
			return;

		final float unitsToPixels = ConstantsPhysics.UnitsToPixels();

		final float jetForce = 50.f;
		final float sparkForce = 20.f;

		final float adjustedVx = velX * unitsToPixels * .5f;
		final float adjustedVy = velY * unitsToPixels * .5f;

		final float adjustedAngle = angle + (float) Math.toRadians(90.f);
		final float adjustedAngleX = (float) Math.cos(adjustedAngle);
		final float adjustedAngleY = (float) Math.sin(adjustedAngle);

		mJetParticleSystem.spawnParticle(posX, posY, -.2f, adjustedVx + adjustedAngleX * jetForce, adjustedVy + adjustedAngleY * jetForce);
		mJetIntenseParticleSystem.spawnParticle(posX, posY, -.2f, adjustedVx + adjustedAngleX * jetForce, adjustedVy + adjustedAngleY * jetForce);

		{
			final float sparkChance = 32.8f;
			if (RandomNumbers.getRandomChance(sparkChance)) {
				final float maxAngle = 10.f;
				final float angleOffset = RandomNumbers.random(-maxAngle, maxAngle);

				final float sparkAngleX = (float) Math.cos(angle + angleOffset);
				final float sparkAngleY = (float) Math.sin(angle + angleOffset);

				mEngineSparkParticleSystem.spawnParticle(posX, posY, -.2f, (sparkAngleX) * sparkForce, (sparkAngleY) * sparkForce);
			}

		}
	}

	// DAMAGE ---------------------------------------------

	public void dealDamageToShip(int shipEntityUid, int damageAmount, float hitAngle) {
		final var lShipToDamage = getShipByEntityUid(shipEntityUid);
		if (lShipToDamage == null)
			return;
	}
}
