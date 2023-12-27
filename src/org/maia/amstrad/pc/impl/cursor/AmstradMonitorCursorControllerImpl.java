package org.maia.amstrad.pc.impl.cursor;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.pc.monitor.cursor.AmstradMonitorCursorController;
import org.maia.util.SystemUtils;

public class AmstradMonitorCursorControllerImpl extends AmstradPcStateAdapter
		implements AmstradMonitorCursorController, MouseMotionListener {

	private AmstradPc amstradPc;

	private boolean autoHideCursor;

	private long autoHideDelayMillis;

	private CursorActivityTracker cursorActivityTracker;

	private long lastCursorActivityTime;

	private long activityMaskEndTime;

	private boolean cursorHidden;

	private Cursor cursorWhenActive;

	private static Cursor BLANK_CURSOR;

	public static long DEFAULT_AUTOHIDE_DELAY_MILLIS = 5000L; // 5 seconds

	public static long STARTUP_ACTIVITY_MASK_DURATION_MILLIS = 1000L; // 1 second

	static {
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
	}

	public AmstradMonitorCursorControllerImpl(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
		this.autoHideDelayMillis = DEFAULT_AUTOHIDE_DELAY_MILLIS;
		init();
	}

	private void init() {
		if (getAmstradPc().isStarted()) {
			amstradPcStarted(getAmstradPc());
		} else {
			getAmstradPc().addStateListener(this);
		}
	}

	@Override
	public synchronized void amstradPcStarted(AmstradPc amstradPc) {
		if (getCursorActivityTracker() == null) {
			setCursorWhenActive(getDisplayComponent().getCursor());
			setActivityMaskEndTime(System.currentTimeMillis() + STARTUP_ACTIVITY_MASK_DURATION_MILLIS);
			getDisplayComponent().addMouseMotionListener(this);
			CursorActivityTracker tracker = new CursorActivityTracker();
			setCursorActivityTracker(tracker);
			tracker.start();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		cursorActive();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		cursorActive();
	}

	private void cursorActive() {
		if (!isActivityMasked()) {
			setLastCursorActivityTime(System.currentTimeMillis());
			if (isCursorHidden()) {
				unhideCursor(false);
			}
		}
	}

	@Override
	public synchronized void hideCursor() {
		if (!isCursorHidden()) {
			setCursorHidden(true);
			setCursorWhenActive(getDisplayComponent().getCursor()); // to restore to when back active
			getDisplayComponent().setCursor(BLANK_CURSOR);
		}
	}

	@Override
	public synchronized void unhideCursor() {
		unhideCursor(true);
	}

	@Override
	public synchronized void setCursor(Cursor cursor) {
		if (!isCursorHidden() && cursor != getCursor()) {
			getDisplayComponent().setCursor(cursor);
		}
		setCursorWhenActive(cursor);
	}

	@Override
	public Cursor getCursor() {
		if (!isCursorHidden()) {
			return getDisplayComponent().getCursor();
		} else {
			return getCursorWhenActive();
		}
	}

	@Override
	public boolean isCursorHidden() {
		return cursorHidden;
	}

	private synchronized void unhideCursor(boolean showAtLeastUntilActive) {
		if (isCursorHidden()) {
			setCursorHidden(false);
			getDisplayComponent().setCursor(getCursorWhenActive());
			if (showAtLeastUntilActive) {
				setLastCursorActivityTime(Long.MAX_VALUE);
			} else {
				setLastCursorActivityTime(System.currentTimeMillis());
			}
		}
	}

	private boolean isActivityMasked() {
		return System.currentTimeMillis() <= getActivityMaskEndTime();
	}

	private JComponent getDisplayComponent() {
		return getAmstradPc().getMonitor().getDisplayComponent();
	}

	private AmstradPc getAmstradPc() {
		return amstradPc;
	}

	@Override
	public boolean isAutoHideCursor() {
		return autoHideCursor;
	}

	@Override
	public void setAutoHideCursor(boolean autoHide) {
		this.autoHideCursor = autoHide;
	}

	public long getAutoHideDelayMillis() {
		return autoHideDelayMillis;
	}

	public void setAutoHideDelayMillis(long delayMillis) {
		this.autoHideDelayMillis = delayMillis;
	}

	private CursorActivityTracker getCursorActivityTracker() {
		return cursorActivityTracker;
	}

	private void setCursorActivityTracker(CursorActivityTracker tracker) {
		this.cursorActivityTracker = tracker;
	}

	private long getLastCursorActivityTime() {
		return lastCursorActivityTime;
	}

	private void setLastCursorActivityTime(long time) {
		this.lastCursorActivityTime = time;
	}

	private long getActivityMaskEndTime() {
		return activityMaskEndTime;
	}

	private void setActivityMaskEndTime(long time) {
		this.activityMaskEndTime = time;
	}

	private void setCursorHidden(boolean hidden) {
		this.cursorHidden = hidden;
	}

	private Cursor getCursorWhenActive() {
		return cursorWhenActive;
	}

	private void setCursorWhenActive(Cursor cursor) {
		this.cursorWhenActive = cursor;
	}

	private class CursorActivityTracker extends Thread {

		public CursorActivityTracker() {
			super("CursorActivityTracker");
			setDaemon(true);
		}

		@Override
		public void run() {
			System.out.println("Cursor activity tracker started");
			while (!getAmstradPc().isTerminated()) {
				if (!isCursorHidden() && isAutoHideCursor() && hasCursorBecomeInactive()) {
					autoHideCursor();
				}
				SystemUtils.sleep(getAutoHideDelayMillis() / 2);
			}
			System.out.println("Cursor activity tracker stopped");
		}

		private boolean hasCursorBecomeInactive() {
			if (getLastCursorActivityTime() == Long.MAX_VALUE) {
				// hold until new cursor activity
				return false;
			} else {
				return System.currentTimeMillis() > getLastCursorActivityTime() + getAutoHideDelayMillis();
			}
		}

		private void autoHideCursor() {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					hideCursor();
				}
			});
		}

	}

}