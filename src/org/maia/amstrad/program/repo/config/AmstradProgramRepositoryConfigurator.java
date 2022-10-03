package org.maia.amstrad.program.repo.config;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

public class AmstradProgramRepositoryConfigurator extends Box {

	private AmstradProgramRepositoryConfiguration state;

	private FileFolderInputField rootFolderField;

	private JCheckBox hideSequenceNumbersOption;

	private JCheckBox searchByProgramNameOption;

	private JTextField searchStringField;

	private JCheckBox facetedOption;

	private CardsInOrderPanel facetCardsInOrderPanel;

	private CardsOfChoicePanel facetCardsOfChoicePanel;

	private AmstradProgramRepositoryConfigurator(AmstradProgramRepositoryConfiguration state) {
		super(BoxLayout.Y_AXIS);
		this.state = state;
		this.rootFolderField = createRootFolderField();
		this.hideSequenceNumbersOption = createHideSequenceNumbersOption();
		this.searchByProgramNameOption = createSearchByProgramNameOption();
		this.searchStringField = createSearchStringField();
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
		add(createSearchComponent());
		add(Box.createVerticalStrut(16));
		add(createFacetsComponent());
	}

	private JComponent createGeneralComponent() {
		Box comp = new Box(BoxLayout.Y_AXIS);
		comp.setAlignmentX(LEFT_ALIGNMENT);
		comp.setBorder(BorderFactory.createTitledBorder("General"));
		comp.add(createRootFolderComponent());
		comp.add(Box.createVerticalStrut(4));
		comp.add(getHideSequenceNumbersOption());
		comp.add(Box.createVerticalStrut(4));
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

	private JComponent createSearchComponent() {
		Box comp = new Box(BoxLayout.Y_AXIS);
		comp.setAlignmentX(LEFT_ALIGNMENT);
		comp.setBorder(BorderFactory.createTitledBorder("Search"));
		comp.add(getSearchByProgramNameOption());
		comp.add(createSearchFieldComponent());
		comp.add(Box.createVerticalStrut(8));
		return comp;
	}

	private JComponent createSearchFieldComponent() {
		Box comp = new Box(BoxLayout.X_AXIS);
		comp.setAlignmentX(LEFT_ALIGNMENT);
		JLabel label = new JLabel("Contains:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));
		comp.add(label);
		comp.add(getSearchStringField());
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

	private JCheckBox createHideSequenceNumbersOption() {
		JCheckBox option = new JCheckBox("Hide sequence numbers", getState().isHideSequenceNumbers());
		option.setAlignmentX(LEFT_ALIGNMENT);
		option.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				getState().setHideSequenceNumbers(getHideSequenceNumbersOption().isSelected());
			}
		});
		return option;
	}

	private JCheckBox createSearchByProgramNameOption() {
		JCheckBox option = new JCheckBox("Search by program name", getState().isSearchByProgramName());
		option.setAlignmentX(LEFT_ALIGNMENT);
		option.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				boolean selected = getSearchByProgramNameOption().isSelected();
				getState().setSearchByProgramName(selected);
				getSearchStringField().setEditable(selected);
			}
		});
		return option;
	}

	private JTextField createSearchStringField() {
		JTextField field = new JTextField(getState().getSearchString(), 20);
		field.setAlignmentX(LEFT_ALIGNMENT);
		field.setEditable(getState().isSearchByProgramName());
		field.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				textChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				textChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				textChanged(e);
			}

			private void textChanged(DocumentEvent e) {
				getState().setSearchString(getSearchStringField().getText().trim());
			}
		});
		return field;
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
		panel.setCardsMinimumWidth(90);
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
			orderPanel.addCard(new FacetCard(facets.getFacet(i)));
		}
		return panel;
	}

	private List<Card> createAllFacetCards() {
		List<Card> cards = new Vector<Card>();
		for (Facet facet : FacetFactory.getInstance().getAllFacets()) {
			cards.add(new FacetCard(facet));
		}
		return cards;
	}

	public AmstradProgramRepositoryConfiguration getState() {
		return state;
	}

	private FileFolderInputField getRootFolderField() {
		return rootFolderField;
	}

	private JCheckBox getHideSequenceNumbersOption() {
		return hideSequenceNumbersOption;
	}

	private JCheckBox getSearchByProgramNameOption() {
		return searchByProgramNameOption;
	}

	private JTextField getSearchStringField() {
		return searchStringField;
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

		public FacetCard(Facet facet) {
			super(facet.getLabel(), facet.getIcon(), Color.WHITE);
			this.facet = facet;
		}

		public Facet getFacet() {
			return facet;
		}

	}

}