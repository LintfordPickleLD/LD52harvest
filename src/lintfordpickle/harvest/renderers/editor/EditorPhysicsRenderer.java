package lintfordpickle.harvest.renderers.editor;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import lintfordpickle.harvest.controllers.editor.EditorPhysicsController;
import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.editor.physics.EditorPhysicsObjectInstance;
import lintfordpickle.harvest.data.scene.collisions.GridEntityType;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.assets.ResourceGroupProvider;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.controllers.editor.EditorHashGridController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.linebatch.LineBatch;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.data.editor.EditorLayerBrush;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class EditorPhysicsRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "Editor Physics Renderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EditorPhysicsController mPhysicsController;
	private EditorBrushController mEditorBrushController;
	private EditorHashGridController mHashGridController;

	private boolean mSnapToPoints;
	private boolean mDrawPhysicsObjects;
	private boolean mDrawPhysicsObjectUids;

	private float mMouseX;
	private float mMouseY;

	private float mMouseDownV;
	private float mMouseDownX;
	private float mMouseDownY;

	private boolean mIsInCreateMode;

	private boolean mMouseDownLastFrame;
	private boolean mIsNewLeftClick;
	private boolean mNextClickForA;
	private boolean mNextClickForB;
	private boolean mNextClickForC;
	private boolean mNextClickForD;

	private EditorPhysicsObjectInstance mCreationPhysicsObject;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean renderPhysicsObjects() {
		return mDrawPhysicsObjects;
	}

	public void renderPhysicsObjects(boolean renderPhysicsObjects) {
		mDrawPhysicsObjects = renderPhysicsObjects;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorPhysicsRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		var lControllerManager = core.controllerManager();

		mPhysicsController = (EditorPhysicsController) lControllerManager.getControllerByNameRequired(EditorPhysicsController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);
		mHashGridController = (EditorHashGridController) lControllerManager.getControllerByNameRequired(EditorHashGridController.CONTROLLER_NAME, mEntityGroupUid);

	}

	@Override
	public boolean handleInput(LintfordCore core) {
		var lInputHandled = super.handleInput(core);

		if (mEditorBrushController.isLayerActive(EditorLayersData.Physics) == false)
			return lInputHandled;

		handleRightMouseDown(core);

		final var leftMouseDown = core.input().mouse().tryAcquireMouseLeftClick(hashCode());
		mMouseX = core.gameCamera().getMouseWorldSpaceX();
		mMouseY = core.gameCamera().getMouseWorldSpaceY();

		// --- ACTIONS
		if (mEditorBrushController.brush().isActionSet() == false) {
			final var lPointsSelected = mPhysicsController.selectedPoints().size() > 0;
			final var lRegionSelected = mPhysicsController.selectedPhysicsObject() != null;

			// check for new actions
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_G, this)) {
				mIsNewLeftClick = false;
				if (lPointsSelected) {
					mEditorBrushController.setAction(EditorPhysicsController.ACTION_OBJECT_TRANSLATE_SELECTED_POINTS, "Translate Points", hashCode());

					final int lNumSelectedPoints = mPhysicsController.selectedPoints().size();
					for (int i = 0; i < lNumSelectedPoints; i++) {
						final var lSelectedPoint = mPhysicsController.selectedPoints().get(i);
						if (lSelectedPoint == null)
							continue;
					}

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;

				} else if (lRegionSelected) {
					mEditorBrushController.setAction(EditorPhysicsController.ACTION_OBJECT_TRANSLATE_SELECTED_REGION, "Translate Region", hashCode());
					mPhysicsController.addDirtyRegion(mPhysicsController.selectedPhysicsObject());

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;
				}
			} else if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_R, this)) {
				mIsNewLeftClick = false;
				if (lRegionSelected) {
					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;

					mMouseDownV = mPhysicsController.selectedPhysicsObject().angle;

					mEditorBrushController.setAction(EditorPhysicsController.ACTION_OBJECT_ROTATE_REGION_SELECTED, "Rotate Region", hashCode());
					mPhysicsController.addDirtyRegion(mPhysicsController.selectedPhysicsObject());
				}
			}
		}

		final int lCurrentBrushAction = mEditorBrushController.brush().brushActionUid();
		// --- END ACTIONS

		mPhysicsController.updatePhysicsObjectInstanceNearby(mMouseX, mMouseY);
		final int lNumFoundEntities = mPhysicsController.phyiscsObjectEntitiesNearby().size();

		if (leftMouseDown && !mMouseDownLastFrame) {
			mIsNewLeftClick = true;
			mMouseDownLastFrame = true;
		}

		if (!mIsInCreateMode) {
			if (lCurrentBrushAction != EditorLayerBrush.NO_ACTION_UID) {
				final var lSelectedRegion = mPhysicsController.selectedPhysicsObject();

				// do something with mouse
				switch (lCurrentBrushAction) {
				case EditorPhysicsController.ACTION_OBJECT_TRANSLATE_SELECTED_POINTS:
					final int lNumSelectedPoints = mPhysicsController.selectedPoints().size();
					for (int i = 0; i < lNumSelectedPoints; i++) {
						final var lSelectedPoint = mPhysicsController.selectedPoints().get(i);
						if (lSelectedPoint == null)
							continue;

						if (lSelectedPoint.worldPosition.x != mMouseX || lSelectedPoint.worldPosition.y != mMouseY) {
							final var lTranslationAmtX = mMouseX - mMouseDownX;
							final var lTranslationAmtY = mMouseY - mMouseDownY;

							mMouseDownX = mMouseX;
							mMouseDownY = mMouseY;

							lSelectedPoint.worldPosition.add(lTranslationAmtX, lTranslationAmtY);

							lSelectedPoint.parent.recalculateBounds();
							mHashGridController.hashGrid().updateEntity(lSelectedPoint.parent);

						}
					}
					break;
				case EditorPhysicsController.ACTION_OBJECT_TRANSLATE_SELECTED_REGION:
					final var lTranslationAmtX = mMouseX - mMouseDownX;
					final var lTranslationAmtY = mMouseY - mMouseDownY;

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;

					lSelectedRegion.a.worldPosition.add(lTranslationAmtX, lTranslationAmtY);
					lSelectedRegion.b.worldPosition.add(lTranslationAmtX, lTranslationAmtY);
					lSelectedRegion.c.worldPosition.add(lTranslationAmtX, lTranslationAmtY);
					lSelectedRegion.d.worldPosition.add(lTranslationAmtX, lTranslationAmtY);

					lSelectedRegion.wcx += lTranslationAmtX;
					lSelectedRegion.wcy += lTranslationAmtY;

					lSelectedRegion.recalculateBounds();

					mHashGridController.hashGrid().updateEntity(lSelectedRegion);

					break;

				case EditorPhysicsController.ACTION_OBJECT_ROTATE_REGION_SELECTED:
					final var lRotAmtInRads = mMouseDownV - (mMouseDownX - mMouseX) * 0.01f;
					mMouseDownX = mMouseX;

					final var rotationCenterX = mEditorBrushController.cursorWorldX();
					final var rotationCenterY = mEditorBrushController.cursorWorldY();

					lSelectedRegion.a.worldPosition.rotateRad(rotationCenterX, rotationCenterY, lRotAmtInRads);
					lSelectedRegion.b.worldPosition.rotateRad(rotationCenterX, rotationCenterY, lRotAmtInRads);
					lSelectedRegion.c.worldPosition.rotateRad(rotationCenterX, rotationCenterY, lRotAmtInRads);
					lSelectedRegion.d.worldPosition.rotateRad(rotationCenterX, rotationCenterY, lRotAmtInRads);

					lSelectedRegion.recalculateBounds();

					mHashGridController.hashGrid().updateEntity(lSelectedRegion);

					break;
				}

				if (mIsNewLeftClick) {
					mEditorBrushController.clearAction(hashCode());

					mPhysicsController.cleanDirtyRegions(core);
				}

			} else if (leftMouseDown) {
				mPhysicsController.selectedRegion(null);
				mPhysicsController.clearSelectedPoints();

				checkSelectedPoints(core);

				final var lSelectedPoints = mPhysicsController.selectedPoints();
				if (lSelectedPoints.size() == 0) {
					for (int i = 0; i < lNumFoundEntities; i++) {
						final var lGridEntity = mPhysicsController.phyiscsObjectEntitiesNearby().get(i);
						if (!(lGridEntity instanceof EditorPhysicsObjectInstance))
							continue;

						final var lRegion = (EditorPhysicsObjectInstance) lGridEntity;

						if (lRegion.intersectAndGetHeight(mMouseX, mMouseY)) {
							mPhysicsController.selectedRegion(lRegion);
							break;
						}
					}
				}
			}
		} else {
			handleCreationInput(core);
		}

		if (!leftMouseDown && !mIsNewLeftClick) {
			mMouseDownLastFrame = false;
			mIsNewLeftClick = false;
		}

		return lInputHandled;

	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		if (!renderPhysicsObjects())
			return;

		final var lineBatch = core.sharedResources().uiLineBatch();
		final var fontUnit = core.sharedResources().uiTextFont();

		lineBatch.lineType(GL11.GL_LINES);

		lineBatch.begin(core.gameCamera());
		fontUnit.begin(core.gameCamera());

		drawPhysicsObjects(core, lineBatch, fontUnit);

		if (mCreationPhysicsObject != null) {
			System.out.println("dicks");
			drawRectangleCreationGuide(core, mCreationPhysicsObject, lineBatch, fontUnit);
		}

		drawSelectedPoints(core, lineBatch, fontUnit);

		lineBatch.end();
		fontUnit.end();

	}

	// --------------------------------------

	private void drawPhysicsObjects(LintfordCore core, LineBatch lineBatch, FontUnit font) {
		final var lCamWorldX = core.gameCamera().getPosition().x;
		final var lCamWorldY = core.gameCamera().getPosition().y;
		final var lCamRadius = core.gameCamera().getWidth() * .5f;
		final var lFilteredFloors = mHashGridController.hashGrid().findNearbyEntities(lCamWorldX, lCamWorldY, lCamRadius, GridEntityType.GRID_ENTITY_TYPE_PHYSICS_OBJECTS);

		final var lRectFloorCount = lFilteredFloors.size();
		for (int i = 0; i < lRectFloorCount; i++) {
			if (lFilteredFloors.get(i) instanceof EditorPhysicsObjectInstance) {
				final var lFloorRegion = (EditorPhysicsObjectInstance) lFilteredFloors.get(i);

				if (lFloorRegion == mCreationPhysicsObject)
					continue;

				boolean lIsPointSelected = false;
				final int lNumSelectedPoints = mPhysicsController.selectedPoints().size();
				for (int j = 0; j < lNumSelectedPoints; j++) {
					if (mPhysicsController.selectedPoints().get(j).parent == lFloorRegion)
						lIsPointSelected = true;
				}

				final var lIsSelected = lFloorRegion == mPhysicsController.selectedPhysicsObject();
				final var lIsInRegion = mPhysicsController.phyiscsObjectEntitiesNearby().contains(lFloorRegion);

				final var lR = lIsSelected ? 0.2f : lIsInRegion ? .9f : .2f;
				final var lG = lIsSelected ? 0.95f : .2f;
				final var lB = .2f;

				final var a = lFloorRegion.a.worldPosition;
				final var b = lFloorRegion.b.worldPosition;
				final var c = lFloorRegion.c.worldPosition;
				final var d = lFloorRegion.d.worldPosition;

				lineBatch.draw(a.x, a.y, b.x, b.y, .01f, lR, lG, lB);
				lineBatch.draw(b.x, b.y, c.x, c.y, .01f, lR, lG, lB * 3.f);
				lineBatch.draw(a.x, a.y, d.x, d.y, .01f, lR, lG, lB * 3.f);
				lineBatch.draw(c.x, c.y, d.x, d.y, .01f, lR, lG, lB);

				if (mDrawPhysicsObjectUids)
					font.drawText("" + lFloorRegion.uid, lFloorRegion.wcx, lFloorRegion.wcy, -0.01f, 0.5f);

				if (lIsSelected || lIsPointSelected) {
					font.drawText("A", a.x, a.y, .01f, 0.5f);
					font.drawText("B", b.x, b.y, .01f, 0.5f);
					font.drawText("C", c.x, c.y, .01f, 0.5f);
					font.drawText("D", d.x, d.y, .01f, 0.5f);

					final var lFloorWorldCenterX = lFloorRegion.wcx;
					final var lFloorWorldCenterY = lFloorRegion.wcy;

					final var lFloorBaryCenterX = lFloorRegion.bcx;
					final var lFloorBaryCenterY = lFloorRegion.bcy;

					Debug.debugManager().drawers().drawCircleImmediate(core.gameCamera(), lFloorWorldCenterX, lFloorWorldCenterY, 1.f);
					Debug.debugManager().drawers().drawCircleImmediate(core.gameCamera(), lFloorBaryCenterX, lFloorBaryCenterY, 2.f);

					Debug.debugManager().drawers().drawCircleImmediate(core.gameCamera(), lFloorBaryCenterX, lFloorBaryCenterY, lFloorRegion.radius);

					final var lAabbX = lFloorRegion.aabb.x();
					final var lAabbY = lFloorRegion.aabb.y();
					final var lAabbW = lFloorRegion.aabb.width();
					final var lAabbH = lFloorRegion.aabb.height();

					Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), lAabbX, lAabbY, lAabbW, lAabbH, 2.f, .65f, .45f, 0.f);
				}
			}
		}
	}

	private void drawRectangleCreationGuide(LintfordCore core, EditorPhysicsObjectInstance phsicsObject, LineBatch lLineBatch, FontUnit font) {
		if (mIsInCreateMode) {
			GL11.glPointSize(4);

			final var a = phsicsObject.a.worldPosition;
			final var b = phsicsObject.b.worldPosition;
			final var c = phsicsObject.c.worldPosition;

			if (mNextClickForA) {
				Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), mMouseX, mMouseY);
				font.drawText("A", mMouseX, mMouseY - font.fontHeight() * .5f, .01f, 0.5f);
			} else if (mNextClickForB) {
				Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), mMouseX, mMouseY);
				font.drawText("B", mMouseX, mMouseY - font.fontHeight() * .5f, .01f, 0.5f);
				lLineBatch.draw(a.x, a.y, mMouseX, mMouseY, .01f, 1.f, 1.f, 1.f);
			} else if (mNextClickForC) {
				Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), mMouseX, mMouseY);
				font.drawText("C", mMouseX, mMouseY - font.fontHeight() * .5f, .01f, 0.5f);
				lLineBatch.draw(a.x, a.y, b.x, b.y, .01f, 1.f, 1.f, 1.f);
				lLineBatch.draw(b.x, b.y, mMouseX, mMouseY, .01f, 1.f, 1.f, 1.f);
			} else {
				Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), mMouseX, mMouseY);
				font.drawText("D", mMouseX, mMouseY - font.fontHeight() * .5f, .01f, 0.5f);
				lLineBatch.draw(a.x, a.y, b.x, b.y, .01f, 1.f, 1.f, 1.f);
				lLineBatch.draw(b.x, b.y, c.x, c.y, .01f, 1.f, 1.f, 1.f);
				lLineBatch.draw(c.x, c.y, mMouseX, mMouseY, .01f, 1.f, 1.f, 1.f);
			}
		}
	}

	private void drawSelectedPoints(LintfordCore core, LineBatch lineBatch, FontUnit font) {

		// TODO: These needs to be changed - Debug points are only drawn when debug mode is on (which is not in the game)

		final int lNumSelectedPoints = mPhysicsController.selectedPoints().size();
		if (lNumSelectedPoints == 0)
			return;

		for (int i = 0; i < lNumSelectedPoints; i++) {
			final var lSelectedPoint = mPhysicsController.selectedPoints().get(i);
			if (lSelectedPoint == null)
				continue;

			Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), lSelectedPoint.worldPosition.x, lSelectedPoint.worldPosition.y, .01f, .95f, 0.97f, 0.f, 1.f);
			Debug.debugManager().drawers().drawCircleImmediate(core.gameCamera(), lSelectedPoint.worldPosition.x, lSelectedPoint.worldPosition.y, 5f, 12);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private boolean isNewRegionValid(EditorPhysicsObjectInstance region) {
		// TODO: Validate dimensions and area of quadrilateral polygon

		return true;
	}

	public void startPhysicsObjectCreation() {
		if (renderPhysicsObjects() == false)
			return; // can't create if layer not visible

		if (mCreationPhysicsObject != null)
			return; // currently creating floor?

		if (mCreationPhysicsObject == null) {
			mCreationPhysicsObject = new EditorPhysicsObjectInstance(ResourceGroupProvider.getRollingEntityNumber());
			mCreationPhysicsObject.body_isStatic = true;
			mCreationPhysicsObject.body_dynamicFriction = .5f;
			mCreationPhysicsObject.body_staticFriction = .5f;
			mCreationPhysicsObject.body_density = 1.f;
			mCreationPhysicsObject.body_restitution = .3f;
			mCreationPhysicsObject.hasPhysicsBody = true;

			final var lWorldX = mEditorBrushController.cursorWorldX();
			final var lWorldY = mEditorBrushController.cursorWorldY();

			mCreationPhysicsObject.initialize(lWorldX, lWorldY, 0.f);
		}

		mIsInCreateMode = true;
		mMouseDownLastFrame = true;
		mIsNewLeftClick = false;
		mNextClickForA = true;
		mNextClickForB = false;
		mNextClickForC = false;
		mNextClickForD = false;

	}

	private void handleCreationInput(LintfordCore core) {
		checkForHoverPoints(core);
		final var lHoverPoint = mPhysicsController.hoverPoint();

		if (mIsNewLeftClick && mNextClickForA) {
			if (lHoverPoint != null)
				mCreationPhysicsObject.a.worldPosition.set(lHoverPoint.worldPosition);
			else
				mCreationPhysicsObject.a.worldPosition.set(mMouseX, mMouseY);

			mIsNewLeftClick = false;
			mNextClickForA = false;
			mNextClickForB = true;
		} else if (mIsNewLeftClick && mNextClickForB) {
			if (lHoverPoint != null)
				mCreationPhysicsObject.b.worldPosition.set(lHoverPoint.worldPosition);
			else
				mCreationPhysicsObject.b.worldPosition.set(mMouseX, mMouseY);

			mIsNewLeftClick = false;
			mNextClickForB = false;
			mNextClickForC = true;
		} else if (mIsNewLeftClick && mNextClickForC) {
			if (lHoverPoint != null)
				mCreationPhysicsObject.c.worldPosition.set(lHoverPoint.worldPosition);
			else
				mCreationPhysicsObject.c.worldPosition.set(mMouseX, mMouseY);

			mIsNewLeftClick = false;
			mNextClickForC = false;
			mNextClickForD = true;
		} else if (mIsNewLeftClick && mNextClickForD) {
			if (lHoverPoint != null)
				mCreationPhysicsObject.d.worldPosition.set(lHoverPoint.worldPosition);
			else
				mCreationPhysicsObject.d.worldPosition.set(mMouseX, mMouseY);

			if (isNewRegionValid(mCreationPhysicsObject)) {

				// re-center the floor x,y in relation to the world vertices
				mCreationPhysicsObject.recalculateBounds();
				// mCreationQuadRegion.body().recalculateBoxCentroidAndRadius();

				mPhysicsController.addPhysicsObjectInstance(mCreationPhysicsObject);
				mHashGridController.hashGrid().addEntity(mCreationPhysicsObject);

				mPhysicsController.selectedRegion(mCreationPhysicsObject);
			}

			cancelAllModes();
			mEditorBrushController.clearAction(hashCode());
			mCreationPhysicsObject = null;
		}
	}

	private void checkForHoverPoints(LintfordCore core) {
		final var leftMouseDown = core.input().mouse().tryAcquireMouseLeftClick(hashCode());

		final var lFloorEntitiesNearby = mPhysicsController.phyiscsObjectEntitiesNearby();
		final var lNumFoundEntities = lFloorEntitiesNearby.size();

		final var lSnapRadiusInPixels = 5.f;
		final var lSnapRadius = ConstantsPhysics.toUnits(lSnapRadiusInPixels);

		mPhysicsController.hoverPoint(null);

		if (leftMouseDown)
			mPhysicsController.selectedPointByCreation(null);

		final var lMouseXInUnits = mMouseX;
		final var lMouseYInUnits = mMouseY;

		if (mSnapToPoints) {
			for (int i = 0; i < lNumFoundEntities; i++) {
				final var lGridEntity = lFloorEntitiesNearby.get(i);
				final var lRectRegion = (EditorPhysicsObjectInstance) lGridEntity;

//				if (CollisionExtensions.intersectsBodyRadius(lRectRegion.body(), lMouseXInUnits, lMouseYInUnits)) {
//					if (Vector2f.dst(lRectRegion.a.transformedPointRef.x, lRectRegion.a.transformedPointRef.y, lMouseXInUnits, lMouseYInUnits) < lSnapRadius) {
//						mFloorController.hoverPoint(lRectRegion.a);
//						if (leftMouseDown)
//							mFloorController.selectedPointByCreation(lRectRegion.a);
//
//					} else if (Vector2f.dst(lRectRegion.b.transformedPointRef.x, lRectRegion.b.transformedPointRef.y, lMouseXInUnits, lMouseYInUnits) < lSnapRadius) {
//						mFloorController.hoverPoint(lRectRegion.b);
//						if (leftMouseDown)
//							mFloorController.selectedPointByCreation(lRectRegion.b);
//
//					} else if (Vector2f.dst(lRectRegion.c.transformedPointRef.x, lRectRegion.c.transformedPointRef.y, lMouseXInUnits, lMouseYInUnits) < lSnapRadius) {
//						mFloorController.hoverPoint(lRectRegion.c);
//						if (leftMouseDown)
//							mFloorController.selectedPointByCreation(lRectRegion.c);
//
//					} else if (Vector2f.dst(lRectRegion.d.transformedPointRef.x, lRectRegion.d.transformedPointRef.y, lMouseXInUnits, lMouseYInUnits) < lSnapRadius) {
//						mFloorController.hoverPoint(lRectRegion.d);
//						if (leftMouseDown)
//							mFloorController.selectedPointByCreation(lRectRegion.d);
//					}
//				}
			}
		}
	}

	private void checkSelectedPoints(LintfordCore core) {
		final var lFloorEntitiesNearby = mPhysicsController.phyiscsObjectEntitiesNearby();
		final var lNumFoundEntities = lFloorEntitiesNearby.size();

		final var lSnapDistInPixels = 5.f;
		final var lSnapRadius = lSnapDistInPixels;

		for (int i = 0; i < lNumFoundEntities; i++) {
			final var lGridEntity = lFloorEntitiesNearby.get(i);
			final var lRectRegion = (EditorPhysicsObjectInstance) lGridEntity;

			if (Vector2f.dst(lRectRegion.a.worldPosition.x, lRectRegion.a.worldPosition.y, mMouseX, mMouseY) < lSnapRadius) {
				mPhysicsController.addSelectedPoint(lRectRegion.a);
			} else if (Vector2f.dst(lRectRegion.b.worldPosition.x, lRectRegion.b.worldPosition.y, mMouseX, mMouseY) < lSnapRadius) {
				mPhysicsController.addSelectedPoint(lRectRegion.b);
			} else if (Vector2f.dst(lRectRegion.c.worldPosition.x, lRectRegion.c.worldPosition.y, mMouseX, mMouseY) < lSnapRadius) {
				mPhysicsController.addSelectedPoint(lRectRegion.c);
			} else if (Vector2f.dst(lRectRegion.d.worldPosition.x, lRectRegion.d.worldPosition.y, mMouseX, mMouseY) < lSnapRadius) {
				mPhysicsController.addSelectedPoint(lRectRegion.d);
			}
		}
	}

	private void handleRightMouseDown(LintfordCore core) {
		if (mEditorBrushController.isLayerActive(EditorLayersData.Physics) == false)
			return;

		if (core.input().mouse().isMouseRightButtonDown()) {
			mIsInCreateMode = false;

			mCreationPhysicsObject = null;
			mEditorBrushController.clearAction(hashCode());

			mPhysicsController.selectedRegion(null);
			mPhysicsController.hoverPoint(null);
			mPhysicsController.selectedPointByCreation(null);
		}
	}

	public void cancelAllModes() {
		mIsInCreateMode = false;

		mNextClickForA = false;
		mNextClickForB = false;
		mNextClickForC = false;
		mNextClickForD = false;

		mIsNewLeftClick = false;
		mCreationPhysicsObject = null;

		mPhysicsController.selectedRegion(null);
		mPhysicsController.hoverPoint(null);
		mPhysicsController.selectedPointByCreation(null);
	}

	public void clearSelectedRegion() {
		final var lSelectionRegion = mPhysicsController.selectedPhysicsObject();
		if (lSelectionRegion == null)
			return;

		if (mPhysicsController.selectedPointByCreation() != null && mPhysicsController.selectedPointByCreation().parent == lSelectionRegion)
			mPhysicsController.selectedPointByCreation(null);

		if (mPhysicsController.hoverPoint() != null && mPhysicsController.hoverPoint().parent == lSelectionRegion)
			mPhysicsController.hoverPoint(null);

		if (lSelectionRegion != null) {
			mPhysicsController.selectedRegion(null);
		}
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

}
