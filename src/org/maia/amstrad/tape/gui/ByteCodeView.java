package org.maia.amstrad.tape.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.maia.amstrad.tape.model.ByteCodeRange;
import org.maia.amstrad.tape.model.ByteSequence;

@SuppressWarnings("serial")
public class ByteCodeView extends JPanel {

	private ByteSequence byteCode;

	private int[] byteCodeOffsets; // JTextPane document offsets by bytecode index

	private StyleContext styleContext;

	private JTextPane byteCodePane;

	private ByteCodeRange byteCodeSelection;

	private static final String NAMED_STYLE_BYTECODE = "byteCode";

	private static final String NAMED_STYLE_BYTECODE_SELECTED = "byteCodeSelected";

	private static Color BYTE_CODE_BACKGROUND = new Color(22, 8, 0);

	public ByteCodeView(ByteSequence byteCode) {
		super(new BorderLayout());
		this.byteCode = byteCode;
		this.styleContext = createStyleContext();
		this.byteCodeOffsets = new int[byteCode.getLength() + 1];
		buildView();
		setPreferredSize(UIResourcesTape.byteCodeViewSize);
	}

	private StyleContext createStyleContext() {
		StyleContext ctx = new StyleContext();
		installByteCodeStyle(ctx);
		installByteCodeSelectedStyle(ctx);
		return ctx;
	}

	private void installByteCodeStyle(StyleContext ctx) {
		Style style = ctx.addStyle(NAMED_STYLE_BYTECODE, null);
		StyleConstants.setForeground(style, new Color(188, 177, 171));
		StyleConstants.setFontFamily(style, Font.MONOSPACED);
		StyleConstants.setBold(style, true);
		StyleConstants.setFontSize(style, 12);
	}

	private void installByteCodeSelectedStyle(StyleContext ctx) {
		Style style = ctx.addStyle(NAMED_STYLE_BYTECODE_SELECTED, ctx.getStyle(NAMED_STYLE_BYTECODE));
		StyleConstants.setBackground(style, new Color(255, 250, 96));
		StyleConstants.setForeground(style, Color.BLACK);
	}

	private void buildView() {
		add(buildByteCodePane(), BorderLayout.CENTER);
	}

	private JComponent buildByteCodePane() {
		JTextPane pane = new JTextPane(new DefaultStyledDocument(getStyleContext()));
		pane.setBackground(BYTE_CODE_BACKGROUND);
		pane.setBorder(new LineBorder(pane.getBackground(), 8));
		Document doc = pane.getDocument();
		Style style = pane.getStyle(NAMED_STYLE_BYTECODE);
		try {
			doc.insertString(doc.getLength(), getByteCode().toHumanReadableString(120, null, this.byteCodeOffsets),
					style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		pane.setEditable(false);
		pane.setCaretPosition(0);
		this.byteCodePane = pane;
		JScrollPane scrollPane = new JScrollPane(pane);
		return scrollPane;
	}

	public void selectByteCode(ByteCodeRange range) {
		clearByteCodeSelection();
		if (range != null) {
			setByteCodeSelection(range);
			applyStyle(range, NAMED_STYLE_BYTECODE_SELECTED);
		}
	}

	public void clearByteCodeSelection() {
		if (getByteCodeSelection() != null) {
			applyStyle(getByteCodeSelection(), NAMED_STYLE_BYTECODE);
			setByteCodeSelection(null);
		}
	}

	public void scrollToVisibleSelection() {
		if (getByteCodeSelection() != null) {
			try {
				int offset = mapDocumentOffset(getByteCodeSelection().getByteCodeOffset());
				Rectangle2D r = getByteCodePane().modelToView2D(offset);
				getByteCodePane().scrollRectToVisible(r.getBounds());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	private void applyStyle(ByteCodeRange range, String styleName) {
		StyledDocument doc = getByteCodePane().getStyledDocument();
		int p0 = mapDocumentOffset(range.getByteCodeOffset());
		int p1 = mapDocumentOffset(range.getByteCodeEnd() + 1);
		doc.setCharacterAttributes(p0, p1 - p0, doc.getStyle(styleName), true);
	}

	private int mapDocumentOffset(int byteCodeIndex) {
		return byteCodeOffsets[byteCodeIndex];
	}

	public ByteSequence getByteCode() {
		return byteCode;
	}

	public ByteCodeRange getByteCodeSelection() {
		return byteCodeSelection;
	}

	private void setByteCodeSelection(ByteCodeRange byteCodeSelection) {
		this.byteCodeSelection = byteCodeSelection;
	}

	private StyleContext getStyleContext() {
		return styleContext;
	}

	private JTextPane getByteCodePane() {
		return byteCodePane;
	}

}