package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.carousel.CarouselComponent.CarouselOutline;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumb;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItem;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItemImpl;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItemMaker;
import org.maia.amstrad.gui.browser.carousel.caption.FolderCaptionComponent;
import org.maia.amstrad.gui.browser.carousel.caption.ProgramCaptionComponent;
import org.maia.amstrad.gui.browser.carousel.info.FolderInfoSection;
import org.maia.amstrad.gui.browser.carousel.info.InfoIcon;
import org.maia.amstrad.gui.browser.carousel.info.InfoSection;
import org.maia.amstrad.gui.browser.carousel.info.ProgramAuthoringInfoSection;
import org.maia.amstrad.gui.browser.carousel.info.ProgramControlsInfoSection;
import org.maia.amstrad.gui.browser.carousel.info.ProgramDescriptionInfoSection;
import org.maia.amstrad.gui.browser.carousel.item.CarouselEmptyItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselFolderItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItemMaker;
import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.GradientImageFactory;
import org.maia.graphics2d.image.GradientImageFactory.GradientFunction;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.swing.animate.imageslide.show.SlidingImageShow;
import org.maia.swing.animate.imageslide.show.SlidingImageShowBuilder;
import org.maia.swing.animate.itemslide.SlidingCursorMovement;
import org.maia.swing.animate.itemslide.impl.SlidingCursorFactory;
import org.maia.swing.animate.itemslide.impl.SlidingDynamicsFactory;
import org.maia.swing.animate.itemslide.impl.SlidingItemLayoutManagerFactory;
import org.maia.swing.animate.itemslide.impl.SlidingShadeFactory;
import org.maia.swing.animate.itemslide.outline.SolidFillOutlineRenderer;
import org.maia.swing.animate.textslide.SlidingTextLabel;
import org.maia.swing.layout.HorizontalAlignment;
import org.maia.swing.layout.VerticalAlignment;
import org.maia.swing.text.TextLabel;
import org.maia.swing.text.VerticalTextAlignment;
import org.maia.util.ColorUtils;
import org.maia.util.StringUtils;

public class CarouselComponentFactory implements CarouselItemMaker, CarouselBreadcrumbItemMaker {

	private CarouselProgramBrowserTheme theme;

	private CarouselLayoutManager layout;

	private AmstradProgramCoverImageProducer programCoverImageProducer;

	private AmstradFolderCoverImageProducer folderCoverImageProducer;

	public CarouselComponentFactory(CarouselProgramBrowserTheme theme, CarouselLayoutManager layout,
			AmstradProgramCoverImageProducer programCoverImageProducer,
			AmstradFolderCoverImageProducer folderCoverImageProducer) {
		this.theme = theme;
		this.layout = layout;
		this.programCoverImageProducer = programCoverImageProducer;
		this.folderCoverImageProducer = folderCoverImageProducer;
	}

	public SlidingTextLabel createHeadingComponent(Node node) {
		String text = node.getName();
		Dimension size = extendHorizontallyAcrossImageShow(getLayout().getHeadingBounds(), node);
		Font font = getTheme().getHeadingFont();
		float fontSize = TextLabel.getFontSizeForLineHeight(font, size.height);
		float fontSizeFitWidth = TextLabel.getFontSizeForLineWidth(font, text, size.width);
		if (fontSizeFitWidth < fontSize && fontSizeFitWidth >= fontSize * 0.8f)
			fontSize = fontSizeFitWidth;
		SlidingTextLabel label = SlidingTextLabel.createSized(text, font.deriveFont(fontSize), size,
				getTheme().getBackgroundColor(), getTheme().getHeadingColor(), HorizontalAlignment.LEFT,
				VerticalTextAlignment.BASELINE);
		label.setSlidingSpeed(20.0);
		label.setSuspensionAtEndsMillis(1000L);
		label.setRepaintClientDriven(true);
		return label;
	}

	private Dimension extendHorizontallyAcrossImageShow(Rectangle bounds, Node node) {
		int width = bounds.width;
		int height = bounds.height;
		if (!hasImageShow(node)) {
			// extend when there is space
			width = bounds.union(getLayout().getPreviewBounds()).width;
		}
		return new Dimension(width, height);
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
		if (!sections.isEmpty()) {
			sections.get(0).getIcon().setSelected(true);
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
			InfoIcon icon = createInfoIcon("description-gray32.png", "description32.png");
			section = new ProgramDescriptionInfoSection(icon, getTheme(), getLayout(), program);
		}
		return section;
	}

	private InfoSection createProgramControlsSection(AmstradProgram program) {
		InfoSection section = null;
		if (ProgramControlsInfoSection.hasInfo(program)) {
			InfoIcon icon = createInfoIcon("controls-gray32.png", "controls32.png");
			section = new ProgramControlsInfoSection(icon, getTheme(), getLayout(), program);
		}
		return section;
	}

	private InfoSection createProgramAuthoringSection(AmstradProgram program) {
		InfoSection section = null;
		if (ProgramAuthoringInfoSection.hasInfo(program)) {
			InfoIcon icon = createInfoIcon("authoring-gray32.png", "authoring32.png");
			section = new ProgramAuthoringInfoSection(icon, getTheme(), getLayout(), program);
		}
		return section;
	}

