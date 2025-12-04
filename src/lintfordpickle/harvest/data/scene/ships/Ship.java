package lintfordpickle.harvest.data.scene.ships;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.GridCollisionTypes;
import lintfordpickle.harvest.data.input.ShipInput;
import lintfordpickle.harvest.data.scene.cargo.Cargo;
import lintfordpickle.harvest.data.scene.cargo.CargoType;
import lintfordpickle.harvest.data.scene.collisions.GridEntityType;
import lintfordpickle.harvest.data.scene.physics.ShipPhysicsData;
import lintfordpickle.harvest.renderers.trails.TrailRendererComponent;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.audio.AudioManager;
import net.lintfordlib.core.audio.AudioSource;
import net.lintfordlib.core.audio.data.AudioFileBase;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.RigidBody.BodyType;
import net.lintfordlib.core.physics.dynamics.RigidBodyEntity;
import net.lintfordlib.core.physics.shapes.PolygonShape;

public class Ship extends RigidBodyEntity {

	private static final long serialVersionUID = -4005560928733452992L;

	public class Inventory {

		// ---------------------------------------------
		// Constants
		// ---------------------------------------------

		public static final int TOTAL_CARGO_SPACE = 3;

		// ---------------------------------------------
		// Variables
		// ---------------------------------------------

		private final List<Cargo> mCargo = new ArrayList<>();

		// ---------------------------------------------
		// Properties
		// ---------------------------------------------

		public Cargo getCargoByIndex(int index) {
			return mCargo.get(index);
		}

		public boolean hasFreeSpace() {
			return mCargo.size() < TOTAL_CARGO_SPACE;
		}

		public int freeSpace() {
			return TOTAL_CARGO_SPACE - mCargo.size();
		}

		public Cargo removeCargoOfType(CargoType cargoType) {
			final var lNumCargo = mCargo.size();
			for (int i = 0; i < lNumCargo; i++) {
				if (mCargo.get(i).cargoType == cargoType)
					return mCargo.remove(i);
			}
			return null;
		}

		// ---------------------------------------------
		// Constructor
		// ---------------------------------------------

