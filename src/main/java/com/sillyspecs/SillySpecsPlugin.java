package com.sillyspecs;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;

@Slf4j
@PluginDescriptor(
		name = "Silly Specs",
		description = "Add some silliness to your special attacks!"
)
public class SillySpecsPlugin extends Plugin
{
	@Inject
	private SillySpecsConfig config;

	@Provides
	SillySpecsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SillySpecsConfig.class);
	}
	private static final int FANG_SPEC_PART_1 = 9367;
	private static final int FANG_SPEC_PART_2 = 9366;
	private static final int FANG_SPEC_PART_3 = 9365;
	private long lastPlayed = 0;

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		int soundId = event.getSoundId();
		if (soundId == 9367 || soundId == 9366 || soundId == 9365)
		{
			event.consume();

			long currentTime = System.currentTimeMillis();
			if (currentTime - lastPlayed > 500)
			{
				log.info("Triggering custom sound for ID: {}", soundId);
				playCustomSound("fang_spec.wav");
				lastPlayed = currentTime;
			}
		}
	}

	private void playCustomSound(String fileName)
	{
		try
		{
			InputStream audioSrc = getClass().getResourceAsStream("/" + fileName);
			if (audioSrc == null)
			{
				log.warn("Custom sound file not found: " + fileName);
				return;
			}

			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);

			FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

			float volume = config.customVolume() / 100f;
			float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);

			volumeControl.setValue(dB);

			clip.start();
		}
		catch (Exception e)
		{
			log.error("Failed to play custom sound: " + fileName, e);
		}
	}
}