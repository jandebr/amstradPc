package org.maia.amstrad.gui.browser.carousel.info;

import org.maia.amstrad.gui.browser.carousel.CarouselComponentFactory;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;

public class FolderInfoSection extends CarouselInfoSection {

	private FolderNode folder;

	public FolderInfoSection(InfoIcon icon, CarouselComponentFactory factory, FolderNode folder) {
		super(icon, factory);
		this.folder = folder;
	}

	public static boolean hasInfo(FolderNode folder) {
		return true;
	}

	@Override
	protected SlidingItemListComponent createInfoComponent() {
		InfoText infoText = createInfoText();
		if (getFolder().isEmpty()) {
			infoText.makeEmpty();
		} else {
			CarouselProgramBrowserTheme theme = getTheme();
			for (Node node : getFolder().getChildNodes()) {
				String name = node.getName();
				if (node.isFolder()) {
					infoText.appendText("> " + name, theme.getFolderInfoSubfolderColor(),
							theme.getFolderInfoSubfolderFont(), 0.6f);
				} else if (node.isProgram()) {
					infoText.appendText(name, theme.getFolderInfoProgramColor(), theme.getFolderInfoProgramFont(),
							0.6f);
				}
			}
		}
		return infoText.getComponent();
	}

	public FolderNode getFolder() {
		return folder;
	}

}