package lintfordpickle.harvest.renderers.hud;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.GameStateController;
import lintfordpickle.harvest.controllers.PlatformController;
import lintfordpickle.harvest.controllers.SceneController;
import lintfordpickle.harvest.controllers.ShipController;
import lintfordpickle.harvest.data.scene.SceneSettingsManager;
import lintfordpickle.harvest.data.scene.platforms.PlatformType;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetManager;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.RendererManagerBase;
import net.lintfordlib.renderers.windows.UiWindow;

public class MinimapRenderer extends UiWindow implements IInputProcessor {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Minimap UiWindow";

	public static enum MinimapSize {
		Off, Small, // 128
		Large // 256
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;
	private ShipController mShipController;
	private SceneSettingsManager mSceneSettingsManager;
	private PlatformController mPlatformsController;

	private SpriteSheetDefinition mCoreSpritesheet;
	private Texture mMinimapTexture;
	private float mInputTimer;

	private float mSceneWidthPx;
	private float mSceneHeightPx;

	private float mZDepth;
	private MinimapSize mMinimapSize;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mGameStateController != null;

	}

	public void setSize(MinimapSize size) {
		if (size == null) {
			mMinimapSize = MinimapSize.Off;
			return;

		}

		mMinimapSize = size;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public MinimapRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mMinimapSize = MinimapSize.Small;

		mZDepth = .01f;
		mIsOpen = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------ww

	@Override
	public void initialize(LintfordCore core) {
		final var controllerManager = core.controllerManager();

		mPlatformsController = (PlatformController) controllerManager.getControllerByNameRequired(PlatformController.CONTROLLER_NAME, entityGroupUid());
		mShipController = (ShipController) controllerManager.getControllerByNameRequired(ShipController.CONTROLLER_NAME, entityGroupUid());
		mGameStateController = (GameStateController) controllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupUid());
		final var sceneController = (SceneController) controllerManager.getControllerByNameRequired(SceneController.CONTROLLER_NAME, entityGroupUid());

		mSceneSettingsManager = sceneController.sceneData().sceneSettingsManager();
		mSceneWidthPx = mSceneSettingsManager.sceneWidthInPx();
		mSceneHeightPx = mSceneSettingsManager.sceneHeightInPx();
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mCoreSpritesheet = resourceManager.spriteSheetManager().getSpriteSheet(SpriteSheetManager.CORE_SPRITESHEET_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
		mMinimapTexture = resourceManager.textureManager().getTexture("TEXTURE_MINIMAP", ConstantsGame.GAME_RESOURCE_GROUP_ID);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mPlatformsController = null;
		mShipController = null;
		mGameStateController = null;

		mSceneSettingsManager = null;

		mCoreSpritesheet = null;
		mMinimapTexture = null;
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_M, this)) {
			switch (mMinimapSize) {
			case Off:
				mMinimapSize = MinimapSize.Small;
				break;
			case Small:
				mMinimapSize = MinimapSize.Large;
				break;
			case Large:
				mMinimapSize = MinimapSize.Off;
				break;
			}
		}

		return super.handleInput(core);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (mInputTimer > 0.f)
			mInputTimer -= core.gameTime().elapsedTimeMilli();

	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {

		if (mMinimapSize == MinimapSize.Off)
			return;

		final var hudBoundingBox = core.HUD().boundingRectangle();

		final var fontUnit = core.sharedResources().uiTitleFont();
		final var spriteBatch = core.sharedResources().uiSpriteBatch();

		spriteBatch.begin(core.HUD());

		var minimapSizePx = 128;
		if (mMinimapSize == MinimapSize.Large) {
			minimapSizePx = 256;
		}

		final var minimapPositionX = hudBoundingBox.right() - 10.f - minimapSizePx;
		final var minimapPositionY = hudBoundingBox.top() + 48.0f;
		final var minimapHudColor = ColorConstants.getWhiteWithAlpha(0.95f);

		fontUnit.begin(core.HUD());
		spriteBatch.setColor(minimapHudColor);
		spriteBatch.draw(mMinimapTexture, 0, 0, 196, 196, minimapPositionX, minimapPositionY, minimapSizePx, minimapSizePx, mZDepth);

		drawMinimapPlatformPositions(core, spriteBatch, minimapPositionX, minimapPositionY, minimapSizePx);

		drawMinimapShipPositions(core, spriteBatch, minimapPositionX, minimapPositionY, minimapSizePx);

		fontUnit.end();
		spriteBatch.end();

	}

