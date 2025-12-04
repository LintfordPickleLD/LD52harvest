package lintfordpickle.harvest.controllers.layers;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneAnimationLayer;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;

public class EditorAnimationLayerController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor Animation Layer Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<SceneAnimationLayer> mAnimationLayers = new ArrayList<>();
	private LayersManager mLayersManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<SceneAnimationLayer> animationLayers() {
		return mAnimationLayers;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorAnimationLayerController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		return super.handleInput(core);

	}

	@Override
	public void update(LintfordCore core) {
		// TODO Auto-generated method stub
		super.update(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setLayerManager(LayersManager layersManager) {
		mLayersManager = layersManager;

		mAnimationLayers.clear();

		final var lLayers = mLayersManager.layers();
		final int lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			if (lLayers.get(i) instanceof SceneAnimationLayer) {
				mAnimationLayers.add((SceneAnimationLayer) lLayers.get(i));
			}
		}
	}

	public void addAnimationLayer(SceneAnimationLayer animationLayer) {
		if (mAnimationLayers.contains(animationLayer) == false)
			mAnimationLayers.add(animationLayer);
	}

	public void removeAnimationLayer(SceneAnimationLayer animationLayer) {
		if (mAnimationLayers.contains(animationLayer))
			mAnimationLayers.remove(animationLayer);
	}

}
