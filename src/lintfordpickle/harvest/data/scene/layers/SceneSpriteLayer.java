package lintfordpickle.harvest.data.scene.layers;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
import lintfordpickle.harvest.data.assets.SceneSpriteManager;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.BaseSceneLayerSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneSpriteInstanceSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneSpritesLayerSaveDefinition;

public class SceneSpriteLayer extends SceneBaseLayer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient List<SceneSpriteInstance> mSprites = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<SceneSpriteInstance> spriteAssets() {
		return mSprites;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneSpriteLayer(int uid) {
		super(uid);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addAssetToLayer(SceneSpriteInstance assetInstance) {
		if (assetInstance == null)
			return;

		if (!mSprites.contains(assetInstance))
			mSprites.add(assetInstance);

	}

	public void removeAssetInstance(SceneSpriteInstance assetInstance) {
		if (mSprites.contains(assetInstance))
			mSprites.remove(assetInstance);

	}

	@Override
	public BaseSceneLayerSaveDefinition getSaveDefinition() {
		final var saveDefinition = new SceneSpritesLayerSaveDefinition();

		fillBaseSceneLayerInfo(saveDefinition);

		final int numSprites = mSprites.size();
		for (int i = 0; i < numSprites; i++) {
			final var spriteToSave = mSprites.get(i);
			final var spriteSaveDef = new SceneSpriteInstanceSaveDefinition();

			spriteSaveDef.entityUid = spriteToSave.uid;
			spriteSaveDef.destinationRectangle = spriteToSave.destRect;
			spriteSaveDef.assetDefinitionName = spriteToSave.definitionName;

			saveDefinition.spriteInstances.add(spriteSaveDef);
		}

		return saveDefinition;
	}

	@Override
	public void finalizeAfterLoading(SceneSpriteManager spriteManager) {
		super.finalizeAfterLoading(spriteManager);

		final var numSprites = mSprites.size();
		for (int i = 0; i < numSprites; i++) {
			final var sprite = mSprites.get(i);

			final var spriteDefinition = spriteManager.definitionManager().getByName(sprite.definitionName);
			sprite.definition = spriteDefinition;
		}

	}

}
