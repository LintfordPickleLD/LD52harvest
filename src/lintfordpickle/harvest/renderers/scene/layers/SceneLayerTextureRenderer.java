package lintfordpickle.harvest.renderers.scene.layers;

import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneTextureLayer;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.renderers.RendererManagerBase;

public class SceneLayerTextureRenderer {

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

	public SceneLayerTextureRenderer(RendererManagerBase rendererManager, LayersManager layerManager, int entityGroupUid) {
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
			final var sceneLayer = layers.get(i);

			if (sceneLayer instanceof SceneTextureLayer textureLayer) {

				if (textureLayer.textureStatus == SceneTextureLayer.TEXTURE_UNLOADED) {
					textureLayer.texture = resourceManager.textureManager().loadTexture(textureLayer.textureName(), textureLayer.textureFilepath(), mEntityGroupUid);
					if (resourceManager.textureManager().textureNotFound().equals(textureLayer.texture)) {
						textureLayer.texture = null;

						return;
					}

					if (textureLayer.texture == null) {
						textureLayer.textureStatus = SceneTextureLayer.TEXTURE_FAILED;
					}
				}

			}
		}
	}

	public void unload() {
		mResourceManager = null;
	}

	public void draw(LintfordCore core, SceneTextureLayer layer) {
		final var spriteBatch = mRendererManager.sharedResources().uiSpriteBatch();

		final var aabb_c = core.gameCamera().boundingRectangle();
		final var cameraPositionX = aabb_c.centerX();
		final var cameraPositionY = aabb_c.centerY();

		if (layer.texture != null) {
			final var camOffsetX = -layer.centerX - cameraPositionX * layer.translationSpeedModX;
			final var camOffsetY = -layer.centerY - cameraPositionY * layer.translationSpeedModY;

			final var srcX = camOffsetX;
			final var srcY = camOffsetY;

			final var srcTexW = layer.texture.getTextureWidth();
			final var srcTexH = layer.texture.getTextureHeight();

			final var srcRatioX = layer.width / srcTexW * srcTexW;
			final var srcRatioY = layer.height / srcTexH * srcTexH;

			final var scaleX = layer.contentScaleX <= 0.f ? 1.f : layer.contentScaleX;
			final var scaleY = layer.contentScaleY <= 0.f ? 1.f : layer.contentScaleY;

			final var srcW = srcRatioX / scaleX;
			final var srcH = srcRatioY / scaleY;

			final var lDstX = layer.centerX - layer.width * .5f;
			final var lDstY = layer.centerY - layer.height * .5f;
			final var lDstWidth = layer.width;
			final var lDstHeight = layer.height;

			spriteBatch.setColorWhite();
			spriteBatch.begin(core.gameCamera());
			spriteBatch.draw(layer.texture, srcX, srcY, srcW, srcH, lDstX, lDstY, lDstWidth, lDstHeight, 9 - layer.zDepth);
			spriteBatch.end();
			return;
		}
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
