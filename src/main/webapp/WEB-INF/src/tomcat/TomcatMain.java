package tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.commons.cli.ParseException;

public class TomcatMain {
    public static void main(String[] args) throws ParseException, LifecycleException {
        TomcatLauncher launcher = new TomcatLauncher(args);
        launcher.start();
    }
}