	private InfoSection createFolderInfoSection(FolderNode folder) {
		InfoSection section = null;
		if (FolderInfoSection.hasInfo(folder)) {
			InfoIcon icon = createInfoIcon("description-gray32.png", "description32.png");
			section = new FolderInfoSection(icon, getTheme(), getLayout(), folder);
		}
		return section;
	}

	private InfoIcon createInfoIcon(String unselectedImagePath, String selectedImagePath) {
		Image unselectedImage = UIResources.loadIcon("browser/" + unselectedImagePath).getImage();
		Image selectedImage = UIResources.loadIcon("browser/" + selectedImagePath).getImage();
		return createInfoIcon(unselectedImage, selectedImage);
	}

	private InfoIcon createInfoIcon(Image unselectedImage, Image selectedImage) {
		Color unselectedBackground = getTheme().getInfoIconUnselectedBackgroundColor();
		Color selectedBackground = getTheme().getInfoIconSelectedBackgroundColor();
		int fullSize = getLayout().getCaptionBounds().height;
		int reducedSize = Math.round(fullSize * getTheme().getInfoIconUnselectedScale());
		Dimension unselectedSize = new Dimension(reducedSize, reducedSize);
		Dimension selectedSize = new Dimension(fullSize, fullSize);
		return new InfoIcon(unselectedImage, unselectedBackground, unselectedSize, selectedImage, selectedBackground,
				selectedSize);
	}

	public JComponent createCaptionComponent(Node node, List<InfoSection> infoSections) {
		Dimension size = extendHorizontallyAcrossImageShow(getLayout().getCaptionBounds(), node);
		if (node.isProgram()) {
			AmstradProgram program = node.asProgram().getProgram();
			return new ProgramCaptionComponent(size, getTheme(), program, infoSections);
		} else if (node.isFolder()) {
			return new FolderCaptionComponent(size, getTheme(), node.asFolder());
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
					image.disposeImage(); // free up image pool
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
		Color c2 = ColorUtils.setTransparency(c1, 1f);
		GradientFunction function = GradientImageFactory.createSigmoidGradientFunction();
		BufferedImage left = ImageUtils.addPadding(
				GradientImageFactory.createLeftToRightGradientImage(new Dimension(tw, h), c1, c2, function),
				new Insets(0, 0, 0, w - tw), c2);
		BufferedImage bottom = ImageUtils.addPadding(
				GradientImageFactory.createBottomToTopGradientImage(new Dimension(w, th), c1, c2, function),
				new Insets(h - th, 0, 0, 0), c2);
		return ImageUtils.combineByTransparency(left, bottom);
	}

	public CarouselComponent createCarouselComponent(CarouselHost host) {
		Dimension size = getLayout().getCarouselBounds().getSize();
		Insets padding = getLayout().getCarouselPadding();
		CarouselComponent comp = new CarouselComponent(size, padding, getTheme().getBackgroundColor(),
				SlidingCursorMovement.LAZY, getTheme().getCarouselCursorColor(), host, this);
		comp.setLayoutManager(SlidingItemLayoutManagerFactory.createHorizontallySlidingCenterAlignedLayout(comp,
				VerticalAlignment.CENTER));
		comp.setShade(SlidingShadeFactory.createGradientShadeRelativeLength(comp, 0.2));
		comp.setSlidingDynamics(SlidingDynamicsFactory.createAdaptiveSpeedDynamics(comp, 0.004, 1.5, 0.5));
		comp.setSteadyLandingMinimumTimeDelayMillis(100L);
		comp.setRepaintClientDriven(true);
		return comp;
	}

	@Override
	public CarouselEmptyItem createCarouselItemForEmptyFolder(CarouselComponent comp) {
		Dimension size = getFolderCoverImageProducer().getImageSize();
		Insets margin = getLayout().getCarouselItemMargin();
		Font font = getTheme().getCarouselEmptyFolderFont();
		float fontSize = TextLabel.getFontSizeForLineWidth(font, StringUtils.repeat('a', 16), size.width - 20);
		font = font.deriveFont(fontSize);
		CarouselEmptyItem item = new CarouselEmptyItem(comp, size, margin, font);
		item.setColor(getTheme().getCarouselEmptyFolderColor());
		return item;
	}

	@Override
	public CarouselFolderItem createCarouselItemForFolder(FolderNode folderNode, ProgramNode featuredProgramNode,
			CarouselComponent comp) {
		Insets margin = getLayout().getCarouselItemMargin();
		CarouselFolderItem item = new CarouselFolderItem(folderNode, featuredProgramNode, comp,
				getFolderCoverImageProducer(), margin);
		item.preLoad(); // image pre-loading
		return item;
	}

	@Override
	public CarouselProgramItem createCarouselItemForProgram(ProgramNode programNode, CarouselComponent comp) {
		Insets margin = getLayout().getCarouselItemMargin();
		CarouselProgramItem item = new CarouselProgramItem(programNode, comp, getProgramCoverImageProducer(), margin);
		item.preLoad(); // image pre-loading
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

	protected CarouselProgramBrowserTheme getTheme() {
		return theme;
	}

	protected CarouselLayoutManager getLayout() {
		return layout;
	}

	protected AmstradProgramCoverImageProducer getProgramCoverImageProducer() {
		return programCoverImageProducer;
	}

	protected AmstradFolderCoverImageProducer getFolderCoverImageProducer() {
		return folderCoverImageProducer;
	}

}