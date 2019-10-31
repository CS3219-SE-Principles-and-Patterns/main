package io.xpire.logic.commands;

import static java.util.Objects.requireNonNull;

import io.xpire.model.Model;
import io.xpire.model.StackManager;
import io.xpire.model.state.State;

/**
 * Undo the previous command.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_UNDO_SUCCESS = "Undo command";
    public static final String MESSAGE_UNDO_FAILURE = "There are no previous commands to undo.";

    @Override
    public CommandResult execute(Model model, StackManager stackManager) {
        requireNonNull(model);
        if (stackManager.isUndoStackEmpty()) {
            return new CommandResult(MESSAGE_UNDO_FAILURE);
        }
        State currentState;
        if (stackManager.isDateSorted()) {
            currentState = new State(model, true, true);
        } else {
            currentState = new State(model);
        }
        State previousState = stackManager.undo(currentState);
        model.update(previousState);
        return new CommandResult(MESSAGE_UNDO_SUCCESS);
    }

    @Override
    public String toString() {
        return "Undo Command";
    }
}