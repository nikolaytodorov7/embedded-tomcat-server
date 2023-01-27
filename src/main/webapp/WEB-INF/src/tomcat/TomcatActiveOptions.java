package tomcat;

import org.apache.commons.cli.CommandLine;

public class TomcatActiveOptions {
    boolean portOption;
    boolean threadsOption;
    boolean dirOption;
    boolean contextOption;

    public TomcatActiveOptions(CommandLine commandLine) {
        portOption = commandLine.hasOption(TomcatOptions.PORT);
        threadsOption = commandLine.hasOption(TomcatOptions.NUMBER_OF_THREADS);
        dirOption = commandLine.hasOption(TomcatOptions.BASE_DIR);
        contextOption = commandLine.hasOption(TomcatOptions.CONTEXT_PATH);
    }

}
