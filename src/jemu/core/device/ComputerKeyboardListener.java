package jemu.core.device;

public interface ComputerKeyboardListener {

	void computerPressEscapeKey(Computer computer);

	void computerSuppressEscapeKey(Computer computer);

	void computerAutotypeStarted(Computer computer);

	void computerAutotypeEnded(Computer computer);

}