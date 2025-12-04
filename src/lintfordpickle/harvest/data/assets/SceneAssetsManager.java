package lintfordpickle.harvest.data.assets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;

public class SceneAssetsManager extends BaseInstanceManager {

	private static final String META_FILENAME = "res/def/assets/_meta.json";

	// ---------------------------------------------
	// Inner-Classes
	// ---------------------------------------------

	public class AssetsDefinitionManager extends DefinitionManager<SceneAssetDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public AssetsDefinitionManager() {
			final var lMetaDataFile = new File(META_FILENAME);
			loadDefinitionsFromMetaFile(lMetaDataFile);
		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider entityLocationProvider) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromFolderWatcherItems(entityLocationProvider, lGson, SceneAssetDefinition.class);
		}

		@Override
		public void loadDefinitionsFromMetaFile(File file) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromMetaFileItems(file, lGson, SceneAssetDefinition.class);
		}

		@Override
		public SceneAssetDefinition loadDefinitionFromFile(File file) {
			final var lGson = new GsonBuilder().create();
			return loadDefinitionFromFile(file, lGson, SceneAssetDefinition.class);
		}

	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private AssetsDefinitionManager mDefinitionManager = new AssetsDefinitionManager();

	private final List<SceneAssetInstance> mAssetInstances = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public AssetsDefinitionManager definitionManager() {
		return mDefinitionManager;
	}

	@Override
	public void initializeInstanceCounter() {
		// TODO: unimplemented
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public SceneAssetsManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public SceneAssetInstance createAssetInstanceFromDefinitionName(String definitionName, float worldX, float worldY) {
		final var lDefinition = mDefinitionManager.getByName(definitionName);

		if (lDefinition == null)
			return null;

		final var lAssetInstance = createNewAsset();
		lAssetInstance.initialize(lDefinition, worldX, worldY, 32, 32, 0, 16);

		mAssetInstances.add(lAssetInstance);

		return lAssetInstance;
	}

	private SceneAssetInstance createNewAsset() {
		return new SceneAssetInstance(getNewInstanceUid());
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
