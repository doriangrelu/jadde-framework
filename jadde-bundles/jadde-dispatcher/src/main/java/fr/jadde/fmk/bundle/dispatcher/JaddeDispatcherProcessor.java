package fr.jadde.fmk.bundle.dispatcher;

import fr.jadde.fmk.app.executor.bean.api.AbstractJaddeBeanProcessor;
import fr.jadde.fmk.bundle.dispatcher.annotation.command.ConsumeCommand;
import fr.jadde.fmk.bundle.dispatcher.annotation.command.CommandConsumer;
import fr.jadde.fmk.bundle.dispatcher.annotation.event.ConsumeEvent;
import fr.jadde.fmk.bundle.dispatcher.annotation.event.EventConsumer;
import fr.jadde.fmk.bundle.dispatcher.service.impl.command.JaddeCommandHandler;
import fr.jadde.fmk.bundle.dispatcher.service.impl.event.JaddeEventHandler;

import java.util.stream.Stream;

public class JaddeDispatcherProcessor extends AbstractJaddeBeanProcessor {
    @Override
    public void process(Object target) {
        if (target.getClass().isAnnotationPresent(EventConsumer.class)) {
            this.processEvent(target);
        }
        if (target.getClass().isAnnotationPresent(CommandConsumer.class)) {
            this.processCommand(target);
        }
    }

    private void processEvent(final Object target) {
        Stream.of(target.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(ConsumeEvent.class))
                .forEach(method -> JaddeEventHandler.handle(this.context(), target, method, method.getAnnotation(ConsumeEvent.class).value()));
    }

    private void processCommand(final Object target) {
        Stream.of(target.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(ConsumeCommand.class))
                .forEach(method -> JaddeCommandHandler.handle(this.context(), target, method, method.getAnnotation(ConsumeCommand.class).value()));
    }

    @Override
    public boolean doesSupport(Object target) {
        return null != target && (target.getClass().isAnnotationPresent(EventConsumer.class) || target.getClass().isAnnotationPresent(CommandConsumer.class));
    }

}
