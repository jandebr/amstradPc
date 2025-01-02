package org.maia.amstrad.program.repo.facet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Icon;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.swing.util.ImageUtils;

public class FacetFactory {

	private Collection<Facet> allFacets;

	private static FacetFactory instance;

	private static final String ICON_PATH = "org/maia/amstrad/gui/icons/facet/";

	private FacetFactory() {
	}

	public Collection<Facet> getAllFacets() {
		if (allFacets == null) {
			allFacets = new Vector<Facet>();
			allFacets.add(new AuthorFacet());
			allFacets.add(new YearFacet());
			allFacets.add(new TapeFacet());
			allFacets.add(new BlocksFacet());
			allFacets.add(new MonitorFacet());
		}
		return allFacets;
	}

	public String toExternalForm(FacetList facetList) {
		StringBuilder sb = new StringBuilder(64);
		for (int i = 0; i < facetList.size(); i++) {
			if (i > 0)
				sb.append(',');
			sb.append(facetList.getFacet(i).toExternalForm());
		}
		return sb.toString();
	}

	public FacetList fromExternalForm(String str) {
		Map<String, Facet> index = new HashMap<String, Facet>();
		for (Facet facet : getAllFacets()) {
			index.put(facet.toExternalForm(), facet);
		}
		FacetList facetList = new FacetList();
		StringTokenizer st = new StringTokenizer(str, ",");
		while (st.hasMoreTokens()) {
			Facet facet = index.get(st.nextToken());
			if (facet != null)
				facetList.add(facet);
		}
		return facetList;
	}

	public static FacetFactory getInstance() {
		if (instance == null) {
			setInstance(new FacetFactory());
		}
		return instance;
	}

	private static synchronized void setInstance(FacetFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

	private static class AuthorFacet extends Facet {

		public AuthorFacet() {
		}

		@Override
		public Icon getIcon() {
			return ImageUtils.getIcon(ICON_PATH + "author24.png");
		}

		@Override
		protected String extractValueFrom(AmstradProgram program) {
			return program.getAuthor();
		}

		@Override
		String toExternalForm() {
			return "author";
		}

	}

	private static class YearFacet extends Facet {

		public YearFacet() {
		}

		@Override
		public Icon getIcon() {
			return ImageUtils.getIcon(ICON_PATH + "year24.png");
		}

		@Override
		protected String extractValueFrom(AmstradProgram program) {
			int year = program.getProductionYear();
			return year > 0 ? String.valueOf(year) : null;
		}

		@Override
		String toExternalForm() {
			return "year";
		}

	}

	private static class TapeFacet extends Facet {

		public TapeFacet() {
		}

		@Override
		public Icon getIcon() {
			return ImageUtils.getIcon(ICON_PATH + "tape24.png");
		}

		@Override
		protected String extractValueFrom(AmstradProgram program) {
			return program.getNameOfTape();
		}

		@Override
		String toExternalForm() {
			return "tape";
		}

	}

	private static class BlocksFacet extends Facet {

		public BlocksFacet() {
		}

		@Override
		public Icon getIcon() {
			return ImageUtils.getIcon(ICON_PATH + "blocks24.png");
		}

		@Override
		protected String extractValueFrom(AmstradProgram program) {
			int blocks = program.getBlocksOnTape();
			return blocks > 0 ? String.valueOf(blocks) : null;
		}

		@Override
		String toExternalForm() {
			return "blocks";
		}

	}

	private static class MonitorFacet extends Facet {

		public MonitorFacet() {
		}

		@Override
		public Icon getIcon() {
			return ImageUtils.getIcon(ICON_PATH + "monitor24.png");
		}

		@Override
		protected String extractValueFrom(AmstradProgram program) {
			AmstradMonitorMode mode = program.getPreferredMonitorMode();
			return mode != null ? mode.name() : null;
		}

		@Override
		String toExternalForm() {
			return "color";
		}

	}

}