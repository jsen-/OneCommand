package tests;

import org.junit.Before;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import static org.junit.Assert.*;

import onecommand.Command;
import onecommand.Process;
import onecommand.Process.Result;

public class HostnameTest {

    private Command command;
    private Process process;
    private Result result;

    @Before
    public void setUp() throws IOException, InterruptedException, ExecutionException {
        this.command = Command.create("hostname")
                .stderr(Command.Stdio.PIPE)
                .stdout(Command.Stdio.PIPE);
        this.process = this.command.start();
        this.result = this.process.waitFor();
    }

    @Test
    public void timeout1() throws IOException, InterruptedException, ExecutionException {
        assertEquals(this.result.rc(), 0);
    }

    @Test
    public void timeout2() throws IOException, InterruptedException, ExecutionException {
        assertNotEquals(this.result.stdout(), "");
    }

    @Test
    public void timeout3() throws IOException, InterruptedException, ExecutionException {
        assertEquals(this.result.stderr(), "");
    }
}
