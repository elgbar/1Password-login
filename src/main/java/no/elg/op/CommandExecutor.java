package no.elg.op;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.util.OSType;

@Singleton
@Slf4j
public class CommandExecutor {


  @Inject
  private OnePasswordPlugin plugin;

  @Inject
  private OnePasswordConfig config;

  public void executeCommand(Consumer</*@Nonnull*/ String> consumer, String... arguments) {
    ProcessBuilder pb = new ProcessBuilder();
    pb.redirectErrorStream(true);
    Map<String, String> environment = pb.environment();
    String opAccount = config.opAccount();
    environment.put("OP_ACCOUNT", opAccount);
    environment.put("OP_ISO_TIMESTAMPS", "");
    switch (OSType.getOSType()) {
      case Linux:
      case MacOS:
        pb.command("bash", "-c");
        break;
      case Windows:
        pb.command("cmd", "/c");
        break;
      case Other:
        plugin.showWarning("Unknown OS",
            "This plugin is not compatible with your operating system, sorry!");
        return;
    }
    List<String> commands = pb.command();
    commands.add(config.cliPath());
    commands.add("--no-color");
    commands.addAll(Arrays.asList(arguments));
    log.info("Cmd to be executed: " + commands);
    Process process;
    try {
      process = pb.start();
    } catch (IOException e) {
      plugin.showWarning("Failed to execute",
          "Failed to start executing command due to an exception: " + e.getClass().getSimpleName());
      return;
    }

    try {
      process.waitFor(7, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      plugin.showWarning("Execution timed out",
          "The command took too long to execute!");
      return;
    }
    try (InputStream is = process.getInputStream()) {
      //noinspection UnstableApiUsage
      consumer.accept(new String(ByteStreams.toByteArray(is)));
    } catch (IOException e) {
      plugin.showWarning("Execution read failed",
          "Failed to read the result of the execution");
    }
  }

}
