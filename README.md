# OneCommand
Easy creation and timed termination of processes capturing stdout and stderr

```java
ProcessResult ping = Command.create("ping")
        .arg("localhost")
        .arg("-n")
        .arg("99")
        .start()
        .waitFor(Duration.ofSeconds(3));

System.out.println(ping.rc + " " + ping.stdout + " " + ping.stderr);
```
