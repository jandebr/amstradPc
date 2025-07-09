package org.maia.amstrad.pc.impl.joystick;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;

import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.io.inputdevice.InputDeviceFilter;
import org.maia.io.inputdevice.controller.InputCommand;
import org.maia.io.inputdevice.controller.InputCommandGroup;
import org.maia.io.inputdevice.controller.InputControllerException;
import org.maia.io.inputdevice.controller.InputControllerType;
import org.maia.io.inputdevice.controller.config.InputControllerByConfigurationBuilder;
import org.maia.io.inputdevice.controller.config.InputControllerConfiguration;
import org.maia.io.inputdevice.controller.config.InputControllerConfigurationByPropertiesBuilder;
import org.maia.io.inputdevice.controller.config.ia.InteractiveBuilder;
import org.maia.io.inputdevice.controller.config.ia.InteractiveBuilderControlType;
import org.maia.io.inputdevice.controller.config.ia.InteractiveBuilderControls;
import org.maia.io.inputdevice.controller.config.ia.JInteractiveBuilder;
import org.maia.io.inputdevice.controller.config.ia.JInteractiveBuilderListener;
import org.maia.io.inputdevice.controller.config.ia.RequiredInputCommands;
import org.maia.io.inputdevice.joystick.Joystick;
import org.maia.io.inputdevice.joystick.JoystickCommand;

public class AmstradJoystickDeviceConfigurator implements JInteractiveBuilderListener {

	private AmstradJoystickDevice joystickDevice;

	private InteractiveBuilder interactiveBuilder;

	private boolean joystickActiveBeforeInteractiveSetup;

	private static Map<AmstradJoystickCommand, JoystickCommand> joystickCommandMap = new HashMap<AmstradJoystickCommand, JoystickCommand>();

	public AmstradJoystickDeviceConfigurator(AmstradJoystickDevice joystickDevice) {
		this.joystickDevice = joystickDevice;
		this.interactiveBuilder = createBuilder();
		renewJoystick();
	}

	protected InteractiveBuilder createBuilder() {
		final Set<JoystickCommand> requiredCommands = getRequiredCommands();
		InteractiveBuilder builder = new InteractiveBuilder(createCommandGroup(), InputDeviceFilter.STICK_OR_GAMEPAD);
		builder.withControllerType(InputControllerType.JOYSTICK).withControllerName(getJoystickName())
				.withControls(new InteractiveBuilderControls() {

					@Override
					public InteractiveBuilderControlType getType(InputCommand command) {
						String commandId = command.getIdentifier();
						if (AmstradJoystickCommand.UP.getIdentifier().equals(commandId)) {
							return InteractiveBuilderControlType.PREVIOUS;
						} else if (AmstradJoystickCommand.DOWN.getIdentifier().equals(commandId)) {
							return InteractiveBuilderControlType.NEXT;
						} else if (AmstradJoystickCommand.FIRE2.getIdentifier().equals(commandId)) {
							return InteractiveBuilderControlType.SUBMIT;
						} else {
							// fyi, no mapping for InteractiveBuilderControlType.CLEAR
							return null;
						}
					}

				}).withRequiredCommands(new RequiredInputCommands() {

					@Override
					public boolean isRequired(InputCommand command) {
						return requiredCommands.contains(command);
					}
				});
		return builder;
	}

	protected InputCommandGroup createCommandGroup() {
		InputCommandGroup group = new InputCommandGroup("amstrad");
		group.addMember(getJoystickCommand(AmstradJoystickCommand.FIRE2));
		group.addMember(getJoystickCommand(AmstradJoystickCommand.FIRE1));
		group.addMember(getJoystickCommand(AmstradJoystickCommand.UP));
		group.addMember(getJoystickCommand(AmstradJoystickCommand.DOWN));
		group.addMember(getJoystickCommand(AmstradJoystickCommand.LEFT));
		group.addMember(getJoystickCommand(AmstradJoystickCommand.RIGHT));
		group.addMember(getJoystickCommand(AmstradJoystickCommand.MENU));
		group.addMember(getJoystickCommand(AmstradJoystickCommand.KEYBOARD));
		return group;
	}

	protected Set<JoystickCommand> getRequiredCommands() {
		Set<JoystickCommand> commands = new HashSet<JoystickCommand>();
		commands.add(getJoystickCommand(AmstradJoystickCommand.UP));
		commands.add(getJoystickCommand(AmstradJoystickCommand.DOWN));
		commands.add(getJoystickCommand(AmstradJoystickCommand.LEFT));
		commands.add(getJoystickCommand(AmstradJoystickCommand.RIGHT));
		commands.add(getJoystickCommand(AmstradJoystickCommand.FIRE2));
		return commands;
	}

