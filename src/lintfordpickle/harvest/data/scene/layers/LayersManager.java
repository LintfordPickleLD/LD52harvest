package lintfordpickle.harvest.data.scene.layers;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneNoiseLayerSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneSpritesLayerSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneTextureLayerSaveDefinition;

public class LayersManager extends BaseInstanceManager {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final List<SceneBaseLayer> mLayers = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public List<SceneBaseLayer> layers() {
		return mLayers;
	}

	@Override
	public void initializeInstanceCounter() {
		// TODO Auto-generated method stub

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LayersManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void addLayer(SceneBaseLayer layerToAd) {
		if (mLayers.contains(layerToAd) == false) {
			mLayers.add(layerToAd);
		}
	}

	public void removedLayer(SceneBaseLayer layerToDelete) {
		if (mLayers.contains(layerToDelete)) {
			mLayers.remove(layerToDelete);
		}
	}

	// ---------------------------------------------

	@Override
	public void initializeManager() {

	}

	@Override
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {

		final int lNumLayers = mLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			final var lSceneLayerToSerialize = mLayers.get(i);
			final var lLayerSaveDefinition = lSceneLayerToSerialize.getSaveDefinition();

			if (lLayerSaveDefinition instanceof SceneTextureLayerSaveDefinition) {
				sceneSaveDefinition.layers().textureLayers.add((SceneTextureLayerSaveDefinition) lLayerSaveDefinition);

			} else if (lLayerSaveDefinition instanceof SceneSpritesLayerSaveDefinition) {
				sceneSaveDefinition.layers().spriteLayers.add((SceneSpritesLayerSaveDefinition) lLayerSaveDefinition);

			} else if (lLayerSaveDefinition instanceof SceneNoiseLayerSaveDefinition) {
				sceneSaveDefinition.layers().noiseLayers.add((SceneNoiseLayerSaveDefinition) lLayerSaveDefinition);

			}
		}
	}

	@Override
	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		final var layerSaveManager = sceneSaveDefinition.layers();

		final var textureLayers = layerSaveManager.textureLayers;
		final var numTextureLayers = textureLayers.size();
		for (int i = 0; i < numTextureLayers; i++) {
			final var layerToImport = textureLayers.get(i);
			final var sceneLayer = layerToImport.getSceneLayer();
			sceneLayer.visible = true;

			layers().add(sceneLayer);
		}

		final var spriteLayers = layerSaveManager.spriteLayers;
		final var numAnimationLayers = spriteLayers.size();
		for (int i = 0; i < numAnimationLayers; i++) {
			final var layerToImport = spriteLayers.get(i);
			final var sceneLayer = layerToImport.getSceneLayer();
			sceneLayer.visible = true;

			layers().add(sceneLayer);
		}

		final var noiseLayers = layerSaveManager.noiseLayers;
		final var numNoiseLayers = noiseLayers.size();
		for (int i = 0; i < numNoiseLayers; i++) {
			final var layerToImport = noiseLayers.get(i);
			final var sceneLayer = layerToImport.getSceneLayer();
			sceneLayer.visible = true;

			layers().add(sceneLayer);
		}
	}

	@Override
	public void finalizeAfterLoading(SceneData sceneData) {
		final var numLayers = mLayers.size();
		for (int i = 0; i < numLayers; i++) {
			final var layerToFinalize = mLayers.get(i);
			layerToFinalize.finalizeAfterLoading(sceneData.spriteManager());
		}

	}

}
