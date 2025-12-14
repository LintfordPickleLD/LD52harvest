package lintfordpickle.harvest.renderers;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.ShipController;
import lintfordpickle.harvest.data.scene.cargo.CargoType;
import lintfordpickle.harvest.data.scene.ships.Ship;
import lintfordpickle.harvest.data.scene.ships.Ship.Inventory;
import lintfordpickle.harvest.renderers.trails.TrailBatchRenderer;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.sprites.SpriteFrame;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class ShipRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Ship Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ShipController mShipController;

	private SpriteSheetDefinition mShipSpritesheet;

	private TrailBatchRenderer mTrailRenderer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mShipController != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ShipRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mTrailRenderer = new TrailBatchRenderer();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mShipController = (ShipController) core.controllerManager().getControllerByNameRequired(ShipController.CONTROLLER_NAME, entityGroupUid());
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mShipSpritesheet = resourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_PROPS", ConstantsGame.GAME_RESOURCE_GROUP_ID);

		mTrailRenderer.loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mTrailRenderer.unloadResources();
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		final var lShipManager = mShipController.shipManager();
		final var lShips = lShipManager.ships();
		final var lNumShips = lShips.size();
		for (int i = 0; i < lNumShips; i++) {
			final var lShip = lShips.get(i);

			drawShip(core, lShip);
		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawShip(LintfordCore core, Ship ship) {
		if (ship == null)
			return;

		final var spritebatch = core.sharedResources().uiSpriteBatch();

		spritebatch.begin(core.gameCamera());

		drawShipEngines(core, spritebatch, ship, ship.rearEngine, true);
		drawShipEngines(core, spritebatch, ship, ship.frontEngine, false);
		drawShipCargo(core, spritebatch, ship);

		drawShipComponents(core, ship, spritebatch);

		spritebatch.end();

		if (ConstantsGame.SHIP_DEBUG_MODE)
			drawShipDebugInfo(core, ship);
	}

	// ---------------------------------------------

	private void drawShipComponents(LintfordCore core, Ship ship, SpriteBatch spriteBatch) {
		{// MainBody
			final var unitsToPixels = ConstantsPhysics.UnitsToPixels();
			final var spriteFrame = mShipSpritesheet.getSpriteFrame("HARVESTER");

			final var destW = spriteFrame.width();
			final var destH = spriteFrame.height();

			final var lBody = ship.body();

			final var shipPosX = lBody.transform.p.x * unitsToPixels;
			final var shipPosY = lBody.transform.p.y * unitsToPixels;
			final var shipPosRot = lBody.transform.angle;

			var shipColor = ColorConstants.WHITE();
			if (ship.isGhostShip)
				shipColor = ColorConstants.getBlackWithAlpha(0.3f);

			spriteBatch.setColor(shipColor);
			spriteBatch.drawAroundCenter(mShipSpritesheet, spriteFrame, shipPosX, shipPosY, destW, destH, shipPosRot, 0f, 0f, 1f);
		}
	}

	private void drawShipEngines(LintfordCore core, SpriteBatch spriteBatch, Ship ship, Vector2f enginePostion, boolean leftEngine) {
		// Engine Glow
		final var body = ship.body();

		final var unitsToPixels = ConstantsPhysics.UnitsToPixels();

		final var shipPosX = enginePostion.x * unitsToPixels;
		final var shipPosY = enginePostion.y * unitsToPixels;

		final var r = ship.engineColorR * 2f;
		final var g = ship.engineColorG * 2f;
		final var b = ship.engineColorB * 2f;
		final var engineColor = ColorConstants.getColor(r, g, b, 0.75f);

		final float pulse = 1.0f + (float) Math.cos(core.gameTime().totalTimeMilli()) * 2.f;

		final float shipSpeed = (float) Math.abs(body.vx * body.vx + body.vy * body.vy) * 20.f;
		final float speedSizeMod = MathHelper.clamp(2.0f + shipSpeed * .02f + pulse, 0.f, 24.f);

		// engien lens flare/ glow effect

		final var lSpriteFrameFlare = mShipSpritesheet.getSpriteFrame("TEXTUREENGINEGLOW");

		var shipColor = ColorConstants.WHITE();
		if (ship.isGhostShip)
			shipColor = ColorConstants.getBlackWithAlpha(0.3f);

		if (leftEngine) {
			if (ship.inputs.isLeftThrottle || ship.inputs.isUpThrottle) {
				spriteBatch.setColor(engineColor);
				spriteBatch.drawAroundCenter(mShipSpritesheet, lSpriteFrameFlare, shipPosX, shipPosY, 4 * speedSizeMod, 2, 0, 0, 0, .01f);

				spriteBatch.setColor(shipColor);
				spriteBatch.drawAroundCenter(mShipSpritesheet, lSpriteFrameFlare, shipPosX, shipPosY, 4 * speedSizeMod * .5f, 2, 0, 0, 0, .01f);
			}
		} else {
			if (ship.inputs.isRightThrottle || ship.inputs.isUpThrottle) {
				spriteBatch.setColor(engineColor);
				spriteBatch.drawAroundCenter(mShipSpritesheet, lSpriteFrameFlare, shipPosX, shipPosY, 4 * speedSizeMod, 2, 0, 0, 0, .01f);

				spriteBatch.setColor(shipColor);
				spriteBatch.drawAroundCenter(mShipSpritesheet, lSpriteFrameFlare, shipPosX, shipPosY, 4 * speedSizeMod * .5f, 2, 0, 0, 0, .01f);
			}
		}
	}

	private void drawShipCargo(LintfordCore core, SpriteBatch spriteBatch, Ship ship) {
		final var lBody = ship.body();

		final var lUnitsToPixels = ConstantsPhysics.UnitsToPixels();

		float xx = 12;
		float yy = -3;

		final int lTotalNumberCargo = Inventory.TOTAL_CARGO_SPACE - ship.cargo.freeSpace();
		for (int i = 0; i < lTotalNumberCargo; i++) {
			final var lCargo = ship.cargo.getCargoByIndex(i);

			final SpriteFrame lSpriteFrameFlare;
			if (lCargo.cargoType == CargoType.Water) {
				lSpriteFrameFlare = mShipSpritesheet.getSpriteFrame("TEXTURECARGOWATER");
			} else {
				lSpriteFrameFlare = mShipSpritesheet.getSpriteFrame("TEXTURECARGOWHEAT");
			}

			final var shipPosX = lBody.transform.p.x * lUnitsToPixels;
			final var shipPosY = lBody.transform.p.y * lUnitsToPixels;
			final var shipRot = lBody.transform.angle;

			float lLocalHalfX = xx - i * 11;
			float lLocalHalfY = yy;

			final float c = (float) Math.cos(shipRot);
			final float s = (float) Math.sin(shipRot);

			final float cargoX = -lLocalHalfX * c - lLocalHalfY * s;
			final float cargoY = -lLocalHalfX * s + lLocalHalfY * c;
			final float cargoW = lSpriteFrameFlare.width() * 2;
			final float cargoH = lSpriteFrameFlare.height() * 2;

			spriteBatch.setColorWhite();
			spriteBatch.drawAroundCenter(mShipSpritesheet, lSpriteFrameFlare, shipPosX + cargoX, shipPosY + cargoY, cargoW, cargoH, shipRot, 0, 0, .01f);
		}
	}

	private void drawShipDebugInfo(LintfordCore core, Ship ship) {
		if (ship.isPlayerControlled) {
			final var body = ship.body();

			final var fontUnit = core.sharedResources().uiTextFont();
			final var boundingBox = core.HUD().boundingRectangle();

			final var shipAX = String.format(java.util.Locale.US, "%.1f", body.accX);
			final var shipAY = String.format(java.util.Locale.US, "%.1f", body.accY);

			final var shipVX = String.format(java.util.Locale.US, "%.1f", body.vx);
			final var shipVY = String.format(java.util.Locale.US, "%.1f", body.vy);

			final var shipX = String.format(java.util.Locale.US, "%.1f", body.transform.p.x);
			final var shipY = String.format(java.util.Locale.US, "%.1f", body.transform.p.y);

			final var shipTorque = String.format(java.util.Locale.US, "%.1f", body.torque);
			final var shipAV = String.format(java.util.Locale.US, "%.1f", body.angularVelocity());
			final var shipR = String.format(java.util.Locale.US, "%.3f", body.transform.angle);

			final var fontScale = 1.0f;
			final var lineHeight = 18.f;

			float yPos = boundingBox.top() + 5.f - 20.f;

			fontUnit.begin(core.HUD());
			fontUnit.drawText("force: " + shipAX + "," + shipAY, boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);
			fontUnit.drawText("velocity: " + shipVX + "," + shipVY, boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);
			fontUnit.drawText("position: " + shipX + "," + shipY, boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);

			fontUnit.drawText(" ", boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);

			fontUnit.drawText("torque: " + shipTorque, boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);
			fontUnit.drawText("angular: " + shipAV, boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);
			fontUnit.drawText("angle: " + shipR, boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);

			fontUnit.drawText(" ", boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);

			fontUnit.drawText("free space: " + ship.cargo.freeSpace(), boundingBox.left() + 5.f, yPos += lineHeight, .01f, fontScale);
			fontUnit.end();
		}
	}
}
