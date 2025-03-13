package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.carousel.CarouselComponent.CarouselOutline;
import org.maia.amstrad.gui.browser.carousel.caption.FolderCaptionComponent;
import org.maia.amstrad.gui.browser.carousel.caption.ProgramCaptionComponent;
import org.maia.amstrad.gui.browser.carousel.info.FolderInfoSection;
import org.maia.amstrad.gui.browser.carousel.info.InfoIcon;
import org.maia.amstrad.gui.browser.carousel.info.InfoSection;
import org.maia.amstrad.gui.browser.carousel.info.ProgramAuthoringInfoSection;
import org.maia.amstrad.gui.browser.carousel.info.ProgramControlsInfoSection;
import org.maia.amstrad.gui.browser.carousel.info.ProgramDescriptionInfoSection;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.swing.FillMode;
import org.maia.swing.HorizontalAlignment;
import org.maia.swing.VerticalAlignment;
import org.maia.swing.animate.imageslide.show.SlidingImageShow;
import org.maia.swing.animate.imageslide.show.SlidingImageShowBuilder;
import org.maia.swing.animate.itemslide.SlidingCursorMovement;
import org.maia.swing.animate.itemslide.impl.SlidingCursorFactory;
import org.maia.swing.animate.itemslide.impl.SlidingDynamicsFactory;
import org.maia.swing.animate.itemslide.impl.SlidingItemLayoutManagerFactory;
import org.maia.swing.animate.itemslide.impl.SlidingShadeFactory;
import org.maia.swing.animate.itemslide.outline.SolidFillOutlineRenderer;
import org.maia.swing.animate.textslide.SlidingTextLabel;
import org.maia.swing.image.GradientImageFactory;
import org.maia.swing.image.GradientImageFactory.GradientFunction;
import org.maia.swing.text.TextLabel;
import org.maia.swing.util.ColorUtils;
import org.maia.swing.util.ImageUtils;
import org.maia.util.StringUtils;

public class CarouselComponentFactory implements CarouselItemMaker, CarouselBreadcrumbItemMaker {

	private CarouselProgramBrowserTheme theme;

	private CarouselLayoutManager layout;

	public CarouselComponentFactory(CarouselProgramBrowserTheme theme, CarouselLayoutManager layout) {
		this.theme = theme;
		this.layout = layout;
	}

	public SlidingTextLabel createHeadingComponent(Node node) {
		int width = getLayout().getHeadingBounds().width;
		int height = getLayout().getHeadingBounds().height;
		if (!hasImageShow(node)) {
			// avoid sliding when there is space
			width = getLayout().getHeadingBounds().union(getLayout().getPreviewBounds()).width;
		}
		Dimension size = new Dimension(width, height);
		Font font = getTheme().getHeadingFont();
		float fontSize = TextLabel.getFontSizeForLineHeight(font, size.height);
		SlidingTextLabel label = SlidingTextLabel.createLine(node.getName(), font.deriveFont(fontSize), size.width,
				HorizontalAlignment.LEFT, getTheme().getBackgroundColor(), getTheme().getHeadingColor());
		label.setSlidingSpeed(20.0);
		label.setSuspensionAtEndsMillis(1000L);
		label.setRepaintClientDriven(true);
		return label;
	}

	public List<InfoSection> createInfoSectionsForNode(Node node) {
		List<InfoSection> sections = new Vector<InfoSection>();
		if (node.isProgram()) {
			AmstradProgram program = node.asProgram().getProgram();
			addInfoSection(sections, createProgramDescriptionSection(program));
			addInfoSection(sections, createProgramControlsSection(program));
			addInfoSection(sections, createProgramAuthoringSection(program));
		} else if (node.isFolder()) {
			addInfoSection(sections, createFolderInfoSection(node.asFolder()));
		}
		return sections;
	}

	private void addInfoSection(List<InfoSection> sections, InfoSection section) {
		if (section != null) {
			sections.add(section);
		}
	}

	private InfoSection createProgramDescriptionSection(AmstradProgram program) {
		InfoSection section = null;
		if (ProgramDescriptionInfoSection.hasInfo(program)) {
			InfoIcon icon = createInfoIcon("description-bright32.png", Color.BLACK, "description-dark32.png",
					Color.ORANGE);
			section = new ProgramDescriptionInfoSection(icon, this, program);
		}
		return section;
	}

	private InfoSection createProgramControlsSection(AmstradProgram program) {
		InfoSection section = null;
		if (ProgramControlsInfoSection.hasInfo(program)) {
			InfoIcon icon = createInfoIcon("controls-bright32.png", Color.BLACK, "controls-dark32.png", Color.ORANGE);
			section = new ProgramControlsInfoSection(icon, this, program);
		}
		return section;
	}

