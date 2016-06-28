# OneCommand
Easy creation and timed termination of processes capturing stdout and stderr

```java
ProcessResult ping = Command.create("ping")
        .arg("localhost")
        .arg("-n")
        .arg("99")
        .stdout(Stdio.PIPE)
        .stderr(Stdio.PIPE)
        .start();

System.out.println(ping.rc + " " + ping.stdout + " " + ping.stderr);
```
