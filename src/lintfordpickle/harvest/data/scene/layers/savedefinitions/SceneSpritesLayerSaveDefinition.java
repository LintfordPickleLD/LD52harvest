package lintfordpickle.harvest.data.scene.layers.savedefinitions;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;

public class SceneSpritesLayerSaveDefinition extends BaseSceneLayerSaveDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7569238335511621943L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final List<SceneSpriteInstanceSaveDefinition> spriteInstances = new ArrayList<>();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneSpritesLayerSaveDefinition() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public SceneBaseLayer getSceneLayer() {
		final var spriteLayer = new SceneSpriteLayer(layerUid);
		spriteLayer.zDepth = layerZDepth;
		spriteLayer.name = layerName;

		spriteLayer.translationSpeedModX = translationSpeedModX;
		spriteLayer.translationSpeedModY = translationSpeedModY;

		spriteLayer.centerX = centerX;
		spriteLayer.centerY = centerY;

		spriteLayer.width = width;
		spriteLayer.height = height;

		final var numSprites = spriteInstances.size();
		for (int i = 0; i < numSprites; i++) {
			final var spriteToLoad = spriteInstances.get(i);
			final var spriteInstance = new SceneSpriteInstance(spriteToLoad.entityUid);

			// TODO: Restore the sprite to its original glory
			spriteInstance.definitionName = spriteToLoad.assetDefinitionName;
			spriteInstance.destRect.set(spriteToLoad.destinationRectangle);

			spriteLayer.addAssetToLayer(spriteInstance);
		}

		return spriteLayer;
	}

}