	private InfoSection createProgramAuthoringSection(AmstradProgram program) {
		InfoSection section = null;
		if (ProgramAuthoringInfoSection.hasInfo(program)) {
			InfoIcon icon = createInfoIcon("authoring-bright32.png", Color.BLACK, "authoring-dark32.png", Color.ORANGE);
			section = new ProgramAuthoringInfoSection(icon, this, program);
		}
		return section;
	}

	private InfoSection createFolderInfoSection(FolderNode folder) {
		InfoSection section = null;
		if (FolderInfoSection.hasInfo(folder)) {
			InfoIcon icon = createInfoIcon("description-bright32.png", Color.BLACK, "description-dark32.png",
					Color.ORANGE);
			section = new FolderInfoSection(icon, this, folder);
		}
		return section;
	}

	private InfoIcon createInfoIcon(String unselectedImagePath, Color unselectedBackground, String selectedImagePath,
			Color selectedBackground) {
		int height = getLayout().getCaptionBounds().height;
		Dimension size = new Dimension(height, height);
		Image unselectedImage = UIResources.loadIcon("browser/" + unselectedImagePath).getImage();
		Image selectedImage = UIResources.loadIcon("browser/" + selectedImagePath).getImage();
		InfoIcon icon = new InfoIcon(unselectedImage, unselectedBackground, selectedImage, selectedBackground);
		icon.setFillMode(FillMode.FIT);
		icon.setPreferredSize(size);
		icon.setMinimumSize(size);
		icon.setMaximumSize(size);
		return icon;
	}

	public JComponent createCaptionComponent(Node node, List<InfoSection> infoSections) {
		if (node.isProgram()) {
			AmstradProgram program = node.asProgram().getProgram();
			return new ProgramCaptionComponent(this, program, infoSections);
		} else if (node.isFolder()) {
			return new FolderCaptionComponent(this, node.asFolder());
		} else {
			return null;
		}
	}

	public SlidingImageShow createImageShow(Node node) {
		SlidingImageShow show = null;
		if (node.isProgram()) {
			List<AmstradProgramImage> images = node.asProgram().getProgram().getImages();
			if (!images.isEmpty()) {
				Dimension size = getLayout().getPreviewBounds().getSize();
				SlidingImageShowBuilder builder = new SlidingImageShowBuilder(size, getTheme().getBackgroundColor());
				builder.withMaxToMinZoomFactorRatio(3.0);
				builder.withRepaintClientDriven(true);
				builder.withImageOverlay(createImageShowOverlay(size));
				for (AmstradProgramImage image : images) {
					builder.addImage(image.getImage());
				}
				show = builder.build();
			}
		}
		return show;
	}

	public boolean hasImageShow(Node node) {
		return node.isProgram() && !node.asProgram().getProgram().getImages().isEmpty();
	}

	private Image createImageShowOverlay(Dimension size) {
		int w = size.width, h = size.height;
		int tw = Math.max(w / 4, 8), th = Math.max(h / 4, 8);
		Color c1 = getTheme().getBackgroundColor();
		Color c2 = ColorUtils.setTransparency(c1, 1.0);
		GradientFunction function = GradientImageFactory.createSigmoidGradientFunction();
		BufferedImage left = ImageUtils.addPadding(
				GradientImageFactory.createLeftToRightGradientImage(new Dimension(tw, h), c1, c2, function),
				new Insets(0, 0, 0, w - tw), c2);
		BufferedImage bottom = ImageUtils.addPadding(
				GradientImageFactory.createBottomToTopGradientImage(new Dimension(w, th), c1, c2, function),
				new Insets(h - th, 0, 0, 0), c2);
		BufferedImage base = ImageUtils.createImage(size, new Color(0, 0, 0, 50));
		return ImageUtils.combineByTransparency(base, ImageUtils.combineByTransparency(left, bottom));
	}

	public CarouselComponent createCarouselComponent(CarouselHost host) {
		Dimension size = getLayout().getCarouselBounds().getSize();
		Insets padding = new Insets(8, 8, 8, 8);
		CarouselComponent comp = new CarouselComponent(size, padding, getTheme().getBackgroundColor(),
				SlidingCursorMovement.LAZY, host, this);
		comp.setLayoutManager(SlidingItemLayoutManagerFactory.createHorizontallySlidingCenterAlignedLayout(comp,
				VerticalAlignment.CENTER));
		comp.setShade(SlidingShadeFactory.createGradientShadeRelativeLength(comp, 0.15));
		comp.setSlidingCursor(
				SlidingCursorFactory.createSolidOutlineCursor(getTheme().getCarouselCursorColor(), 6, 1, true));
		comp.setSlidingDynamics(SlidingDynamicsFactory.createAdaptiveSpeedDynamics(comp, 0.002, 1.5, 0.5));
		comp.setSteadyLandingMinimumTimeDelayMillis(100L);
		comp.setRepaintClientDriven(true);
		return comp;
	}

