package fr.jadde.test.fmk.bundle.dispatcher.mock.services;

import fr.jadde.fmk.bundle.dispatcher.annotation.event.ConsumeEvent;
import fr.jadde.fmk.bundle.dispatcher.annotation.event.EventConsumer;
import fr.jadde.fmk.bundle.dispatcher.api.payload.Event;

@EventConsumer
public class SecondFakeHandler {

    public static String message = null;

    @ConsumeEvent(value = "myEvent")
    public void onMyEvent(final Event<String> event) {
        message = event.body();
    }

}
