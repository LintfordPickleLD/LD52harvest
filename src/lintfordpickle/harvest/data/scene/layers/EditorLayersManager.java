package lintfordpickle.harvest.data.scene.layers;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.editor.BaseEditorInstanceManager;
import lintfordpickle.harvest.data.editor.EditorSceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneNoiseLayerSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneSpritesLayerSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneTextureLayerSaveDefinition;

public class EditorLayersManager extends BaseEditorInstanceManager {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final List<SceneBaseLayer> mLayers = new ArrayList<>();
	private int mInstanceUidCounter;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public List<SceneBaseLayer> layers() {
		return mLayers;
	}

	public int getNewInstanceUid() {
		return mInstanceUidCounter++;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorLayersManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void setLayerUidCounters() {

		// for synchronizing the layers in the LayerManager to the PanelUi we need to ensure each layer has a unique id.

		final var numLayers = mLayers.size();
		for (int i = 0; i < numLayers; i++) {
			mLayers.get(i).layerUid = i;

		}

		mInstanceUidCounter = numLayers + 1;
	}

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

		final int numLayers = mLayers.size();
		for (int i = 0; i < numLayers; i++) {
			final var sceneLayerToSerialize = mLayers.get(i);
			final var layerSaveDefinition = sceneLayerToSerialize.getSaveDefinition();

			if (layerSaveDefinition instanceof SceneTextureLayerSaveDefinition) {
				sceneSaveDefinition.layers().textureLayers.add((SceneTextureLayerSaveDefinition) layerSaveDefinition);

			} else if (layerSaveDefinition instanceof SceneSpritesLayerSaveDefinition) {
				sceneSaveDefinition.layers().spriteLayers.add((SceneSpritesLayerSaveDefinition) layerSaveDefinition);

			} else if (layerSaveDefinition instanceof SceneNoiseLayerSaveDefinition) {
				sceneSaveDefinition.layers().noiseLayers.add((SceneNoiseLayerSaveDefinition) layerSaveDefinition);

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
		final var lNumAnimationLayers = spriteLayers.size();
		for (int i = 0; i < lNumAnimationLayers; i++) {
			final var layerToImport = spriteLayers.get(i);
			final var sceneLayer = layerToImport.getSceneLayer();
			sceneLayer.visible = true;

			layers().add(sceneLayer);
		}

		final var lNoiseLayers = layerSaveManager.noiseLayers;
		final var numNoiseLayers = lNoiseLayers.size();
		for (int i = 0; i < numNoiseLayers; i++) {
			final var layerToImport = lNoiseLayers.get(i);
			final var sceneLayer = layerToImport.getSceneLayer();
			sceneLayer.visible = true;

			layers().add(sceneLayer);
		}

		setLayerUidCounters();
	}

	@Override
	public void finalizeAfterLoading(EditorSceneData sceneData) {
		final var spriteManager = sceneData.spriteManager();

		final var numLayers = mLayers.size();
		for (int i = 0; i < numLayers; i++) {
			final var layerToFinalize = mLayers.get(i);
			layerToFinalize.finalizeAfterLoading(spriteManager);
		}

	}

}
