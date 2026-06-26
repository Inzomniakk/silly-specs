package com.sillyspecs;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SillySpecsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SillySpecsPlugin.class);
		RuneLite.main(args);
	}
}