		public Inventory() {
		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		public void addCargo(Cargo newCargo) {
			if (hasFreeSpace() == false)
				return;

			if (mCargo.contains(newCargo))
				return;

			mCargo.add(newCargo);
		}

		public Cargo removeCargo(CargoType type) {
			final var lNumCargo = mCargo.size();
			for (int i = 0; i < lNumCargo; i++) {
				if (mCargo.get(i).cargoType == type) {
					return mCargo.remove(i);
				}
			}

			return null;
		}
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public class ShipAudioComponent {
		private boolean mAudioEnabled;

		private AudioFileBase mEngine00_TO;
		private AudioFileBase mEngine00_00;
		private AudioFileBase mEngine00_20;
		private AudioFileBase mEngine00_40;
		private AudioFileBase mEngine00_60;
		private AudioFileBase mEngine00_80;
		private AudioFileBase mEngine00_100;

		private float mCrossFade00Amt;
		private float mCrossFade20Amt;
		private float mCrossFade40Amt;
		private float mCrossFade60Amt;
		private float mCrossFade80Amt;
		private float mCrossFade100Amt;

		private float mInMinAmt = 0;
		private float mInMaxAmt = 100;
		private float mInAmt = 0;

		private float mPitch = 1.f;

		private int stepActive00;
		private int stepActive01;
		private int stepActive02;

		private AudioSource mEngineAudioSource0;
		private AudioSource mEngineAudioSource1;
		private AudioSource mEngineAudioSource2;

		public boolean audioEnabled() {
			return mAudioEnabled;
		}

		public void initialize(AudioManager audioManager) {
			mAudioEnabled = true;

			mEngine00_TO = audioManager.getAudioDataBufferByName("ENGINE00_TO");
			mEngine00_00 = audioManager.getAudioDataBufferByName("ENGINE00_00");
			mEngine00_20 = audioManager.getAudioDataBufferByName("ENGINE00_20");
			mEngine00_40 = audioManager.getAudioDataBufferByName("ENGINE00_40");
			mEngine00_60 = audioManager.getAudioDataBufferByName("ENGINE00_60");
			mEngine00_80 = audioManager.getAudioDataBufferByName("ENGINE00_80");
			mEngine00_100 = audioManager.getAudioDataBufferByName("ENGINE00_100");

			mEngineAudioSource0 = audioManager.getAudioSource(hashCode(), AudioManager.AUDIO_SOURCE_TYPE_SOUNDFX);
			mEngineAudioSource1 = audioManager.getAudioSource(hashCode(), AudioManager.AUDIO_SOURCE_TYPE_SOUNDFX);
			mEngineAudioSource2 = audioManager.getAudioSource(hashCode(), AudioManager.AUDIO_SOURCE_TYPE_SOUNDFX);

			mEngineAudioSource0.setLooping(true);
			mEngineAudioSource1.setLooping(true);
			mEngineAudioSource2.setLooping(true);

			mEngineAudioSource0.play(mEngine00_00.bufferID());
			stepActive00 = 0;
			stepActive01 = -1;
			stepActive02 = -1;

		}

		public void unloadResources() {
			if (mEngineAudioSource0 != null)
				mEngineAudioSource0.stop();

			if (mEngineAudioSource1 != null)
				mEngineAudioSource1.stop();

			if (mEngineAudioSource2 != null)
				mEngineAudioSource2.stop();

			mEngineAudioSource0 = null;
			mEngineAudioSource1 = null;
			mEngineAudioSource2 = null;

			mEngine00_TO = null;
			mEngine00_00 = null;
			mEngine00_20 = null;
			mEngine00_40 = null;
			mEngine00_60 = null;
			mEngine00_80 = null;
			mEngine00_100 = null;
		}

		public void update(LintfordCore core, Ship ship) {
			final float lInAmt = MathHelper.scaleToRange(ship.rollingThrottle, ship.rollingThrottleMin, ship.rollingThrottleMax, 0, 100);
			final float lStepSize = 20.f; // distance between samples on RPM scale

			mPitch = MathHelper.scaleToRange(ship.rollingThrottle * .5f, ship.rollingThrottleMin, ship.rollingThrottleMax, 0.2f, 0.5f);

			mCrossFade00Amt = getCrossFadeAmt(lInAmt, 00, lStepSize);
			mCrossFade20Amt = getCrossFadeAmt(lInAmt, 20, lStepSize);
			mCrossFade40Amt = getCrossFadeAmt(lInAmt, 40, lStepSize);
			mCrossFade60Amt = getCrossFadeAmt(lInAmt, 60, lStepSize);
			mCrossFade80Amt = getCrossFadeAmt(lInAmt, 80, lStepSize);
			mCrossFade100Amt = getCrossFadeAmt(lInAmt, 100, lStepSize);

			modifyCrossFadeOnChannel(mEngine00_00, mCrossFade00Amt, 0);
			modifyCrossFadeOnChannel(mEngine00_20, mCrossFade20Amt, 1);
			modifyCrossFadeOnChannel(mEngine00_40, mCrossFade40Amt, 2);
			modifyCrossFadeOnChannel(mEngine00_60, mCrossFade60Amt, 3);
			modifyCrossFadeOnChannel(mEngine00_80, mCrossFade80Amt, 4);
			modifyCrossFadeOnChannel(mEngine00_100, mCrossFade100Amt, 5);
		}

		private float getCrossFadeAmt(float inAmt, float lvlAmt, float maxSize) {
			final float relVal = Math.abs(inAmt - lvlAmt);
			return 1.f - MathHelper.scaleToRange(relVal, 0, maxSize, 0, 1);
		}

		private void modifyCrossFadeOnChannel(AudioFileBase audioData, float crossFadeAmt, int channelId) {
			if (crossFadeAmt > 0) {
				if (stepActive00 == channelId) {
					mEngineAudioSource0.setGain(crossFadeAmt);
				} else if (stepActive01 == channelId) {
					mEngineAudioSource1.setGain(crossFadeAmt);
				} else if (stepActive02 == channelId) {
					mEngineAudioSource2.setGain(crossFadeAmt);
				} else {
					if (stepActive00 == -1) {
						stepActive00 = channelId;
						mEngineAudioSource0.play(audioData.bufferID());
						mEngineAudioSource0.setGain(crossFadeAmt);
					} else if (stepActive01 == -1) {
						stepActive01 = channelId;
						mEngineAudioSource1.play(audioData.bufferID());
						mEngineAudioSource1.setGain(crossFadeAmt);
					} else if (stepActive02 == -1) {
						stepActive02 = channelId;
						mEngineAudioSource2.play(audioData.bufferID());
						mEngineAudioSource2.setGain(crossFadeAmt);
					}
				}

			} else {
				if (stepActive00 == channelId) {
					mEngineAudioSource0.setGain(crossFadeAmt);
					mEngineAudioSource0.stop();
					stepActive00 = -1;
				} else if (stepActive01 == channelId) {
					mEngineAudioSource1.setGain(crossFadeAmt);
					mEngineAudioSource1.stop();
					stepActive01 = -1;
				} else if (stepActive02 == channelId) {
					mEngineAudioSource2.setGain(crossFadeAmt);
					mEngineAudioSource2.stop();
					stepActive02 = -1;
				}
			}

			mEngineAudioSource0.setPitch(mPitch);
			mEngineAudioSource1.setPitch(mPitch);
			mEngineAudioSource2.setPitch(mPitch);
		}
	}

	public int tiltLevel;
	public float tiltAmount;
	public boolean isPlayerControlled;

	public float velocityMagnitude;

	public final ShipInput inputs = new ShipInput();
	public final Vector2f rearEngine = new Vector2f();
	public final Vector2f frontEngine = new Vector2f();

	public int owningPlayerSessionUid;

	// ghost ships are used in time-trial mode to show the relative state of a previous playthrough
	public boolean isGhostShip;

	public float engineColorR = 0.11f;
	public float engineColorG = 0.2f;
	public float engineColorB = 0.74f;

	public final TrailRendererComponent mRearTrailRendererComponent = new TrailRendererComponent();
	public final TrailRendererComponent mFrontTrailRendererComponent = new TrailRendererComponent();

	public final ShipAudioComponent audio = new ShipAudioComponent();

	public final Inventory cargo = new Inventory();
	public final int maxHealth = 100;
	public int health;

	public float rollingThrottle;
	public float rollingThrottleMin = 0.f;
	public float rollingThrottleMax = 100.f;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isDead() {
		return health <= 0;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public Ship(int entityUid) {
		super(entityUid, GridCollisionTypes.COLLISION_TYPE_SHIP);

		final var pixelsToUnits = ConstantsPhysics.PixelsToUnits();
		final var density = 2.f;

		final var width = 64.f * pixelsToUnits;
		final var height = 32.f * pixelsToUnits;

		final var restitution = .1f;

		final var staticFriction = .8f;
		final var dynamicFriction = .5f;

		body = new RigidBody(BodyType.Dynamic);
		body.addShape(PolygonShape.createBoxShape(width, height, 0.f, density, restitution, staticFriction, dynamicFriction));
		body.userData(new ShipPhysicsData(entityUid));

		body.categoryBits(GridEntityType.GRID_ENTITY_TYPE_SHIP);
		body.maskBits(GridEntityType.GRID_ENTITY_TYPE_PHYSICS_OBJECTS);

		health = maxHealth;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void update(LintfordCore core) {

		final var lPixelsToUnits = ConstantsPhysics.PixelsToUnits();

		final float s = (float) Math.sin(body.transform.angle);
		final float c = (float) Math.cos(body.transform.angle);

		final float axleLengthHalf = 24f * lPixelsToUnits;
		final float leftThrottleAmt = (inputs.isRightThrottle || inputs.isUpThrottle) ? 1.f : 0.f;
		final float rightThrottleAmt = (inputs.isLeftThrottle || inputs.isUpThrottle) ? 1.f : 0.f;
		final float axleHeightHalfL = (16f * leftThrottleAmt) * lPixelsToUnits;
		final float axleHeightHalfR = (16f * rightThrottleAmt) * lPixelsToUnits;

		final float lRearX = -axleLengthHalf * c - axleHeightHalfL * s;
		final float lRearY = -axleLengthHalf * s + axleHeightHalfL * c;

		final float lFrontX = axleLengthHalf * c - axleHeightHalfR * s;
		final float lFrontY = axleLengthHalf * s + axleHeightHalfR * c;

		rearEngine.set(body.transform.p.x + lRearX, body.transform.p.y + lRearY);
		frontEngine.set(body.transform.p.x + lFrontX, body.transform.p.y + lFrontY);

		updateEngineTrails(core);

		velocityMagnitude = body.vx * body.vx + body.vy * body.vy;
	}

	private void updateEngineTrails(LintfordCore core) {

		final var lUnitsToPixels = ConstantsPhysics.UnitsToPixels();
		final float rearWorldX = rearEngine.x * lUnitsToPixels;
		final float rearWorldY = rearEngine.y * lUnitsToPixels;
		final float frontWorldX = frontEngine.x * lUnitsToPixels;
		final float frontWorldY = frontEngine.y * lUnitsToPixels;

		mFrontTrailRendererComponent.color(engineColorR, engineColorG, engineColorB, .5f);
		mFrontTrailRendererComponent.updateTrail(core, rearWorldX, rearWorldY, body().transform.angle);

		mRearTrailRendererComponent.color(engineColorR, engineColorG, engineColorB, .5f);
		mRearTrailRendererComponent.updateTrail(core, frontWorldX, frontWorldY, body().transform.angle);
	}

	public void applyDamage(int amt) {
		health -= amt;
	}

}
