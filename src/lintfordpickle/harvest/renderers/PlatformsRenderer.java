package lintfordpickle.harvest.renderers;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.PlatformController;
import lintfordpickle.harvest.data.scene.platforms.PlatformInstance;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class PlatformsRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Platforms Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private PlatformController mPlatformsController;
	private SpriteSheetDefinition mPlatformsSpritesheet;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mPlatformsController != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PlatformsRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mPlatformsController = (PlatformController) core.controllerManager().getControllerByNameRequired(PlatformController.CONTROLLER_NAME, entityGroupUid());
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mPlatformsSpritesheet = resourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_PROPS", ConstantsGame.GAME_RESOURCE_GROUP_ID);

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
		final var lPlatformManager = mPlatformsController.platformManager();
		final var lPlatformsList = lPlatformManager.platforms();
		final int lNumPlatform = lPlatformsList.size();
		for (int i = 0; i < lNumPlatform; i++) {
			final var lPlatform = lPlatformsList.get(i);

			drawPlatform(core, lPlatform);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawPlatform(LintfordCore core, PlatformInstance platform) {
		if (platform == null)
			return;

		final var lSpriteFrame = getPlatformSpriteFrame(platform);

		if (lSpriteFrame == null)
			return;

		lSpriteFrame.update(core);

		final var lWhiteWithAlpha = ColorConstants.getColor(1.f, 1.f, 1.f, 1.f);
		final var spriteBatch = core.sharedResources().uiSpriteBatch();

		spriteBatch.setColor(lWhiteWithAlpha);
		spriteBatch.begin(core.gameCamera());
		spriteBatch.draw(mPlatformsSpritesheet, lSpriteFrame, platform, .01f);

		if (platform.containerSprite != null) {
			platform.containerSprite.update(core);
			spriteBatch.draw(mPlatformsSpritesheet, platform.containerSprite, platform, .01f);
		}

		// texture light
		if (platform.isStockFull) {
			spriteBatch.setColor(ColorConstants.GREEN());
			spriteBatch.draw(mPlatformsSpritesheet, mPlatformsSpritesheet.getSpriteFrame("TEXTURELIGHTGLOW"), platform.x(), platform.y(), 8, 8, .01f);
		} else {
			if (platform.isRefillingStock) {
				spriteBatch.setColor(ColorConstants.YELLOW());
				spriteBatch.draw(mPlatformsSpritesheet, mPlatformsSpritesheet.getSpriteFrame("TEXTURELIGHTGLOW"), platform.x(), platform.y(), 8, 8, .01f);
			} else {
				spriteBatch.setColor(ColorConstants.RED());
				spriteBatch.draw(mPlatformsSpritesheet, mPlatformsSpritesheet.getSpriteFrame("TEXTURELIGHTGLOW"), platform.x(), platform.y(), 8, 8, .01f);
			}

		}

		spriteBatch.end();
	}

	private SpriteInstance getPlatformSpriteFrame(PlatformInstance platform) {
		final String FARM_FULL_NAME = "textureWheatFull";
		final String FARM_HALF_NAME = "TEXTUREWHEATHALF";
		final String FARM_EMPTY_NAME = "TEXTUREWHEATEMPTY";

		final String WATER_FULL_NAME = "TEXTUREWATERFULL";
		final String WATER_EMPTY_NAME = "TEXTUREWATEREMPTY";

		final String WAREHOUSE_NAME = "TEXTUREWAREHOUSE";

		switch (platform.platformType) {
		case Farm:
			if (platform.isStockFull) {
				if (platform.platformSprite != null) {
					if (platform.spriteName != null && platform.spriteName.equals(FARM_FULL_NAME))
						return platform.platformSprite;
				}

				platform.spriteName = FARM_FULL_NAME;
				platform.platformSprite = mPlatformsSpritesheet.getSpriteInstance(FARM_FULL_NAME);
				return platform.platformSprite;
			}

			if (platform.isRefillingStock) {
				if (platform.platformSprite != null) {
					if (platform.spriteName != null && platform.spriteName.equals(FARM_HALF_NAME))
						return platform.platformSprite;
				}

				platform.spriteName = FARM_HALF_NAME;
				platform.platformSprite = mPlatformsSpritesheet.getSpriteInstance(FARM_HALF_NAME);
				return platform.platformSprite;
			}

			if (platform.platformSprite != null) {
				if (platform.spriteName != null && platform.spriteName.equals(FARM_EMPTY_NAME))
					return platform.platformSprite;
			}

			platform.spriteName = FARM_EMPTY_NAME;
			platform.platformSprite = mPlatformsSpritesheet.getSpriteInstance(FARM_EMPTY_NAME);
			return platform.platformSprite;

		case Water:
			// Platform sprite is an animation
			if (platform.platformSprite == null) {
				platform.spriteName = "waterstation";
				platform.platformSprite = mPlatformsSpritesheet.getSpriteInstance("waterstation");
			}

			// TODO: Container sprite

			if (platform.containerSprite != null) {
				platform.platformSprite = mPlatformsSpritesheet.getSpriteInstance("watercontainer");
			}

			return platform.platformSprite;

		default: // warehouse
			if (platform.spriteName != null && platform.spriteName.equals(WAREHOUSE_NAME))
				return platform.platformSprite;

			platform.spriteName = WAREHOUSE_NAME;
			platform.platformSprite = mPlatformsSpritesheet.getSpriteInstance(WAREHOUSE_NAME);
			return platform.platformSprite;
		}

	}
}
