package lintfordpickle.harvest.renderers.editor;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.controllers.editor.EditorAssetsController;
import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.data.editor.EditorLayerBrush;

public class EditorSpriteLayerRenderer implements IInputProcessor {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Editor Sprite Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private EditorBrushController mEditorBrushController;
	private EditorAssetsController mEditorAssetsController;

	private ResourceManager mResourceManager;

	private float mMouseX;
	private float mMouseY;

	private boolean mMouseDownLastFrame;
	private boolean mIsNewLeftClick;
	private float mMouseDownX;
	private float mMouseDownY;

	private boolean mRenderSpriteLayer;
	private int mEntityGroupUid;

	protected float mInputTimer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean renderSpritesLayer() {
		return mRenderSpriteLayer;
	}

	public void renderSpritesLayer(boolean renderingEnabled) {
		mRenderSpriteLayer = renderingEnabled;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorSpriteLayerRenderer(int entityGroupID) {
		mEntityGroupUid = entityGroupID;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();

		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorAssetsController = (EditorAssetsController) lControllerManager.getControllerByNameRequired(EditorAssetsController.CONTROLLER_NAME, mEntityGroupUid);

	}

	public void loadResources(ResourceManager resourceManager) {
		mResourceManager = resourceManager;

	}

	public void unloadResources() {

	}

	public boolean handleInput(LintfordCore core, SceneSpriteLayer layer) {
		if (!mEditorBrushController.isLayerActive(EditorLayersData.Layer_Animation))
			return false;

		final var leftMouseDown = core.input().mouse().tryAcquireMouseLeftClick(hashCode());
		mMouseX = core.gameCamera().getMouseWorldSpaceX();
		mMouseY = core.gameCamera().getMouseWorldSpaceY();

		// --- ACTIONS
		if (mEditorBrushController.brush().isActionSet() == false) {
			final var isSpriteSelected = mEditorAssetsController.selectedAssetinstance() != null;

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_G, this)) {
				mIsNewLeftClick = false;

				if (isSpriteSelected) {
					mEditorBrushController.setAction(EditorLayerController.ACTION_OBJECT_TRANSLATE_SELECTED_LAYER, "Translate Layer", hashCode());

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;
				}
			} else if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_W, this)) {
				mIsNewLeftClick = false;

				if (isSpriteSelected) {
					mEditorBrushController.setAction(EditorLayerController.ACTION_OBJECT_SCALE_SELECTED_LAYER_X, "Scale Width", hashCode());

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;
				}
			} else if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_H, this)) {
				mIsNewLeftClick = false;

				if (isSpriteSelected) {
					mEditorBrushController.setAction(EditorLayerController.ACTION_OBJECT_SCALE_SELECTED_LAYER_Y, "Scale Height", hashCode());

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;
				}
			} else if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DELETE, this)) {

				if (isSpriteSelected) {
					layer.removeAssetInstance(mEditorAssetsController.selectedAssetinstance());
					mEditorAssetsController.selectedAssetinstance(null);
				}
			}
		}

		final int lCurrentBrushAction = mEditorBrushController.brush().brushActionUid();
		// --- END ACTIONS

		// ----

		if (leftMouseDown && !mMouseDownLastFrame) {
			mIsNewLeftClick = true;
			mMouseDownLastFrame = true;
		}

		if (lCurrentBrushAction != EditorLayerBrush.NO_ACTION_UID) {

			final var selectedSpriteInstance = mEditorAssetsController.selectedAssetinstance();

			// do something with mouse
			switch (lCurrentBrushAction) {
			case EditorLayerController.ACTION_OBJECT_TRANSLATE_SELECTED_LAYER: {
				final var lTranslationAmtX = mMouseX - mMouseDownX;
				final var lTranslationAmtY = mMouseY - mMouseDownY;

				mMouseDownX = mMouseX;
				mMouseDownY = mMouseY;

				final var curX = selectedSpriteInstance.destRect.x();
				final var curY = selectedSpriteInstance.destRect.y();

				selectedSpriteInstance.destRect.x(curX + lTranslationAmtX);
				selectedSpriteInstance.destRect.y(curY + lTranslationAmtY);
			}

				break;

			case EditorLayerController.ACTION_OBJECT_SCALE_SELECTED_LAYER_X: {
				final var lTranslationAmtX = mMouseX - mMouseDownX;

				mMouseDownX = mMouseX;
				mMouseDownY = mMouseY;

				final var curW = selectedSpriteInstance.destRect.width();
				selectedSpriteInstance.destRect.width(curW + lTranslationAmtX);
				break;
			}

			case EditorLayerController.ACTION_OBJECT_SCALE_SELECTED_LAYER_Y: {
				final var lTranslationAmtY = mMouseY - mMouseDownY;

				mMouseDownX = mMouseX;
				mMouseDownY = mMouseY;

				final var curH = selectedSpriteInstance.destRect.height();
				selectedSpriteInstance.destRect.height(curH + lTranslationAmtY);
				break;
			}

			}

			if (mIsNewLeftClick) {
				mEditorBrushController.clearAction(hashCode());
			}

		} else if (leftMouseDown) {

			mEditorAssetsController.selectedAssetinstance(null);

			// selection needs to happen in the translated layer space (relative to the camera)

			final var aabb_c = core.gameCamera().boundingRectangle();
			final var cameraPositionX = aabb_c.centerX();
			final var cameraPositionY = aabb_c.centerY();

			final var camOffsetX = -layer.centerX + cameraPositionX * layer.translationSpeedModX;
			final var camOffsetY = -layer.centerY - cameraPositionY * layer.translationSpeedModY;

			final var lLayerAnimations = layer.spriteAssets();
			final var lNumAnimations = lLayerAnimations.size();
			for (int j = 0; j < lNumAnimations; j++) {
				final var assetInstance = lLayerAnimations.get(j);

				if (assetInstance.destRect.intersectsAA(mMouseX - camOffsetX, mMouseY - camOffsetY)) {
					mEditorAssetsController.selectedAssetinstance(assetInstance);
					break;
				}
			}
		}

		// ----

		if (!leftMouseDown && !mIsNewLeftClick) {
			mMouseDownLastFrame = false;
			mIsNewLeftClick = false;
		}

		return false;
	}

	public void update(LintfordCore core, SceneSpriteLayer layer) {
		if (mInputTimer > 0)
			mInputTimer -= core.gameTime().elapsedTimeMilli();

		if (!layer.visible)
			return;

		final var layerSprites = layer.spriteAssets();
		final var numSprites = layerSprites.size();
		for (int j = 0; j < numSprites; j++) {
			final var spriteAsset = layerSprites.get(j);

			if (spriteAsset.spriteInstance == null)
				continue;

			spriteAsset.update(core);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void drawSpriteLayer(LintfordCore core, SceneSpriteLayer layer) {
		if (!mRenderSpriteLayer)
			return;

		if (!layer.visible)
			return;

		final var aabb_c = core.gameCamera().boundingRectangle();
		final var cameraPositionX = aabb_c.centerX();
		final var cameraPositionY = aabb_c.centerY();

		final var camOffsetX = -layer.centerX + cameraPositionX * layer.translationSpeedModX;
		final var camOffsetY = -layer.centerY - cameraPositionY * layer.translationSpeedModY;

		final var spriteBatch = core.sharedResources().uiSpriteBatch();

		spriteBatch.begin(core.gameCamera());

		final var zDepth = 9.f - layer.zInvDepth;

		final var selectedSpriteInstance = mEditorAssetsController.selectedAssetinstance();

		final var layerAnimations = layer.spriteAssets();
		final var numAnimations = layerAnimations.size();
		for (int i = 0; i < numAnimations; i++) {
			final var assetInstance = layerAnimations.get(i);

			final var destX = assetInstance.destRect.x();
			final var destY = assetInstance.destRect.y();
			final var destW = assetInstance.destRect.width();
			final var destH = assetInstance.destRect.height();

			if (layer.visible) {
				// Draw soemthing that can be used independently of the spriteInstance (which may fil to load).
				// TODO: Don't use the fucking debug drawers for this, they're not always available.
				if (selectedSpriteInstance != null && selectedSpriteInstance == assetInstance) {
					Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), camOffsetX + destX, camOffsetY + destY, destW, destH, 1, 0, 0);
				} else {
					Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), camOffsetX + destX, camOffsetY + destY, destW, destH);
				}
			}

			if (!validateSpriteSheetDefinition(assetInstance))
				continue;

			if (!validateSpriteInstance(assetInstance))
				continue;

			spriteBatch.draw(assetInstance.spriteSheetDefinition, assetInstance.spriteInstance, camOffsetX + destX, camOffsetY + destY, destW, destH, zDepth);

		}

		spriteBatch.end();
	}

	private boolean validateSpriteSheetDefinition(SceneSpriteInstance assetInstance) {
		if (assetInstance.spriteSheetStatus == SceneSpriteInstance.STATUS_LOADED)
			return true;

		if (assetInstance.spriteSheetStatus == SceneSpriteInstance.STATUS_FAILED)
			return false;

		final var assetDefinition = assetInstance.definition;
		if (assetDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to resolve Asset definition '" + assetInstance.definitionName + "' - looks like it wasn't loaded correctly.");
			return false;
		}
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

		// make sure the default width/height have set values (and if not, set them based on sprite frame)
		if (assetDefinition.propDefaultWidth == 0) {
			assetDefinition.propDefaultWidth = (int) assetInstance.spriteInstance.width();
		}

		if (assetDefinition.propDefaultHeight == 0) {
			assetDefinition.propDefaultHeight = (int) assetInstance.spriteInstance.height();
		}

		assetInstance.spriteInstStatus = SceneSpriteInstance.STATUS_LOADED;
		return true;
	}

	public void drawSelectedLayerDebug(LintfordCore core, SceneBaseLayer layer) {
		final var x = layer.centerX - layer.width * .5f;
		final var y = layer.centerY - layer.height * .5f;

		Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), x, y, layer.width, layer.height, 1f, 1f, 0f);
	}

	public void onLayerDeselected() {
		mEditorAssetsController.selectedAssetinstance(null);
	}

	// ---------------------------------------------
	// Inherited-Methods
	// ---------------------------------------------

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mInputTimer = 300;

	}

	@Override
	public boolean allowGamepadInput() {
		return false;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}

}
