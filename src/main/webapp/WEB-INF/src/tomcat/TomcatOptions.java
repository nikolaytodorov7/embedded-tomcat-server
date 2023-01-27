package tomcat;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class TomcatOptions {
    static final Option NUMBER_OF_THREADS = Option.builder("t").hasArg(true).build();
    static final Option PORT = Option.builder("p").hasArg(true).build();
    static final Option BASE_DIR = Option.builder("d").hasArg(true).build();
    static final Option CONTEXT_PATH = Option.builder("c").hasArg(true).build();

    static Options getOptions() {
        return new Options()
                .addOption(NUMBER_OF_THREADS)
                .addOption(PORT)
                .addOption(CONTEXT_PATH)
                .addOption(BASE_DIR);
    }
}
