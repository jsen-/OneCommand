package onecommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Command {

    public enum Stdio {
        INHERIT(ProcessBuilder.Redirect.INHERIT),
        PIPE(ProcessBuilder.Redirect.PIPE);

        private ProcessBuilder.Redirect value;

        private Stdio(ProcessBuilder.Redirect value) {
            this.value = value;
        }
    }

    final private ProcessBuilder pb = new ProcessBuilder();
    final private List<String> components;

    private Command(String program) {
        this.components = new ArrayList<>();
        this.components.add(program);
    }

    public static Command create(String program) {
        return new Command(program);
    }

    public Command arg(String arg) {
        this.components.add(arg);
        return this;
    }

    public Command env(String name, String value) {
        this.pb.environment().put(name, value);
        return this;
    }

    public Command cwd(File dir) {
        this.pb.directory(dir);
        return this;
    }

    public Command stderr(Stdio type) {
        switch (type) {
            case INHERIT:
                this.pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                break;
            case PIPE:
                this.pb.redirectError(ProcessBuilder.Redirect.PIPE);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return this;
    }

    public Command stdout(Stdio type) {
        switch (type) {
            case INHERIT:
                this.pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                break;
            case PIPE:
                this.pb.redirectError(ProcessBuilder.Redirect.PIPE);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return this;
    }

    public Process start() throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(components);
        return new Process(pb.start(), pb.redirectInput() == ProcessBuilder.Redirect.PIPE, pb.redirectError() == ProcessBuilder.Redirect.PIPE);
    }

}
