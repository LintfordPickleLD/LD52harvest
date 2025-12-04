package lintfordpickle.harvest.controllers.layers;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneParticleLayer;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;

public class EditorParticleLayerController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor Particle Layer Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<SceneParticleLayer> mParticleLayers = new ArrayList<>();
	private LayersManager mLayersManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<SceneParticleLayer> particleLayers() {
		return mParticleLayers;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorParticleLayerController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setLayerManager(LayersManager layersManager) {
		mLayersManager = layersManager;

		mParticleLayers.clear();

		final var lLayers = mLayersManager.layers();
		final int lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			if (lLayers.get(i) instanceof SceneParticleLayer) {
				mParticleLayers.add((SceneParticleLayer) lLayers.get(i));
			}
		}
	}

	public void addParticleLayer(SceneParticleLayer particleLayer) {
		if (mParticleLayers.contains(particleLayer) == false)
			mParticleLayers.add(particleLayer);
	}

	public void removeParticleLayer(SceneParticleLayer particleLayer) {
		if (mParticleLayers.contains(particleLayer))
			mParticleLayers.remove(particleLayer);
	}

}
