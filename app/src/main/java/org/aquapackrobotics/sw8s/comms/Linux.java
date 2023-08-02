package org.aquapackrobotics.sw8s.comms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

class CommandResult {
    public int ec;
    public String stdout;
    public String stderr;
}

public class Linux {
    public static CommandResult runShellCommand(String command) throws IOException, InterruptedException {
        CommandResult res = new CommandResult();
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(command);
        proc.waitFor();
        res.ec = proc.exitValue();
        BufferedReader stdoutStream = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stderrStream = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        res.stdout = stdoutStream.lines().collect(Collectors.joining("\n"));
        res.stderr = stderrStream.lines().collect(Collectors.joining("\n"));
        return res;
    }
}
