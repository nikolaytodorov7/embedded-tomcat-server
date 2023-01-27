package tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.cli.*;

public class TomcatLauncher {
    private Tomcat tomcat;
    private int serverPort;
    private String numberOfThreads;
    private String baseDir;
    private String contextPath = "/";
    private TomcatActiveOptions activeOptions;
    private CommandLine commandLine;

    public TomcatLauncher(String[] args) throws ParseException {
        parseOptions(args);
        setOptions(commandLine, activeOptions);
        tomcat = new Tomcat();
        tomcat.setPort(serverPort);
        tomcat.getConnector().setProperty("maxThreads", numberOfThreads);
        tomcat.getHost().setAppBase(baseDir);
        tomcat.addWebapp(tomcat.getHost(), contextPath, "src\\main\\webapp");
    }

    public void start() throws LifecycleException {
        tomcat.start();
        tomcat.getServer().await();
    }

    private void parseOptions(String[] args) throws ParseException {
        Options options = TomcatOptions.getOptions();
        CommandLineParser parser = new DefaultParser();
        commandLine = parser.parse(options, args);
        activeOptions = new TomcatActiveOptions(commandLine);
    }

    private void setOptions(CommandLine commandLine, TomcatActiveOptions activeOptions) {
        if (!activeOptions.dirOption)
            throw new IllegalArgumentException("Must provide base dir!");

        baseDir = commandLine.getOptionValue("d");
        if (activeOptions.portOption)
            setPort(commandLine);

        if (activeOptions.threadsOption)
            setThreads(commandLine);

        if (activeOptions.contextOption)
            contextPath = commandLine.getOptionValue("c");
    }

    private void setThreads(CommandLine commandLine) {
        String strThreads = commandLine.getOptionValue("t");
        try {
            Integer.parseInt(strThreads);
            numberOfThreads = strThreads;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Possible threads must be a number!");
        }
    }

    private void setPort(CommandLine commandLine) {
        String strPort = commandLine.getOptionValue("p");
        try {
            serverPort = Integer.parseInt(strPort);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port must be a number!");
        }
    }
}
