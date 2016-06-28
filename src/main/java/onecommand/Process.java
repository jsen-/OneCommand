package onecommand;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Process {

    public enum Result {
        COMPLETED,
        TIMEDOUT,
        KILLED;

        int rc;
        String stdout;
        String stderr;

        Result init(int rc, String stdout, String stderr) {
            this.rc = rc;
            this.stdout = stdout;
            this.stderr = stderr;
            return this;
        }

        public int rc() {
            return this.rc;
        }

        public String stdout() {
            return this.stdout;
        }

        public String stderr() {
            return this.stderr;
        }

        static Result completed(int rc, String stdout, String stderr) {
            return Result.COMPLETED.init(rc, stdout, stderr);
        }

        static Result timedout(int rc, String stdout, String stderr) {
            return Result.TIMEDOUT.init(rc, stdout, stderr);
        }

        static Result killed(int rc, String stdout, String stderr) {
            return Result.KILLED.init(rc, stdout, stderr);
        }
    }

    private static Callable createGobbler(InputStream stream) {
        return () -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringWriter sw = new StringWriter();
            char[] buffer = new char[1024 * 4];
            int n;
            while (-1 != (n = br.read(buffer))) {
                sw.write(buffer, 0, n);
            }
            return sw.toString();
        };
    }

    private final java.lang.Process process;
    private Future<String> out = null;
    private Future<String> err = null;

    Process(java.lang.Process process, boolean captureStdout, boolean captureStderr) {
        this.process = process;
        if (captureStdout || captureStderr) {
            ExecutorService pool = Executors.newFixedThreadPool((captureStdout ? 1 : 0) + (captureStderr ? 1 : 0));
            this.out = pool.submit(createGobbler(process.getInputStream()));
            this.err = pool.submit(createGobbler(process.getErrorStream()));
            pool.shutdown();
        }
    }

    public Result waitFor() throws InterruptedException, ExecutionException {
        return Result.completed(this.process.waitFor(),
                this.out == null ? null : this.out.get(),
                this.err == null ? null : this.err.get());
    }

    public Result waitFor(Duration timeout) throws InterruptedException, ExecutionException {
        Result r;
        if (this.process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            r = Result.COMPLETED;
        } else {
            this.process.destroy();
            if (this.process.waitFor(200, TimeUnit.MILLISECONDS)) {
                r = Result.TIMEDOUT;
            } else {
                this.process.destroyForcibly();
                r = Result.KILLED;
            }
        }

        return r.init(this.process.exitValue(),
                this.out == null ? null : this.out.get(),
                this.err == null ? null : this.err.get());
    }
}
