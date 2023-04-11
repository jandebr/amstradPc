package org.maia.amstrad;

public class AmstradMain {

	public static void main(String[] args) throws Exception {
		AmstradFactory.getInstance().getAmstradContext().getMode().launch(args);
	}

}