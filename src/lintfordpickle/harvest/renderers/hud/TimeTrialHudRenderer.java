package lintfordpickle.harvest.renderers.hud;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.GameStateController;
import lintfordpickle.harvest.controllers.ShipController;
import lintfordpickle.harvest.data.game.GameState;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.core.time.TimeConstants;
import net.lintfordlib.renderers.RendererManagerBase;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiBar;

public class TimeTrialHudRenderer extends UiWindow {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Hud UiWindow";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;
	private ShipController mShipController;

	private GameState mGameState;
	private SpriteSheetDefinition mHudSpritesheet;
	private UiBar mHealthBar;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mGameStateController != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TimeTrialHudRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mHealthBar = new UiBar(0.f, 100.f);
		mIsOpen = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();

		mShipController = (ShipController) lControllerManager.getControllerByNameRequired(ShipController.CONTROLLER_NAME, entityGroupUid());
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupUid());

		mGameState = mGameStateController.gameState();
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mHudSpritesheet = resourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_HUD", ConstantsGame.GAME_RESOURCE_GROUP_ID);

	}

	@Override
	public void unloadResources() {
		super.unloadResources();
	}

	@Override
	public boolean handleInput(LintfordCore core) {

		return super.handleInput(core);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		final var hudBoundingBox = core.HUD().boundingRectangle();

		final var fontUnit = core.sharedResources().uiTitleFont();
		final var spriteBatch = core.sharedResources().uiSpriteBatch();

		spriteBatch.begin(core.HUD());

		final var timeRemaining = mGameStateController.gameState().gameTimer;

		var tempTime = timeRemaining;
		final var totalMinutes = (int) tempTime / TimeConstants.MillisPerMinute;
		tempTime -= totalMinutes * TimeConstants.MillisPerMinute;
		final var totalSeconds = (int) tempTime / TimeConstants.MillisPerSecond;
		tempTime -= totalSeconds * TimeConstants.MillisPerSecond;

		final var timeFormatted = String.format(java.util.Locale.US, "%02d", totalMinutes) + ":" + String.format(java.util.Locale.US, "%02d", totalSeconds) + ":" + String.format(java.util.Locale.US, "%1.0f", tempTime);

		fontUnit.begin(core.HUD());
		spriteBatch.setColorWhite();
		spriteBatch.draw(mHudSpritesheet, mHudSpritesheet.getSpriteFrame("TEXTURE_CLOCK"), hudBoundingBox.left() + 5.f, hudBoundingBox.top() + 5.0f, 32, 32, .01f);
		fontUnit.drawText(": " + timeFormatted, hudBoundingBox.left() + 38.f, hudBoundingBox.top() + 5.0f, -0.01f, 1.f);
		var gridPositionY = hudBoundingBox.top() + 16.0f;

		// Player Stats
		drawPlatformStatus(core, fontUnit, spriteBatch, hudBoundingBox.left() + 10.f, gridPositionY += 32.f, 1);
		drawPlatformStatus(core, fontUnit, spriteBatch, hudBoundingBox.left() + 10.f, gridPositionY += 32.f, 2);
		drawPlatformStatus(core, fontUnit, spriteBatch, hudBoundingBox.left() + 10.f, gridPositionY += 32.f, 3);
		drawPlatformStatus(core, fontUnit, spriteBatch, hudBoundingBox.left() + 10.f, gridPositionY += 32.f, 4);

		spriteBatch.setColorWhite();
		spriteBatch.draw(mHudSpritesheet, mHudSpritesheet.getSpriteFrame("TEXTURE_SPANNER"), hudBoundingBox.right() - 5.f - 32f, hudBoundingBox.top() + 5.0f, 32, 32, .01f);
		final var lShip = mShipController.shipManager().playerShip();

		final var lHealthBarWidth = 196.f - 32.f;
		mHealthBar.innerBorderPadding(2);
		mHealthBar.setInnerColor(0.92f, 0.07f, 0.04f, 1.f);
		mHealthBar.setDestRectangle(hudBoundingBox.right() - 10 - lHealthBarWidth - 32, hudBoundingBox.top() + 12, lHealthBarWidth, 20);
		mHealthBar.setCurrentValue(lShip.health);
		mHealthBar.setMinMax(0, 100);
		mHealthBar.draw(core, spriteBatch, fontUnit, -0.01f);

		fontUnit.end();
		spriteBatch.end();

	}

	private void drawPlatformStatus(LintfordCore core, FontUnit font, SpriteBatch spriteBatch, float x, float y, int platformNr) {
		font.drawText(String.valueOf(platformNr), x, y, -0.01f, 1.f);

		// TODO: Show both players
		final var lPlayerScorecard = mGameState.getScoreCard(0);

		final var waterColor = lPlayerScorecard.isPlatformWatered(platformNr) ? ColorConstants.WHITE() : ColorConstants.GREY_DARK();
		spriteBatch.setColor(waterColor);
		spriteBatch.draw(mHudSpritesheet, mHudSpritesheet.getSpriteFrame("TEXTURE_WATER"), x + 24.f, y, 32, 32, .01f);

		final var wheatColor = lPlayerScorecard.isPlatformHarvested(platformNr) ? ColorConstants.WHITE() : ColorConstants.GREY_DARK();
		spriteBatch.setColor(wheatColor);
		spriteBatch.draw(mHudSpritesheet, mHudSpritesheet.getSpriteFrame("TEXTURE_WHEAT"), x + 48.f, y, 32, 32, .01f);
	}

}