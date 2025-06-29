package org.maia.amstrad.gui.covers.fabric;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.covers.fabric.FabricPatch.Orientation;
import org.maia.amstrad.gui.covers.util.Randomizer;

public abstract class FabricPatchStacking {

	private FabricPatchStacking() {
	}

	public static FabricPatchStacking getStackingOf(FabricPatchPattern pattern, Randomizer randomizer) {
		FabricPatchStackingOrder order = pattern.getStackingOrder();
		if (FabricPatchStackingOrder.ORIGINAL.equals(order)) {
			return new OriginalStacking();
		} else if (FabricPatchStackingOrder.VERTICAL_OVER_HORIZONTAL.equals(order)) {
			return new VerticalOverHorizontalStacking();
		} else if (FabricPatchStackingOrder.HORIZONTAL_OVER_VERTICAL.equals(order)) {
			return new HorizontalOverVerticalStacking();
		} else if (FabricPatchStackingOrder.ALTERNATING_HORIZONTAL_VERTICAL.equals(order)) {
			return new AlternatingHorizontalVerticalStacking();
		} else if (FabricPatchStackingOrder.ALTERNATING_VERTICAL_HORIZONTAL.equals(order)) {
			return new AlternatingVerticalHorizontalStacking();
		} else if (FabricPatchStackingOrder.RANDOM.equals(order)) {
			return new RandomStacking(randomizer);
		} else {
			// default
			return new OriginalStacking();
		}
	}

	public final List<FabricPatch> getPatchesInDrawingOrder(FabricPatchPattern pattern) {
		return getPatchesInDrawingOrder(pattern.getPatches());
	}

	public abstract List<FabricPatch> getPatchesInDrawingOrder(List<FabricPatch> patches);

	private static class OriginalStacking extends FabricPatchStacking {

		public OriginalStacking() {
		}

		@Override
		public List<FabricPatch> getPatchesInDrawingOrder(List<FabricPatch> patches) {
			return patches;
		}

	}

	private static class VerticalOverHorizontalStacking extends FabricPatchStacking {

		public VerticalOverHorizontalStacking() {
		}

		@Override
		public List<FabricPatch> getPatchesInDrawingOrder(List<FabricPatch> patches) {
			List<FabricPatch> result = new Vector<FabricPatch>(patches.size());
			// first horizontal
			for (FabricPatch patch : patches) {
				if (patch.isHorizontal())
					result.add(patch);
			}
			// then vertical
			for (FabricPatch patch : patches) {
				if (patch.isVertical())
					result.add(patch);
			}
			return result;
		}

	}

	private static class HorizontalOverVerticalStacking extends FabricPatchStacking {

		public HorizontalOverVerticalStacking() {
		}

		@Override
		public List<FabricPatch> getPatchesInDrawingOrder(List<FabricPatch> patches) {
			List<FabricPatch> result = new Vector<FabricPatch>(patches.size());
			// first vertical
			for (FabricPatch patch : patches) {
				if (patch.isVertical())
					result.add(patch);
			}
			// then horizontal
			for (FabricPatch patch : patches) {
				if (patch.isHorizontal())
					result.add(patch);
			}
			return result;
		}

	}

	private static abstract class AlternatingStacking extends FabricPatchStacking {

		protected AlternatingStacking() {
		}

		@Override
		public List<FabricPatch> getPatchesInDrawingOrder(List<FabricPatch> patches) {
			List<FabricPatch> remaining = new Vector<FabricPatch>(patches);
			List<FabricPatch> result = new Vector<FabricPatch>(patches.size());
			Orientation nextOrientation = getInitialOrientation();
			while (!remaining.isEmpty()) {
				FabricPatch nextPatch = remaining.get(0);
				for (FabricPatch patch : remaining) {
					if (patch.getOrientation().equals(nextOrientation)) {
						nextPatch = patch;
						break;
					}
				}
				remaining.remove(nextPatch);
				result.add(nextPatch);
				nextOrientation = Orientation.HORIZONTAL.equals(nextOrientation) ? Orientation.VERTICAL
						: Orientation.HORIZONTAL;
			}
			return result;
		}

		protected abstract Orientation getInitialOrientation();

	}

	private static class AlternatingHorizontalVerticalStacking extends AlternatingStacking {

		public AlternatingHorizontalVerticalStacking() {
		}

		@Override
		protected Orientation getInitialOrientation() {
			return Orientation.HORIZONTAL;
		}

	}

	private static class AlternatingVerticalHorizontalStacking extends AlternatingStacking {

		public AlternatingVerticalHorizontalStacking() {
		}

		@Override
		protected Orientation getInitialOrientation() {
			return Orientation.VERTICAL;
		}

	}

	private static class RandomStacking extends FabricPatchStacking {

		private Randomizer randomizer;

		public RandomStacking(Randomizer randomizer) {
			this.randomizer = randomizer;
		}

		@Override
		public List<FabricPatch> getPatchesInDrawingOrder(List<FabricPatch> patches) {
			List<FabricPatch> result = new Vector<FabricPatch>(patches);
			getRandomizer().shuffle(result);
			return result;
		}

		private Randomizer getRandomizer() {
			return randomizer;
		}

	}

}