package lintfordpickle.harvest.data.scene.layers;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.assets.SceneAssetInstance;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.BaseSceneLayerSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneSpritesLayerSaveDefinition;

public class SceneSpriteLayer extends SceneBaseLayer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<Integer> mLayerAssetUids = new ArrayList<>();
	private transient List<SceneAssetInstance> mSprites = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<SceneAssetInstance> spriteAssets() {
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

	public void addAssetToLayer(SceneAssetInstance assetInstance) {
		if (assetInstance == null)
			return;

		final var lAssetUid = assetInstance.uid;
		if (mLayerAssetUids.contains(lAssetUid))
			return;

		mLayerAssetUids.add(lAssetUid);
		mSprites.add(assetInstance);

	}

	public void removeAssetInstance(SceneAssetInstance assetInstance) {
		final var lAssetUid = assetInstance.uid;

		mLayerAssetUids.remove(lAssetUid);
		mSprites.remove(assetInstance);
	}

	@Override
	public BaseSceneLayerSaveDefinition getSaveDefinition() {
		final var lSaveDefinition = new SceneSpritesLayerSaveDefinition();

		fillBaseSceneLayerInfo(lSaveDefinition);

		// TODO Auto-generated method stub

		return lSaveDefinition;
	}

}
