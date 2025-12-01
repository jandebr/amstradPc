package org.maia.amstrad.program.browser.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.browser.AmstradProgramBrowserStyle;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.facet.Facet;
import org.maia.amstrad.program.repo.facet.FacetFactory;
import org.maia.amstrad.program.repo.facet.FacetList;
import org.maia.swing.cards.CardsInOrderListener;
import org.maia.swing.cards.CardsInOrderPanel;
import org.maia.swing.cards.CardsInOrderPanel.Card;
import org.maia.swing.cards.CardsOfChoicePanel;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.input.FolderInputField;
import org.maia.swing.input.GenericFileInputField;
import org.maia.swing.input.GenericFileInputFieldListener;

public class AmstradProgramBrowserConfigurator extends JTabbedPane {

	private AmstradProgramBrowserConfiguration state;

	private JComboBox<String> styleSelectorField;

	private FolderInputField rootFolderField;

	private JCheckBox hideSequenceNumbersOption;

	private JCheckBox searchByProgramNameOption;

	private JTextField searchStringField;

	private JCheckBox facetedOption;

	private CardsInOrderPanel facetCardsInOrderPanel;

	private CardsOfChoicePanel facetCardsOfChoicePanel;

	private AmstradProgramBrowserConfigurator(AmstradProgramBrowserConfiguration state) {
		this.state = state;
		this.styleSelectorField = createStyleSelectorField();
		this.rootFolderField = createRootFolderField();
		this.hideSequenceNumbersOption = createHideSequenceNumbersOption();
		this.searchByProgramNameOption = createSearchByProgramNameOption();
		this.searchStringField = createSearchStringField();
		this.facetedOption = createFacetedOption();
		this.facetCardsInOrderPanel = createFacetCardsInOrderPanel();
		this.facetCardsOfChoicePanel = createFacetCardsOfChoicePanel(getFacetCardsInOrderPanel());
		buildUI();
	}

	public static ActionableDialog createDialog(AmstradPcFrame frame, AmstradProgramBrowserConfiguration state) {
		AmstradProgramBrowserConfigurator cfg = new AmstradProgramBrowserConfigurator(state);
		ActionableDialog dialog = ActionableDialog.createOkCancelModalDialog(frame, "Program browser setup", cfg);
		cfg.getFacetCardsInOrderPanel().setPanelMinimumWidth(cfg.getFacetCardsOfChoicePanel().getWidth());
		return dialog;
	}

	private void buildUI() {
		addTab("General", createGeneralTabComponent());
		addTab("Search", createSearchTabComponent());
		addTab("Facets", createFacetsTabComponent());
	}

	private JComponent createGeneralTabComponent() {
		Box box = new Box(BoxLayout.Y_AXIS);
		box.setAlignmentX(LEFT_ALIGNMENT);
		box.add(createStyleSelectorComponent());
		box.add(Box.createVerticalStrut(12));
		box.add(createRootFolderComponent());
		box.add(Box.createVerticalStrut(4));
		box.add(getHideSequenceNumbersOption());
		return createTabComponent(box);
	}

