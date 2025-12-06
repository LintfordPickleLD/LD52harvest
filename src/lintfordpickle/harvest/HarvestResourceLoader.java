package lintfordpickle.harvest;

import net.lintfordlib.assets.ResourceLoader;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.options.DisplayManager;

public class HarvestResourceLoader extends ResourceLoader {

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public HarvestResourceLoader(ResourceManager resourceManager, DisplayManager displayManager, int entityGroupUid) {
		super(resourceManager, displayManager, true, entityGroupUid);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected void resourcesToLoadInBackground(int entityGroupUid) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading game assets into group: " + ConstantsGame.GAME_RESOURCE_GROUP_ID);
		mResourceManager.addProtectedEntityGroupUid(ConstantsGame.GAME_RESOURCE_GROUP_ID);

		currentStatusMessage("loading resources");

		mResourceManager.textureManager().loadTexturesFromMetafile("res/textures/_meta.json", ConstantsGame.GAME_RESOURCE_GROUP_ID);

		// TODO: This needs to be updated, the only way currently to 'reload' the core spritesheet is to manually reload the texture and then the new spritesheet
//		mResourceManager.textureManager().loadTexture("TEXTURE_CORE", "res/textures/textureCore.png", GL11.GL_NEAREST, true, LintfordCore.CORE_ENTITY_GROUP_ID);
//		mResourceManager.spriteSheetManager().loadSpriteSheet("res/spritesheets/spritesheetCore.json", LintfordCore.CORE_ENTITY_GROUP_ID);
		mResourceManager.spriteSheetManager().loadSpriteSheetFromMeta("res/spritesheets/_meta.json", ConstantsGame.GAME_RESOURCE_GROUP_ID);

//		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_12", "res/fonts/fontNulshock12.json");
//		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_16", "res/fonts/fontNulshock16.json");
//		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_22", "res/fonts/fontNulshock22.json");
	}
}
