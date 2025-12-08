package lintfordpickle.harvest.renderers.scene.layers;

import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.renderers.RendererManagerBase;

public class SceneLayerSpriteRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LayersManager mLayersManager;
	private ResourceManager mResourceManager;
	private RendererManagerBase mRendererManager;

	private int mEntityGroupUid;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneLayerSpriteRenderer(RendererManagerBase rendererManager, LayersManager layerManager, int entityGroupUid) {
		mRendererManager = rendererManager;
		mLayersManager = layerManager;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		mResourceManager = resourceManager;

		final var layers = mLayersManager.layers();
		final var numLayers = layers.size();

		for (int i = 0; i < numLayers; i++) {
			if (layers.get(i) instanceof SceneSpriteLayer spriteLayer) {

				final var layerAnimations = spriteLayer.spriteAssets();
				final var numAnimations = layerAnimations.size();
				for (int j = 0; j < numAnimations; j++) {
					final var spriteInstance = layerAnimations.get(j);

					if (!validateSpriteSheetDefinition(spriteInstance))
						continue;

					validateSpriteInstance(spriteInstance);

				}
			}
		}
	}

	public void unload() {
		mResourceManager = null;
	}

	public void draw(LintfordCore core, SceneSpriteLayer layer) {
		final var spriteBatch = mRendererManager.sharedResources().uiSpriteBatch();

		spriteBatch.setColorWhite();
		spriteBatch.begin(core.gameCamera());

		final var layerAnimations = layer.spriteAssets();
		final var numAnimations = layerAnimations.size();
		for (int i = 0; i < numAnimations; i++) {
			final var lSpriteInstance = layerAnimations.get(i);

			// basically, if we couldn't resolve the sprite during loading, then we can' do it now either
			if (lSpriteInstance.spriteInstStatus != SceneSpriteInstance.STATUS_LOADED)
				continue;

			spriteBatch.draw(lSpriteInstance.spriteSheetDefinition, lSpriteInstance.spriteInstance, .001f);
		}

		spriteBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private boolean validateSpriteSheetDefinition(SceneSpriteInstance assetInstance) {
		if (assetInstance.spriteSheetStatus == SceneSpriteInstance.STATUS_LOADED)
			return true;

		if (assetInstance.spriteSheetStatus == SceneSpriteInstance.STATUS_FAILED)
			return false;

		final var assetDefinition = assetInstance.definition;
		final var spriteAssetDefintion = assetDefinition.sceneSpriteContainer;

		assetInstance.spriteSheetDefinition = mResourceManager.spriteSheetManager().loadSpriteSheet(spriteAssetDefintion.spritesheetDefinitionName, spriteAssetDefintion.spritesheetDefinitionFilename, mEntityGroupUid);
		if (assetInstance.spriteSheetDefinition == null) {
			assetInstance.spriteSheetStatus = SceneSpriteInstance.STATUS_FAILED;
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to resolve SpriteAsset '" + assetDefinition.name + "' - SpriteSheetDefinition: " + spriteAssetDefintion.spritesheetDefinitionFilename);
			return false;
		}

		assetInstance.spriteSheetStatus = SceneSpriteInstance.STATUS_LOADED;
		return true;
	}

	private boolean validateSpriteInstance(SceneSpriteInstance assetInstance) {
		if (assetInstance.spriteInstStatus == SceneSpriteInstance.STATUS_LOADED)
			return true;

		if (assetInstance.spriteInstStatus == SceneSpriteInstance.STATUS_FAILED)
			return false;

		final var assetDefinition = assetInstance.definition;
		final var spriteAssetDefintion = assetDefinition.sceneSpriteContainer;
		final var assetSpriteSheetDefinition = assetInstance.spriteSheetDefinition;

		assetInstance.spriteInstance = assetSpriteSheetDefinition.getSpriteInstance(spriteAssetDefintion.spriteName);
		if (assetInstance.spriteInstance == null) {
			assetInstance.spriteInstStatus = SceneSpriteInstance.STATUS_FAILED;
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create sprite instance '" + assetDefinition.name + "' - SpriteSheetDefinition: " + spriteAssetDefintion.spriteName);
			return false;
		}

		assetInstance.spriteInstStatus = SceneSpriteInstance.STATUS_LOADED;
		return true;
	}
}