	private JComponent createStyleSelectorComponent() {
		Box box = new Box(BoxLayout.X_AXIS);
		box.setAlignmentX(LEFT_ALIGNMENT);
		JLabel label = new JLabel("Browser style:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));
		box.add(label);
		box.add(getStyleSelectorField());
		return box;
	}

	private JComponent createRootFolderComponent() {
		Box box = new Box(BoxLayout.X_AXIS);
		box.setAlignmentX(LEFT_ALIGNMENT);
		JLabel label = new JLabel("Home folder:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));
		box.add(label);
		box.add(getRootFolderField());
		return box;
	}

	private JComponent createSearchTabComponent() {
		Box box = new Box(BoxLayout.Y_AXIS);
		box.setAlignmentX(LEFT_ALIGNMENT);
		box.add(getSearchByProgramNameOption());
		box.add(Box.createVerticalStrut(8));
		box.add(createSearchFieldComponent());
		return createTabComponent(box);
	}

	private JComponent createSearchFieldComponent() {
		Box box = new Box(BoxLayout.X_AXIS);
		box.setAlignmentX(LEFT_ALIGNMENT);
		JLabel label = new JLabel("Contains:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));
		box.add(label);
		box.add(getSearchStringField());
		return box;
	}

	private JComponent createFacetsTabComponent() {
		Box box = new Box(BoxLayout.Y_AXIS);
		box.setAlignmentX(LEFT_ALIGNMENT);
		box.add(getFacetedOption());
		box.add(Box.createVerticalStrut(4));
		box.add(getFacetCardsOfChoicePanel());
		box.add(getFacetCardsInOrderPanel());
		return createTabComponent(box);
	}

	private JComponent createTabComponent(JComponent contentPane) {
		contentPane.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
		JComponent comp = new JPanel(new BorderLayout());
		comp.add(contentPane, BorderLayout.NORTH);
		return comp;
	}

	private JComboBox<String> createStyleSelectorField() {
		JComboBox<String> field = new JComboBox<String>();
		for (AmstradProgramBrowserStyle style : AmstradProgramBrowserStyle.values()) {
			field.addItem(style.getDisplayName());
		}
		field.setSelectedItem(getState().getStyle().getDisplayName());
		field.setEditable(false);
		field.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("comboBoxChanged")) {
					String selectedStyle = field.getSelectedItem().toString();
					getState().setStyle(AmstradProgramBrowserStyle.forDisplayNameIgnoreCase(selectedStyle));
				}
			}
		});
		return field;
	}

	private FolderInputField createRootFolderField() {
		FolderInputField field = new FolderInputField(getRepositoryState().getRootFolder());
		field.setShowAbsolutePath(true);
		field.setFileChooserDialogTitle("Select the home folder of programs");
		field.addListener(new GenericFileInputFieldListener() {

			@Override
			public void fileSelectionChanged(GenericFileInputField inputField) {
				getRepositoryState().setRootFolder(inputField.getFile());
			}
		});
		field.disableClear();
		return field;
	}

	private JCheckBox createHideSequenceNumbersOption() {
		JCheckBox option = new JCheckBox("Hide sequence numbers", getRepositoryState().isHideSequenceNumbers());
		option.setAlignmentX(LEFT_ALIGNMENT);
		option.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				getRepositoryState().setHideSequenceNumbers(getHideSequenceNumbersOption().isSelected());
			}
		});
		return option;
	}

	private JCheckBox createSearchByProgramNameOption() {
		JCheckBox option = new JCheckBox("Search by program name", getRepositoryState().isSearchByProgramName());
		option.setAlignmentX(LEFT_ALIGNMENT);
		option.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				boolean selected = getSearchByProgramNameOption().isSelected();
				getRepositoryState().setSearchByProgramName(selected);
				getSearchStringField().setEditable(selected);
			}
		});
		return option;
	}

	private JTextField createSearchStringField() {
		JTextField field = new JTextField(getRepositoryState().getSearchString(), 20);
		field.setAlignmentX(LEFT_ALIGNMENT);
		field.setEditable(getRepositoryState().isSearchByProgramName());
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
				getRepositoryState().setSearchString(getSearchStringField().getText().trim());
			}
		});
		return field;
	}

	private JCheckBox createFacetedOption() {
		JCheckBox option = new JCheckBox("Browse by facets", isFaceted());
		option.setAlignmentX(LEFT_ALIGNMENT);
		option.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				setFaceted(getFacetedOption().isSelected());
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
		panel.setEnabled(isFaceted());
		panel.addListener(new CardsInOrderListener() {

			@Override
			public void cardsRearrangedInPanel(CardsInOrderPanel panel) {
			}

			@Override
			public void cardsChangedInPanel(CardsInOrderPanel panel) {
				getRepositoryState().setFacets(getFacetList(panel));
			}

			@Override
			public void cardRemovedFromPanel(CardsInOrderPanel panel, Card card) {
				if (panel.isEmpty()) {
					setFaceted(false);
				}
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
		panel.setEnabled(isFaceted());
		FacetList facets = getRepositoryState().getFacets();
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

	private boolean isFaceted() {
		return getRepositoryState().isFaceted();
	}

	private void setFaceted(boolean faceted) {
		getFacetedOption().setSelected(faceted);
		getRepositoryState().setFaceted(faceted);
		getFacetCardsOfChoicePanel().setEnabled(faceted);
		getFacetCardsInOrderPanel().setEnabled(faceted);
	}

	public AmstradProgramRepositoryConfiguration getRepositoryState() {
		return getState().getRepositoryConfiguration();
	}

	public AmstradProgramBrowserConfiguration getState() {
		return state;
	}

	private JComboBox<String> getStyleSelectorField() {
		return styleSelectorField;
	}

	private FolderInputField getRootFolderField() {
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