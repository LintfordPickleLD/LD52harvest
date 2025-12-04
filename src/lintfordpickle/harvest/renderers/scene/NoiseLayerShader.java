package lintfordpickle.harvest.renderers.scene;

import org.lwjgl.opengl.GL20;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PT;
import net.lintfordlib.core.storage.FileUtils;

public class NoiseLayerShader extends ShaderMVP_PT {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public final static String SHADER_NAME = "ShaderMVP_PCT";

	public final static String SHADER_VERT_FILENAME = FileUtils.RESOURCE_LOCATION_PREFIX + "/res/shaders/shader_basic_pt.vert";
	public final static String SHADER_FRAG_FILENAME = "res/shaders/shaderForefog.frag";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public NoiseLayerShader() {
		super("Fog Shader", SHADER_VERT_FILENAME, SHADER_FRAG_FILENAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {

		final float lScreenWidth = core.config().display().windowWidth();
		final float lScreenHeight = core.config().display().windowHeight();

		final float lCamPosX = core.gameCamera().getPosition().x * 0.02f;
		final float lCamPosY = -core.gameCamera().getPosition().y * 0.015f;

		final float lTimeAcc = (float) (core.gameTime().totalTimeMilli() * .00001f) % 1000;
		final float lCamZoom = core.gameCamera().getZoomFactorOverOne();
		final float lDayNightMod = 1.f;

		final float lFogColorR = (100.f / 255.f) * lDayNightMod;
		final float lFogColorG = (100.f / 255.f) * lDayNightMod;
		final float lFogColorB = (100.f / 255.f) * lDayNightMod;

		final float lFogLevel = 1.f;

		final float lWindDirX = 1.f;
		final float lWindDirY = 0.f;

		int it = GL20.glGetUniformLocation(shaderID(), "timeAcc");

		GL20.glUniform2f(GL20.glGetUniformLocation(shaderID(), "screenDimensions"), lScreenWidth, lScreenHeight);
		GL20.glUniform2f(GL20.glGetUniformLocation(shaderID(), "cameraPosition"), lCamPosX, lCamPosY);
		GL20.glUniform1f(GL20.glGetUniformLocation(shaderID(), "timeAcc"), lTimeAcc);
		GL20.glUniform1f(GL20.glGetUniformLocation(shaderID(), "cameraZoom"), lCamZoom);
		GL20.glUniform3f(GL20.glGetUniformLocation(shaderID(), "fogColor"), lFogColorR, lFogColorG, lFogColorB);
		GL20.glUniform1f(GL20.glGetUniformLocation(shaderID(), "fadeAmount"), lFogLevel);
		GL20.glUniform2f(GL20.glGetUniformLocation(shaderID(), "windDir"), lWindDirX, lWindDirY);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();

		super.getUniformLocations();
		int lColorSamplerLocation = GL20.glGetUniformLocation(shaderID(), "lightSampler");

		GL20.glUniform1i(lColorSamplerLocation, 0);
	}
}
