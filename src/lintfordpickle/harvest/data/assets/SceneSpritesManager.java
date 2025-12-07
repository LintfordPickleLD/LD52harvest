package lintfordpickle.harvest.data.assets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;

public class SceneSpritesManager extends BaseInstanceManager {

	private static final String META_FILENAME = "res/def/assets/_meta.json";

	// ---------------------------------------------
	// Inner-Classes
	// ---------------------------------------------

	public class SpriteDefinitionManager extends DefinitionManager<SceneSpriteDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public SpriteDefinitionManager() {
			final var lMetaDataFile = new File(META_FILENAME);
			loadDefinitionsFromMetaFile(lMetaDataFile);
		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider entityLocationProvider) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromFolderWatcherItems(entityLocationProvider, lGson, SceneSpriteDefinition.class);
		}

		@Override
		public void loadDefinitionsFromMetaFile(File file) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromMetaFileItems(file, lGson, SceneSpriteDefinition.class);
		}

		@Override
		public SceneSpriteDefinition loadDefinitionFromFile(File file) {
			final var lGson = new GsonBuilder().create();
			return loadDefinitionFromFile(file, lGson, SceneSpriteDefinition.class);
		}

	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SpriteDefinitionManager mDefinitionManager = new SpriteDefinitionManager();

	private final List<SceneSpriteInstance> mAssetInstances = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public SpriteDefinitionManager definitionManager() {
		return mDefinitionManager;
	}

	@Override
	public void initializeInstanceCounter() {
		// TODO: unimplemented
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public SceneSpritesManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public SceneSpriteInstance createAssetInstanceFromDefinitionName(String definitionName, float worldX, float worldY) {
		final var lDefinition = mDefinitionManager.getByName(definitionName);

		if (lDefinition == null)
			return null;

		final var lAssetInstance = createNewAsset();
		lAssetInstance.initialize(lDefinition, worldX, worldY, 32, 32, 0, 16);

		mAssetInstances.add(lAssetInstance);

		return lAssetInstance;
	}

	private SceneSpriteInstance createNewAsset() {
		return new SceneSpriteInstance(getNewInstanceUid());
	}

	// ---------------------------------------------
	// Inherited-Methods
	// ---------------------------------------------

	@Override
	public void initializeManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeInTrackDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromTrackDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalizeAfterLoading() {
		// TODO Auto-generated method stub

	}

}
