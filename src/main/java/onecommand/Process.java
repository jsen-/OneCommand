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

    ProcessResult waitFor() throws InterruptedException, ExecutionException {
        return new ProcessResult(this.process.waitFor(),
                this.out == null ? null : this.out.get(),
                this.err == null ? null : this.err.get());
    }

    ProcessResult waitFor(Duration timeout) throws InterruptedException, ExecutionException {
        if (!this.process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            // the process did not yet terminate
            this.process.destroyForcibly();
        }

        ProcessResult res = new ProcessResult(this.process.exitValue(),
                this.out == null ? null : this.out.get(),
                this.err == null ? null : this.err.get());
        return res;
    }
}
