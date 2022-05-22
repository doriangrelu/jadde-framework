package fr.jadde.test.fmk.app.mock.services;

import fr.jadde.fmk.container.annotation.JaddeBean;
import fr.jadde.fmk.container.annotation.Qualifier;

@Qualifier("myQualifier")
@JaddeBean
public class MyThirdService implements MyServiceInterface {
}