	public void showSetupDialog() {
		JFrame frame = getJoystickDevice().getAmstradPc().getFrame();
		setJoystickActiveBeforeInteractiveSetup(getJoystickDevice().isActive());
		getJoystickDevice().deactivate();
		getInteractiveBuilder().resetTo(loadPersistedConfiguration());
		JInteractiveBuilder jbuilder = new JInteractiveBuilder(getInteractiveBuilder(), frame,
				"Setup " + getJoystickName());
		jbuilder.setShowCommandGroupNames(false);
		jbuilder.addListener(this);
		jbuilder.show();
	}

	@Override
	public void interactiveBuilderCompleted(JInteractiveBuilder jbuilder) {
		InputControllerConfiguration configuration = jbuilder.getConfiguration();
		persistConfiguration(configuration);
		renewJoystick(configuration);
		getJoystickDevice().activateAfter(500L); // suppress noisy events
	}

	@Override
	public void interactiveBuilderCancelled(JInteractiveBuilder jbuilder) {
		getJoystickDevice().switchActiveState(isJoystickActiveBeforeInteractiveSetup());
	}

	private void renewJoystick() {
		renewJoystick(loadPersistedConfiguration());
	}

	private synchronized void renewJoystick(InputControllerConfiguration configuration) {
		if (configuration != null) {
			getJoystickDevice().disposeJoystickDelegate();
			Joystick joystick = createJoystick(configuration);
			if (joystick != null) {
				getJoystickDevice().installJoystickDelegate(joystick);
			}
		}
	}

	private Joystick createJoystick(InputControllerConfiguration configuration) {
		Joystick joystick = null;
		if (configuration != null) {
			try {
				joystick = new InputControllerByConfigurationBuilder(configuration).buildJoystick();
			} catch (InputControllerException e) {
				// input device may no longer be connected
			}
		}
		return joystick;
	}

	private InputControllerConfiguration loadPersistedConfiguration() {
		InputControllerConfiguration cfg = null;
		File file = getPersistedPropertiesFile();
		String keyPrefix = getPersistedPropertiesKeyPrefix();
		try {
			Properties props = InputControllerConfigurationByPropertiesBuilder.readPropertiesFromFile(file);
			cfg = new InputControllerConfigurationByPropertiesBuilder(props, keyPrefix).build();
		} catch (IOException e) {
			// file may not exist
		}
		return cfg;
	}

	private void persistConfiguration(InputControllerConfiguration configuration) {
		File file = getPersistedPropertiesFile();
		String keyPrefix = getPersistedPropertiesKeyPrefix();
		Properties props = new Properties();
		new InputControllerConfigurationByPropertiesBuilder(props, keyPrefix).loadIntoProperties(configuration);
		try {
			InputControllerConfigurationByPropertiesBuilder.writePropertiesToFile(file, props, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static JoystickCommand getJoystickCommand(AmstradJoystickCommand command) {
		JoystickCommand joyc = joystickCommandMap.get(command);
		if (joyc == null) {
			joyc = createJoystickCommand(command);
			joystickCommandMap.put(command, joyc);
		}
		return joyc;
	}

	private static JoystickCommand createJoystickCommand(AmstradJoystickCommand command) {
		JoystickCommand joyc = new JoystickCommand(command.getDisplayName(), command.getIdentifier());
		joyc.setDescription(command.getDescription());
		return joyc;
	}

	protected File getPersistedPropertiesFile() {
		return new File(getJoystickIdentifier() + ".ini");
	}

	protected String getPersistedPropertiesKeyPrefix() {
		return null;
	}

	private String getJoystickIdentifier() {
		return getJoystickDevice().getJoystickId().getIdentifier();
	}

	private String getJoystickName() {
		return getJoystickDevice().getJoystickId().getDisplayName();
	}

	public AmstradJoystickDevice getJoystickDevice() {
		return joystickDevice;
	}

	protected InteractiveBuilder getInteractiveBuilder() {
		return interactiveBuilder;
	}

	private boolean isJoystickActiveBeforeInteractiveSetup() {
		return joystickActiveBeforeInteractiveSetup;
	}

	private void setJoystickActiveBeforeInteractiveSetup(boolean active) {
		this.joystickActiveBeforeInteractiveSetup = active;
	}

}