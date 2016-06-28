package onecommand;

public class ProcessResult {

    int rc;
    String stdout;
    String stderr;
    
    public ProcessResult(int rc, String stdout, String stderr) {
        this.rc = rc;
        this.stdout = stdout;
        this.stderr = stderr;
    }
    
}