	@Override
	public CarouselItem createCarouselItemForNode(Node repositoryNode, CarouselComponent comp) {
		return createCarouselItem(repositoryNode, comp);
	}

	@Override
	public CarouselItem createCarouselItemForEmptyFolder(CarouselComponent comp) {
		return createCarouselItem(null, comp);
	}

	private CarouselItem createCarouselItem(Node repositoryNode, CarouselComponent comp) {
		CarouselItem item = null;
		Insets margin = new Insets(8, 16, 8, 16);
		int height = comp.getViewportHeight();
		int width = (int) Math.round(height * 0.8);
		Dimension size = new Dimension(width, height);
		Font font = getTheme().getCarouselFont();
		float fontSize = TextLabel.getFontSizeForLineWidth(font, StringUtils.repeat('a', 16), width - 20);
		font = font.deriveFont(fontSize);
		if (repositoryNode == null) {
			item = new CarouselEmptyItem(comp, size, margin, font);
		} else if (repositoryNode.isFolder()) {
			item = new CarouselFolderItem(repositoryNode.asFolder(), comp, size, margin, font);
		} else if (repositoryNode.isProgram()) {
			item = new CarouselProgramItem(repositoryNode.asProgram(), comp, size, margin, font);
		}
		return item;
	}

	public CarouselOutline createCarouselOutline(CarouselComponent comp) {
		int thickness = getLayout().getCarouselOutlineBounds().height;
		CarouselOutline outline = comp.createOutline(thickness);
		outline.setBorder(BorderFactory.createLineBorder(getTheme().getCarouselOutlineBorderColor()));
		outline.setExtentMargin(new Insets(1, 0, 1, 0));
		outline.setExtentRenderer(null);
		outline.setCursorBorder(BorderFactory.createLineBorder(getTheme().getCarouselOutlineCursorBorderColor()));
		outline.setCursorRenderer(new SolidFillOutlineRenderer(getTheme().getCarouselOutlineCursorFillColor()));
		return outline;
	}

	public CarouselBreadcrumb createCarouselBreadcrumb(CarouselHost host) {
		Dimension size = getLayout().getCarouselBreadcrumbBounds().getSize();
		Insets padding = new Insets(1, 4, 1, 4);
		CarouselBreadcrumb comp = new CarouselBreadcrumb(size, padding, getTheme().getBackgroundColor(),
				SlidingCursorMovement.EAGER, host, this);
		comp.setLayoutManager(SlidingItemLayoutManagerFactory.createHorizontallySlidingLeftAlignedLayout(comp,
				VerticalAlignment.CENTER));
		comp.setShade(SlidingShadeFactory.createGradientShadeRelativeLength(comp, 0.15));
		comp.setSlidingCursor(SlidingCursorFactory.createSolidOutlineCursor(getTheme().getBreadcrumbCursorColor(), 1));
		comp.setRepaintClientDriven(true);
		return comp;
	}

	@Override
	public CarouselBreadcrumbItem createBreadcrumbItemForFolder(FolderNode folderNode, CarouselBreadcrumb breadcrumb) {
		String text = folderNode.isRoot() ? "HOME" : folderNode.getName();
		return createBreadcrumbItem(folderNode, text, getTheme().getBreadcrumbFolderColor(),
				getTheme().getBreadcrumbSelectedFolderColor(), breadcrumb);
	}

	@Override
	public CarouselBreadcrumbItem createBreadcrumbSeparatorItem(CarouselBreadcrumb breadcrumb) {
		return createBreadcrumbItem(null, ">", getTheme().getBreadcrumbSeparatorColor(), null, breadcrumb);
	}

	private CarouselBreadcrumbItem createBreadcrumbItem(FolderNode folderNode, String text, Color textColor,
			Color selectedTextColor, CarouselBreadcrumb breadcrumb) {
		Insets margin = new Insets(1, 6, 1, 6);
		Insets insets = new Insets(0, 4, 2, 4);
		int lineHeight = breadcrumb.getViewportHeight() - insets.top - insets.bottom;
		Font font = getTheme().getBreadcrumbFont();
		float fontSize = TextLabel.getFontSizeForLineHeight(font, lineHeight);
		TextLabel label = TextLabel.createLineLabel(text, font.deriveFont(fontSize), HorizontalAlignment.CENTER,
				insets);
		label.setSize(label.getPreferredSize());
		label.setBackground(getTheme().getBackgroundColor());
		label.setForeground(textColor);
		return new CarouselBreadcrumbItemImpl(breadcrumb, folderNode, label, margin, selectedTextColor);
	}

	public CarouselProgramBrowserTheme getTheme() {
		return theme;
	}

	public CarouselLayoutManager getLayout() {
		return layout;
	}

}