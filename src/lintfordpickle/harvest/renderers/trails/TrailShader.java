package lintfordpickle.harvest.renderers.trails;

import org.lwjgl.opengl.GL20;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PCT;
import net.lintfordlib.core.storage.FileUtils;

public class TrailShader extends ShaderMVP_PCT {

	// --------------------------------------
	// COnstants
	// --------------------------------------

	private static final String VERT_FILENAME = FileUtils.RESOURCE_LOCATION_PREFIX + "/res/shaders/shader_batch_pct.vert";
	private static final String FRAG_FILENAME = "res/shaders/shader_trail_batch_pct.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mTimeAcc;

	private int mTimeShaderLocation;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TrailShader(String shaderName) {
		super(shaderName, VERT_FILENAME, FRAG_FILENAME);
		mTimeShaderLocation = -1;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
		mTimeAcc += (float) core.gameTime().elapsedTimeMilli() * 0.001f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void updateUniforms() {
		super.updateUniforms();

		if (mTimeShaderLocation != -1)
			GL20.glUniform1f(mTimeShaderLocation, mTimeAcc);

	}

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();

		mTimeShaderLocation = GL20.glGetUniformLocation(shaderID(), "time");
	}

}
