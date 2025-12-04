package lintfordpickle.harvest.screens.editor;

import lintfordpickle.harvest.screens.editor.panels.LayerAnimationPanel;
import lintfordpickle.harvest.screens.editor.panels.LayerNoisePanel;
import lintfordpickle.harvest.screens.editor.panels.LayerPhysicsObjects;
import lintfordpickle.harvest.screens.editor.panels.LayerPhysicsScene;
import lintfordpickle.harvest.screens.editor.panels.LayerTexturePanel;
import lintfordpickle.harvest.screens.editor.panels.LayersPanel;
import lintfordpickle.harvest.screens.editor.panels.ScenePanel;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.renderers.RendererManagerBase;
import net.lintfordlib.renderers.editor.panels.CameraPanel;
import net.lintfordlib.renderers.editor.panels.CursorPanel;
import net.lintfordlib.renderers.editor.panels.FileInfoPanel;
import net.lintfordlib.renderers.editor.panels.GridPanel;
import net.lintfordlib.renderers.editor.panels.UiDockedWindow;
import net.lintfordlib.renderers.windows.components.UiLabel;

public class EditorGui extends UiDockedWindow {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String GUI_NAME = "Editor";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiLabel mWindowTitle;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorGui(RendererManagerBase rendererManager, int entityGroupUid) {
		super(rendererManager, GUI_NAME, entityGroupUid);

		UiDockedWindow.DOCKED_WINDOW_WIDTH = 320;

		mWindowTitle = new UiLabel("Window Title");

		addComponent(mWindowTitle);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lHudBoundBox = core.HUD().boundingRectangle();
		mWindowTitle.set(lHudBoundBox.left(), lHudBoundBox.top(), lHudBoundBox.width(), 35);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void createGuiPanels() {
		super.createGuiPanels();

		editorPanels().add(new FileInfoPanel(this, mEntityGroupUid));
		editorPanels().add(new ScenePanel(this, mEntityGroupUid));
		editorPanels().add(new CameraPanel(this, mEntityGroupUid));
		editorPanels().add(new CursorPanel(this, mEntityGroupUid));
		editorPanels().add(new GridPanel(this, mEntityGroupUid));
		editorPanels().add(new LayersPanel(this, mEntityGroupUid));

		editorPanels().add(new LayerTexturePanel(this, mEntityGroupUid));
		editorPanels().add(new LayerAnimationPanel(this, mEntityGroupUid));
		editorPanels().add(new LayerNoisePanel(this, mEntityGroupUid));
		editorPanels().add(new LayerPhysicsScene(this, mEntityGroupUid));
		editorPanels().add(new LayerPhysicsObjects(this, mEntityGroupUid));
	}

}
