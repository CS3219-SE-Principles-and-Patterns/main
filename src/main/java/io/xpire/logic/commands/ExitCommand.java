package io.xpire.logic.commands;

import io.xpire.model.Model;

import static io.xpire.logic.commands.CommandType.XPIRE;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting Xpire as requested ...";

    public final CommandType commandType = XPIRE;

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true);
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
}
