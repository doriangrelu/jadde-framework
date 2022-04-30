package fr.jadde.test.fmk.bundle.dispatcher.mock.services;

import fr.jadde.fmk.bundle.dispatcher.annotation.command.ConsumeCommand;
import fr.jadde.fmk.bundle.dispatcher.annotation.command.CommandConsumer;
import fr.jadde.fmk.bundle.dispatcher.annotation.event.ConsumeEvent;
import fr.jadde.fmk.bundle.dispatcher.annotation.event.EventConsumer;
import fr.jadde.fmk.bundle.dispatcher.api.payload.Command;
import fr.jadde.fmk.bundle.dispatcher.api.payload.Event;

@EventConsumer
@CommandConsumer
public class FirstFakeHandler {

    public static String message = null;

    @ConsumeEvent(value = "myEvent")
    public void onMyEvent(final Event<String> event) {
        message = event.body();
    }

    @ConsumeCommand("myCommand")
    public void onMyCommand(final Command<String> command) {
        command.complete(command.body() + "Command!");
    }


    @ConsumeCommand("myCommandError")
    public void onMyCommandError(final Command<String> command) {
        command.fail(new IllegalStateException());
    }

}