	private void drawMinimapShipPositions(LintfordCore core, SpriteBatch spriteBatch, float minimapPosX, float minimapPosY, float minimapSize) {
		final var sceneHalfWidthPx = mSceneWidthPx / 2.f;
		final var sceneHalfHeightPx = mSceneHeightPx / 2.f;

		int entitySize = 2;
		if (mMinimapSize == MinimapSize.Large)
			entitySize = 4;

		final var shipManager = mShipController.shipManager();
		final var ships = shipManager.ships();
		final var numShips = ships.size();
		for (int i = 0; i < numShips; i++) {
			final var ship = ships.get(i);
			final var worldPositionX = ship.body().transform.p.x * ConstantsPhysics.UnitsToPixels();
			final var worldPositionY = ship.body().transform.p.y * ConstantsPhysics.UnitsToPixels();

			final var scaledPositionX = MathHelper.scaleToRange(worldPositionX, -sceneHalfWidthPx, sceneHalfWidthPx, 0, minimapSize);
			final var scaledPositionY = MathHelper.scaleToRange(worldPositionY, -sceneHalfHeightPx, sceneHalfHeightPx, 0, minimapSize);

			var shipColor = ship.isPlayerControlled ? ColorConstants.RED() : ColorConstants.GREY_DARK();
			spriteBatch.setColor(shipColor);
			spriteBatch.draw(mCoreSpritesheet, mCoreSpritesheet.getSpriteFrame("TEXTURE_WHITE"), minimapPosX + scaledPositionX, minimapPosY + scaledPositionY, entitySize, entitySize, mZDepth);
		}

	}

	private void drawMinimapPlatformPositions(LintfordCore core, SpriteBatch spriteBatch, float minimapPosX, float minimapPosY, float minimapSize) {
		final var sceneHalfWidthPx = mSceneWidthPx / 2.f;
		final var sceneHalfHeightPx = mSceneHeightPx / 2.f;

		int entitySize = 2;
		if (mMinimapSize == MinimapSize.Large)
			entitySize = 4;

		final var platformManager = mPlatformsController.platformManager();
		final var platforms = platformManager.platforms();
		final var numPlatforms = platforms.size();
		for (int i = 0; i < numPlatforms; i++) {
			final var platform = platforms.get(i);

			final var scaledPositionX = MathHelper.scaleToRange(platform.x(), -sceneHalfWidthPx, sceneHalfWidthPx, 0, minimapSize);
			final var scaledPositionY = MathHelper.scaleToRange(platform.y(), -sceneHalfHeightPx, sceneHalfHeightPx, 0, minimapSize);

			var shipColor = ColorConstants.GREEN();
			if (platform.platformType == PlatformType.Warehouse) {
				shipColor = ColorConstants.YELLOW();
			} else if (platform.platformType == PlatformType.Water) {
				shipColor = ColorConstants.BLUE();
			}

			spriteBatch.setColor(shipColor);
			spriteBatch.draw(mCoreSpritesheet, mCoreSpritesheet.getSpriteFrame("TEXTURE_WHITE"), minimapPosX + scaledPositionX, minimapPosY + scaledPositionY, entitySize, entitySize, mZDepth);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mInputTimer = IInputProcessor.INPUT_COOLDOWN_TIME;
	}

	@Override
	public boolean allowGamepadInput() {
		return true;
	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}
}
