package org.maia.amstrad.tape.gui;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.maia.amstrad.tape.model.AudioRange;
import org.maia.amstrad.tape.model.AudioTapeIndex;
import org.maia.amstrad.tape.model.AudioTapeProgram;
import org.maia.amstrad.tape.model.profile.TapeProfile;

@SuppressWarnings("serial")
public class AudioTapeIndexView extends JPanel implements ListSelectionListener {

	private AudioTapeIndex tapeIndex;

	private JTable table;

	private List<IndexSelectionListener> selectionListeners;

	public AudioTapeIndexView(AudioTapeIndex tapeIndex) {
		super(new BorderLayout());
		this.tapeIndex = tapeIndex;
		this.table = buildTable();
		this.selectionListeners = new Vector<IndexSelectionListener>();
		add(buildIndexPane(), BorderLayout.CENTER);
	}

	private JTable buildTable() {
		JTable table = new IndexTable();
		table.getSelectionModel().addListSelectionListener(this);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		return table;
	}

	private JComponent buildIndexPane() {
		JScrollPane scrollPane = new JScrollPane(getTable());
		return scrollPane;
	}

	public void addSelectionListener(IndexSelectionListener listener) {
		getSelectionListeners().add(listener);
	}

	public void removeSelectionListener(IndexSelectionListener listener) {
		getSelectionListeners().remove(listener);
	}

	public void clearSelection() {
		getTable().clearSelection();
	}

	public void changeSelection(AudioTapeProgram program) {
		int i = getRowForProgram(program);
		if (i >= 0) {
			getTable().setRowSelectionInterval(i, i);
			getTable().scrollRectToVisible(getTable().getCellRect(i, 0, true));
		} else {
			clearSelection();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			for (IndexSelectionListener listener : getSelectionListeners()) {
				listener.indexSelectionUpdate(this);
			}
		}
	}

	public AudioTapeProgram getSelectedProgram() {
		AudioTapeProgram program = null;
		if (getTable() != null) {
			int row = getTable().getSelectedRow();
			if (row >= 0) {
				program = getProgramForRow(row);
			}
		}
		return program;
	}

	private AudioTapeProgram getProgramForRow(int row) {
		return getTapeIndex().getPrograms().get(row);
	}

	private int getRowForProgram(AudioTapeProgram program) {
		return getTapeIndex().getPrograms().indexOf(program);
	}

	public AudioTapeIndex getTapeIndex() {
		return tapeIndex;
	}

	private JTable getTable() {
		return table;
	}

	private List<IndexSelectionListener> getSelectionListeners() {
		return selectionListeners;
	}

	public static interface IndexSelectionListener {

		void indexSelectionUpdate(AudioTapeIndexView source);

	}

	private class IndexTable extends JTable {

		public IndexTable() {
			super(new IndexTableModel());
			setupColumnWidths();
			setAutoResizeMode(AUTO_RESIZE_OFF);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		private void setupColumnWidths() {
			TableColumnModel col = getColumnModel();
			col.getColumn(0).setMinWidth(20);
			col.getColumn(0).setMaxWidth(20);
			col.getColumn(1).setMaxWidth(40);
			col.getColumn(2).setMinWidth(180);
		}

	}

	private class IndexTableModel extends AbstractTableModel {

		public IndexTableModel() {
		}

		@Override
		public int getColumnCount() {
			return 7;
		}

		@Override
		public int getRowCount() {
			return getTapeIndex().size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			AudioTapeProgram program = getProgramForRow(row);
			if (col == 0) {
				value = program.hasModifiedSourceCode() ? UIResourcesTape.pencilIcon : UIResourcesTape.tapeIcon;
			} else if (col == 1) {
				value = row + 1;
			} else if (col == 2) {
				value = program.getProgramName();
			} else if (col == 3) {
				value = program.getNumberOfBlocks();
			} else if (col == 4) {
				value = program.getSourceCodeOnTape().getLineCount();
			} else if (col == 5 || col == 6) {
				TapeProfile programProfile = program.getProfileOnTape();
				if (programProfile != null) {
					AudioRange range = programProfile.getAudioRange();
					long samplePosition = col == 5 ? range.getSampleOffset() : range.getSampleEnd();
					int sampleRate = program.getAudioFile().getSampleRate();
					value = UIResourcesTape.formatTimeOfAudioSamplePosition(samplePosition, sampleRate, false);
				}
			}
			return value;
		}

		@Override
		public String getColumnName(int col) {
			if (col == 0) {
				return "";
			} else if (col == 1) {
				return "Nr.";
			} else if (col == 2) {
				return "Name";
			} else if (col == 3) {
				return "Blocks";
			} else if (col == 4) {
				return "Code lines";
			} else if (col == 5) {
				return "Tape from";
			} else if (col == 6) {
				return "Tape until";
			} else {
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int col) {
			if (col == 0) {
				return Icon.class;
			} else if (col == 1) {
				return Integer.class;
			} else if (col == 2) {
				return String.class;
			} else if (col == 3) {
				return Integer.class;
			} else if (col == 4) {
				return Integer.class;
			} else if (col == 5) {
				return Long.class;
			} else if (col == 6) {
				return Long.class;
			} else {
				return null;
			}
		}

	}

}