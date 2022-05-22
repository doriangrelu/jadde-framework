package fr.jadde.test.fmk.app.mock.services;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bean.annotation.Inject;
import fr.jadde.fmk.app.executor.bean.annotation.Start;
import fr.jadde.fmk.app.executor.bean.annotation.Stop;
import fr.jadde.fmk.container.annotation.JaddeBean;

import java.util.List;

/**
 * @author Dorian GRELU
 */
@JaddeBean
public class MySecondService implements MyServiceInterface {

    @Inject
    private MyFirstService myFirstService;

    @Inject
    private JaddeApplicationContext applicationContext;

    @Inject
    private List<MyFirstService> myFirstServices;

    private String name = null;

    @Start
    public void start() {
        this.name = "Dorian";
    }

    @Stop
    public void stop() {
        System.out.println("STOP !");
    }

    public String name() {
        return this.name;
    }

    public MyFirstService myFirstService() {
        return myFirstService;
    }

    public JaddeApplicationContext applicationContext() {
        return this.applicationContext;
    }

    public List<MyFirstService> myFirstServices() {
        return this.myFirstServices;
    }
}