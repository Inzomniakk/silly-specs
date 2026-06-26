package com.sillyspecs;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Slf4j
@PluginDescriptor(name = "SillySpecs")
public class SillySpecsPlugin extends Plugin
{
	@Inject
	private AudioPlayer audioPlayer;

	@Inject
	private SillySpecsConfig config;

	private static final long COOLDOWN_MS = 500;
	private static final Set<Integer> FANG_SPEC_IDS = Set.of(9365, 9366, 9367);
	private static final Set<Integer> CLAW_SPEC_IDS = Set.of(4138, 4139, 4140);
	private static final int DBAXE_SPEC_ID = 2538;

	private long lastFangTime = 0;
	private long lastClawTime = 0;
	private long lastDbaxeTime = 0;

	@Provides
	SillySpecsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SillySpecsConfig.class);
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
		int soundId = event.getSoundId();
		String fileName = null;
		long lastTime = 0;

		if (CLAW_SPEC_IDS.contains(soundId)) {
			fileName = "dclaw_spec.wav";
			lastTime = lastClawTime;
		} else if (FANG_SPEC_IDS.contains(soundId)) {
			fileName = "fang_spec.wav";
			lastTime = lastFangTime;
		} else if (soundId == DBAXE_SPEC_ID) {
			fileName = "dbaxe_spec.wav";
			lastTime = lastDbaxeTime;
		}

		if (fileName != null) {
			event.consume();
			if (System.currentTimeMillis() - lastTime > COOLDOWN_MS) {
				updateLastTime(soundId, System.currentTimeMillis());
				playCustomSound(fileName);
			}
		}
	}

	private void updateLastTime(int soundId, long time) {
		if (CLAW_SPEC_IDS.contains(soundId)) lastClawTime = time;
		else if (FANG_SPEC_IDS.contains(soundId)) lastFangTime = time;
		else if (soundId == DBAXE_SPEC_ID) lastDbaxeTime = time;
	}

	private void playCustomSound(String fileName) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
		// AudioPlayer.play consumes the InputStream and handles its own buffering and volume
		InputStream is = getClass().getResourceAsStream("/" + fileName);
		if (is == null)
		{
			log.error("Could not find file: /{}", fileName);
			return;
		}

		float volume = config.customVolume() / 100f;

		// AudioPlayer handles gain/volume scaling natively
		if (volume > 0.0f)
		{
			audioPlayer.play(is, volume);
		}
	}
}