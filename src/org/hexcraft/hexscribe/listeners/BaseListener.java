package org.hexcraft.hexscribe.listeners;

import org.bukkit.event.Listener;
import org.hexcraft.HexScribe;

//-- I don't feel like typing this over and over and over for different classes that implement listener..
//-- so, we will just extend the base class.
public class BaseListener implements Listener {

	public HexScribe plugin;
	
	public BaseListener(HexScribe _plugin)
	{
		this.plugin = _plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
}
