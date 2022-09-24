package org.maia.amstrad.program.repo.config;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.repo.facet.Facet;
import org.maia.amstrad.program.repo.facet.FacetFactory;
import org.maia.amstrad.program.repo.facet.FacetList;
import org.maia.swing.cards.CardsInOrderListener;
import org.maia.swing.cards.CardsInOrderPanel;
import org.maia.swing.cards.CardsInOrderPanel.Card;
import org.maia.swing.cards.CardsOfChoicePanel;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.file.FileFolderInputField;
import org.maia.swing.file.FileFolderInputFieldListener;
import org.maia.swing.util.SwingUtils;

public class AmstradProgramRepositoryConfigurator extends Box {

	private AmstradProgramRepositoryConfiguration state;

	private FileFolderInputField rootFolderField;

	private JCheckBox sequenceNumberFilterOption;

	private JCheckBox facetedOption;

	private CardsInOrderPanel facetCardsInOrderPanel;

	private CardsOfChoicePanel facetCardsOfChoicePanel;

	private AmstradProgramRepositoryConfigurator(AmstradProgramRepositoryConfiguration state) {
		super(BoxLayout.Y_AXIS);
		this.state = state;
		this.rootFolderField = createRootFolderField();
		this.sequenceNumberFilterOption = createSequenceNumberFilterOption();
		this.facetedOption = createFacetedOption();
		this.facetCardsInOrderPanel = createFacetCardsInOrderPanel();
		this.facetCardsOfChoicePanel = createFacetCardsOfChoicePanel(getFacetCardsInOrderPanel());
		buildUI();
	}

	public static ActionableDialog createDialog(AmstradPcFrame frame, AmstradProgramRepositoryConfiguration state) {
		AmstradProgramRepositoryConfigurator cfg = new AmstradProgramRepositoryConfigurator(state);
		ActionableDialog dialog = ActionableDialog.createOkCancelModalDialog(frame, "Program browser setup", cfg);
		cfg.getFacetCardsInOrderPanel().setPanelMinimumWidth(cfg.getFacetCardsOfChoicePanel().getWidth());
		return dialog;
	}

	private void buildUI() {
		add(createGeneralComponent());
		add(Box.createVerticalStrut(16));
		add(createFacetsComponent());
	}

	private JComponent createGeneralComponent() {
		Box comp = new Box(BoxLayout.Y_AXIS);
		comp.setAlignmentX(LEFT_ALIGNMENT);
		comp.setBorder(BorderFactory.createTitledBorder("General"));
		comp.add(createRootFolderComponent());
		comp.add(Box.createVerticalStrut(4));
		comp.add(getSequenceNumberFilterOption());
		return comp;
	}

	private JComponent createRootFolderComponent() {
		Box comp = new Box(BoxLayout.X_AXIS);
		comp.setAlignmentX(LEFT_ALIGNMENT);
		JLabel label = new JLabel("Home folder:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));
		comp.add(label);
		comp.add(getRootFolderField());
		return comp;
	}

	private JComponent createFacetsComponent() {
		Box comp = new Box(BoxLayout.Y_AXIS);
		comp.setAlignmentX(LEFT_ALIGNMENT);
		comp.setBorder(BorderFactory.createTitledBorder("Facets"));
		comp.add(getFacetedOption());
		comp.add(getFacetCardsOfChoicePanel());
		comp.add(getFacetCardsInOrderPanel());
		return comp;
	}

	private FileFolderInputField createRootFolderField() {
		FileFolderInputField field = new FileFolderInputField(getState().getRootFolder());
		field.setFolderChooserDialogTitle("Select the home folder of programs");
		field.addListener(new FileFolderInputFieldListener() {

			@Override
			public void folderChanged(FileFolderInputField field) {
				getState().setRootFolder(field.getFolder());
			}
		});
		return field;
	}

	private JCheckBox createSequenceNumberFilterOption() {
		JCheckBox option = new JCheckBox("Hide sequence numbers", getState().isSequenceNumberFiltered());
		option.setAlignmentX(LEFT_ALIGNMENT);
		option.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				getState().setSequenceNumberFiltered(getSequenceNumberFilterOption().isSelected());
			}
		});
		return option;
	}

	private JCheckBox createFacetedOption() {
		JCheckBox option = new JCheckBox("Browse by facets", getState().isFaceted());
		option.setAlignmentX(LEFT_ALIGNMENT);
		option.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				boolean selected = getFacetedOption().isSelected();
				getState().setFaceted(selected);
				getFacetCardsOfChoicePanel().setEnabled(selected);
				getFacetCardsInOrderPanel().setEnabled(selected);
			}
		});
		return option;
	}

	private CardsInOrderPanel createFacetCardsInOrderPanel() {
		CardsInOrderPanel panel = new CardsInOrderPanel(400, 100);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		panel.setEmptyMessage("Click facets to add and reorder by dragging");
		panel.setEnabled(getState().isFaceted());
		panel.addListener(new CardsInOrderListener() {

			@Override
			public void cardsRearrangedInPanel(CardsInOrderPanel panel) {
			}

			@Override
			public void cardsChangedInPanel(CardsInOrderPanel panel) {
				getState().setFacets(getFacetList(panel));
			}

			@Override
			public void cardRemovedFromPanel(CardsInOrderPanel panel, Card card) {
			}

			@Override
			public void cardAddedToPanel(CardsInOrderPanel panel, Card card) {
			}

			private FacetList getFacetList(CardsInOrderPanel panel) {
				FacetList list = new FacetList();
				for (Card card : panel.getCardsInOrder()) {
					list.add(((FacetCard) card).getFacet());
				}
				return list;
			}

		});
		return panel;
	}

	private CardsOfChoicePanel createFacetCardsOfChoicePanel(CardsInOrderPanel orderPanel) {
		CardsOfChoicePanel panel = new CardsOfChoicePanel(createAllFacetCards(), orderPanel);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		panel.setEnabled(getState().isFaceted());
		FacetList facets = getState().getFacets();
		for (int i = 0; i < facets.size(); i++) {
			orderPanel.addCard(createFacetCard(facets.getFacet(i)));
		}
		return panel;
	}

	private List<Card> createAllFacetCards() {
		List<Card> cards = new Vector<Card>();
		for (Facet facet : FacetFactory.getInstance().getAllFacets()) {
			cards.add(createFacetCard(facet));
		}
		return cards;
	}

	private Card createFacetCard(Facet facet) {
		Icon icon = SwingUtils.getIcon("edit32.png");
		return new FacetCard(facet, icon);
	}

	public AmstradProgramRepositoryConfiguration getState() {
		return state;
	}

	private FileFolderInputField getRootFolderField() {
		return rootFolderField;
	}

	private JCheckBox getSequenceNumberFilterOption() {
		return sequenceNumberFilterOption;
	}

	private JCheckBox getFacetedOption() {
		return facetedOption;
	}

	private CardsInOrderPanel getFacetCardsInOrderPanel() {
		return facetCardsInOrderPanel;
	}

	private CardsOfChoicePanel getFacetCardsOfChoicePanel() {
		return facetCardsOfChoicePanel;
	}

	private static class FacetCard extends Card {

		private Facet facet;

		public FacetCard(Facet facet, Icon icon) {
			super(facet.getLabel(), icon, Color.WHITE);
			this.facet = facet;
		}

		public Facet getFacet() {
			return facet;
		}

	}

}