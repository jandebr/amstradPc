package org.maia.amstrad.pc;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.menu.AmstradMenuBar;
import org.maia.amstrad.pc.menu.maker.AmstradMenuBarMaker;
import org.maia.amstrad.pc.menu.maker.AmstradMenuDefaultLookAndFeel;

public class AmstradPcFrameTest {

	public AmstradPcFrameTest() {
	}

	public static void main(String[] args) {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		frame.addFrameListener(new AmstradPcFrameListener() {

			@Override
			public void amstradPcFrameClosed(AmstradPcFrame frame) {
				System.out.println("CLOSED");
			}
		});
		amstradPc.start();
		frame.setTitle("Frame test");
		new TestMenuBarMaker(amstradPc).createMenuBar().install();
	}

	private static class TestMenuBarMaker extends AmstradMenuBarMaker {

		public TestMenuBarMaker(AmstradPc amstradPc) {
			super(amstradPc, new AmstradMenuDefaultLookAndFeel());
		}

		@Override
		protected AmstradMenuBar doCreateMenu() {
			AmstradMenuBar menuBar = new AmstradMenuBar(getAmstradPc());
			menuBar.add(createFileMenu());
			menuBar.add(createEmulatorMenu());
			menuBar.add(createMonitorMenu());
			menuBar.add(createWindowMenu());
			return updateMenuBarLookAndFeel(menuBar);
		}

	}

}