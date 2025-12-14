package lintfordpickle.harvest.renderers.editor;

import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.controllers.editor.EditorSceneController;
import lintfordpickle.harvest.controllers.editor.ILayerSelectionListener;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import lintfordpickle.harvest.data.scene.layers.SceneTextureLayer;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class EditorLayersRenderer extends BaseRenderer implements ILayerSelectionListener {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Editor Layers Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private EditorSceneController mSceneController;

	private EditorLayerController mEditorLayerController;
	private EditorBrushController mEditorBrushController;

	private EditorTextureLayerRenderer mEditorTextureLayerRenderer;
	private EditorNoiseLayerRenderer mEditorNoiseLayerRenderer;
	private EditorSpriteLayerRenderer mEditorSpriteLayerRenderer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mSceneController != null;

	}

	public EditorTextureLayerRenderer textureLayerRenderer() {
		return mEditorTextureLayerRenderer;
	}

	public EditorNoiseLayerRenderer noiseLayerRenderer() {
		return mEditorNoiseLayerRenderer;
	}

	public EditorSpriteLayerRenderer spritesLayerRenderer() {
		return mEditorSpriteLayerRenderer;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorLayersRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mEditorTextureLayerRenderer = new EditorTextureLayerRenderer(entityGroupID);
		mEditorNoiseLayerRenderer = new EditorNoiseLayerRenderer(entityGroupID);
		mEditorSpriteLayerRenderer = new EditorSpriteLayerRenderer(entityGroupID);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();

		mSceneController = (EditorSceneController) lControllerManager.getControllerByNameRequired(EditorSceneController.CONTROLLER_NAME, entityGroupUid());
		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, entityGroupUid());
		mEditorLayerController = (EditorLayerController) lControllerManager.getControllerByNameRequired(EditorLayerController.CONTROLLER_NAME, entityGroupUid());
		mEditorLayerController.selectionListener(this);

		mEditorTextureLayerRenderer.initialize(core);
		mEditorNoiseLayerRenderer.initialize(core);
		mEditorSpriteLayerRenderer.initialize(core);
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mEditorTextureLayerRenderer.loadResources(resourceManager);
		mEditorNoiseLayerRenderer.loadResources(resourceManager);
		mEditorSpriteLayerRenderer.loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mEditorTextureLayerRenderer.unloadResources();
		mEditorNoiseLayerRenderer.unloadResources();
		mEditorSpriteLayerRenderer.unloadResources();
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (super.handleInput(core))
			return true;

		final var selectedLayer = mEditorLayerController.selectedLayer();

		// handle input on the selected layer only
		if (selectedLayer != null) {
			if (selectedLayer == null || !selectedLayer.visible)
				return false;

			switch (selectedLayer) {
			case SceneNoiseLayer layer: {
				if (mEditorNoiseLayerRenderer.handleInput(core, layer))
					return true;

				break;
			}

			case SceneTextureLayer layer: {
				if (mEditorTextureLayerRenderer.handleInput(core, layer))
					return true;

				break;
			}

			case SceneSpriteLayer layer: {
				if (mEditorSpriteLayerRenderer.handleInput(core, layer))
					return true;

				break;
			}

			default: {
				/* ignore */ }

			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var layers = mEditorLayerController.layersManager().layers();
		final var numLayers = layers.size();
		for (int i = 0; i < numLayers; i++) {
			final var sceneLayer = layers.get(i);

			if (sceneLayer == null)
				continue;

			switch (sceneLayer) {
			case SceneNoiseLayer s -> mEditorNoiseLayerRenderer.update(core, s);
			case SceneTextureLayer z -> mEditorTextureLayerRenderer.update(core, z);
			case SceneSpriteLayer t -> mEditorSpriteLayerRenderer.update(core, t);
			default -> {
				/* ignore */ }

			}
		}
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		final var layers = mEditorLayerController.layersManager().layers();
		final var numLayers = layers.size();
		for (int i = 0; i < numLayers; i++) {
			final var sceneLayer = layers.get(i);

			if (sceneLayer == null)
				continue;

			switch (sceneLayer) {
			case SceneNoiseLayer s -> mEditorNoiseLayerRenderer.drawNoiseLayer(core, s);
			case SceneTextureLayer z -> mEditorTextureLayerRenderer.drawTextureLayer(core, z);
			case SceneSpriteLayer t -> mEditorSpriteLayerRenderer.drawSpriteLayer(core, t);
			default -> {
				/* ignore */ }

			}
		}
	}

	// ---------------------------------------------
	// Inherited-Methods
	// ---------------------------------------------

	@Override
	public void OnLayerSelected(SceneBaseLayer layer) {
		mEditorTextureLayerRenderer.onLayerDeselected();
		mEditorNoiseLayerRenderer.onLayerDeselected();
		mEditorSpriteLayerRenderer.onLayerDeselected();
	}

	@Override
	public void OnLayerDeselected(SceneBaseLayer layer) {

	}
}
