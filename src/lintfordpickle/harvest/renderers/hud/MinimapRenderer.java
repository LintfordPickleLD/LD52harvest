package lintfordpickle.harvest.renderers.hud;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.GameStateController;
import lintfordpickle.harvest.controllers.PlatformController;
import lintfordpickle.harvest.controllers.ShipController;
import lintfordpickle.harvest.data.scene.platforms.PlatformType;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
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

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;
	private ShipController mShipController;
	private PlatformController mPlatformsController;

	private SpriteSheetDefinition mCoreSpritesheet;
	private Texture mMinimapTexture;
	private float mInputTimer;

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

	public MinimapRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mIsOpen = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------ww

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();

		mPlatformsController = (PlatformController) lControllerManager.getControllerByNameRequired(PlatformController.CONTROLLER_NAME, entityGroupUid());
		mShipController = (ShipController) lControllerManager.getControllerByNameRequired(ShipController.CONTROLLER_NAME, entityGroupUid());
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupUid());
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
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_M, this)) {
			mIsOpen = !mIsOpen;
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
		final var hudBoundingBox = core.HUD().boundingRectangle();

		final var fontUnit = core.sharedResources().uiTitleFont();
		final var spriteBatch = core.sharedResources().uiSpriteBatch();

		spriteBatch.begin(core.HUD());

		final var size = 196;

		final var minimapPositionX = hudBoundingBox.right() - 10.f - size;
		final var minimapPositionY = hudBoundingBox.top() + 48.0f;
		final var minimapHudColor = ColorConstants.getWhiteWithAlpha(0.75f);

		fontUnit.begin(core.HUD());
		spriteBatch.setColor(minimapHudColor);
		spriteBatch.draw(mMinimapTexture, 0, 0, 196, 196, minimapPositionX, minimapPositionY, size, size, .01f);

		final var platformManager = mPlatformsController.platformManager();
		final var platforms = platformManager.platforms();
		final var numPlatforms = platforms.size();
		for (int i = 0; i < numPlatforms; i++) {
			final var lPlatform = platforms.get(i);
			final var lWorldPositionX = lPlatform.x();
			final var lWorldPositionY = lPlatform.y();

			final var lScaledPositionX = MathHelper.scaleToRange(lWorldPositionX, -1024, 1024, 0, size);
			final var lScaledPositionY = MathHelper.scaleToRange(lWorldPositionY, -1024, 1024, 0, size);

			var shipColor = ColorConstants.GREEN();
			if (lPlatform.platformType == PlatformType.Warehouse) {
				shipColor = ColorConstants.YELLOW();
			} else if (lPlatform.platformType == PlatformType.Water) {
				shipColor = ColorConstants.BLUE();
			}

			spriteBatch.setColor(shipColor);
			spriteBatch.draw(mCoreSpritesheet, mCoreSpritesheet.getSpriteFrame("TEXTURE_WHITE"), minimapPositionX + lScaledPositionX, minimapPositionY + lScaledPositionY, 4, 4, .01f);
		}

		final var lShipManager = mShipController.shipManager();
		final var lShips = lShipManager.ships();
		final var lNumShips = lShips.size();
		for (int i = 0; i < lNumShips; i++) {
			final var lShip = lShips.get(i);
			final var lWorldPositionX = lShip.body().transform.p.x * ConstantsPhysics.UnitsToPixels();
			final var lWorldPositionY = lShip.body().transform.p.y * ConstantsPhysics.UnitsToPixels();

			final var lScaledPositionX = MathHelper.scaleToRange(lWorldPositionX, -1024, 1024, 0, size);
			final var lScaledPositionY = MathHelper.scaleToRange(lWorldPositionY, -1024, 1024, 0, size);

			var shipColor = lShip.isPlayerControlled ? ColorConstants.RED() : ColorConstants.GREY_DARK();
			spriteBatch.setColor(shipColor);
			spriteBatch.draw(mCoreSpritesheet, mCoreSpritesheet.getSpriteFrame("TEXTURE_WHITE"), minimapPositionX + lScaledPositionX, minimapPositionY + lScaledPositionY, 4, 4, .01f);
		}

		fontUnit.end();
		spriteBatch.end();

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
