package org.maia.amstrad.gui.covers.fabric;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class FabricPatchPattern {

	private Color backgroundColor;

	private List<FabricPatch> patches;

	public FabricPatchPattern(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		this.patches = new Vector<FabricPatch>();
	}

	public void addPatch(FabricPatch patch) {
		getPatches().add(patch);
	}

	public int[] getInnerVerticalPatchCoords(int totalWidth) {
		Set<Integer> coords = new HashSet<Integer>();
		for (FabricPatch patch : getPatches()) {
			coords.add(patch.getOffsetX());
			coords.add(patch.getOffsetX() + patch.getWidth() - 1);
		}
		coords.remove(0);
		coords.remove(totalWidth - 1);
		List<Integer> sortedCoords = new Vector<Integer>(coords);
		Collections.sort(sortedCoords);
		return toIntArray(sortedCoords);
	}

	public int[] getInnerHorizontalPatchCoords(int totalHeight) {
		Set<Integer> coords = new HashSet<Integer>();
		for (FabricPatch patch : getPatches()) {
			coords.add(patch.getOffsetY());
			coords.add(patch.getOffsetY() + patch.getHeight() - 1);
		}
		coords.remove(0);
		coords.remove(totalHeight - 1);
		List<Integer> sortedCoords = new Vector<Integer>(coords);
		Collections.sort(sortedCoords);
		return toIntArray(sortedCoords);
	}

	private int[] toIntArray(List<Integer> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < list.size(); i++)
			array[i] = list.get(i);
		return array;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public List<FabricPatch> getPatches() {
		return patches;
	}

}