package com.sillyspecs;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("sillyspecs")
public interface SillySpecsConfig extends Config
{
	@Range(min = 0, max = 100)
	@ConfigItem(
			keyName = "customVolume",
			name = "Sound Volume",
			description = "Adjust the volume of the custom special attack sounds.",
			position = 1
	)
	default int customVolume()
	{
		return 50;
	}
}