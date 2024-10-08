package jemu.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author       Richard Wilson
 * @version 1.0
 */

/**
 * This class provides the background processing required to implement timed
 * Events using the Counter class.
 *
 * @author Richard Wilson
 * @version 1.0
 */
public class Timer extends Component implements Runnable {

  protected Vector events = new Vector(1);

  /**
   * The Timer instance.
   */
  private static Timer timer = new Timer();

  /**
   * The current Timer Thread.
   */
  protected Thread timerThread = null;

  /**
   * A Vector containing all Counter instances currently running.
   */
  protected Vector counters = new Vector(1);

  /**
   * Constructs a Timer.
   */
  private Timer() {
    enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
  }

  @SuppressWarnings("unchecked")
public synchronized void post(UserEvent event) {
    events.addElement(event);
    setVisible(!isVisible());
  }

  /**
   * Adds a Counter to the Timer.
   *
   * @param counter The Counter to be added
   */
  @SuppressWarnings("unchecked")
  protected static void addCounter(Counter counter) {
    synchronized(timer) {
      timer.counters.addElement(counter);
      timer.checkThread();
    }
  }

  /**
   * Removes a Counter from the Timer.
   *
   * @param counter The Counter to be removed
   */
  protected static void removeCounter(Counter counter) {
    synchronized(timer) {
      timer.counters.removeElement(counter);
    }
  }

  /**
   * Ensures that the Timer Thread is running.
   */
  protected void checkThread() {
    if (timerThread == null) {
      timerThread = new Thread(this);
      timerThread.start();
    }
  }

  /**
   * Processes an AWTEvent (A timed Event), passing the notification on to the
   * TimerListener for the Counter.
   *
   * @param e The AWTEvent (UserEvent)
   */
  public void processEvent(AWTEvent e) {
    UserEvent event = null;
    synchronized(this) {
      if (events.size() > 0) {
        event = (UserEvent)events.firstElement();
        events.removeElementAt(0);
      }
    }
    if (event != null) {
      Counter counter = (Counter)event.getData();
      counter.listener.timerTick(counter);
    }
  }

  /**
   * Implements the run() method of the Runnable instance for the Timer Thread.
   * Cycles through the list of Counters, calling their tick method
   * periodically.
   */
  public void run() {
    int count;
    do {
      synchronized(this) {
        count = counters.size();
        if (count == 0)
          timerThread = null;
        else {
          long time = System.currentTimeMillis();
          for (int i = 0; i < count; i++)
            ((Counter)counters.elementAt(i)).tick(this, time);
        }
        try {
          wait(10);
        } catch (Exception e) { }
      }
    } while (count != 0);
  }

}