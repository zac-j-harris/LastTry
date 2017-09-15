package org.egordorichev.lasttry.world.components;

import org.egordorichev.lasttry.world.ChestRegistry;
import org.egordorichev.lasttry.world.WorldProvderImpl;

import java.util.ArrayList;

public class WorldChestsComponent extends WorldComponent {
	/**
	 * Info about all chests in the world
	 */
	private ArrayList<ChestRegistry> chests = new ArrayList<>();

	public WorldChestsComponent(WorldProvderImpl world) {
		super(world);
	}

	ChestRegistry getChestRegestry(int x, int y) {
		return null; // TODO
	}

	ChestRegistry getClosestChestRegestry(int x, int y) {
		return null; // TODO
	}
}