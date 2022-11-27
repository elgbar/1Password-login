package no.elg.op;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

@Singleton
@Slf4j
public class OnePasswordService {

  @Inject
  private OnePasswordPlugin plugin;

  @Inject
  private OnePasswordConfig config;
  @Inject
  private CommandExecutor commandExecutor;
  @Inject
  private Client client;
  @Inject
  private Gson gson;
  @Inject
  private CredentialsManager credentialsManager;

  private final Executor threadExecutor = Executors.newSingleThreadExecutor();

  public static final String CMD_UNKNOWN_CMD = "is not recognized as an internal or external command";
  public static final String BASH_UNKNOWN_CMD = "command not found";
  public static final String OP_CLI_NOT_LOGGED_IN = "Sign in to an account to get started.";
  public static final String OP_CLI_AUTH_DISMISSED = "authorization prompt dismissed, please try again";
  public static final String OP_CLI_VAULT_NOT_UNLOCKED = "biometrics currently not available. Please open 1Password, unlock it with your password and then try again";
  public static final String OP_CLI_UNKNOWN_FIELD = "isn't a field in the";
  public static final String OP_CLI_UNKNOWN_ITEM = "isn't an item. Specify the item with its UUID, name, or domain.";
  public static final String OP_CLI_INTERNAL_ERROR = "runtime stack:";
  public static final String OP_CLI_INTERNAL_ERROR_2 = "An operation was attempted on something that is not a socket.";
  public static final String OP_CLI_INIT_ERROR = "error initializing client:";

  private static final Type FIELDS_TYPE = new TypeToken<List<Map<String, Object>>>() {
  }.getType();

  private final AtomicBoolean cmdRunning = new AtomicBoolean(false);

  private final Consumer<String> usernameAndPasswordSetter = stringJson -> {
    if (invalidMessage(stringJson)) {
      return;
    }
    List<Map<String, Object>> json;
    try {
      json = gson.fromJson(stringJson, FIELDS_TYPE);
    } catch (JsonIOException | JsonSyntaxException e) {
      plugin.showWarning("Invalid JSON",
          "Failed to parse returned json, please check your configuration");
      log.info("json: " + stringJson);
      return;
    }

    String username = null;
    String password = null;

    for (Map<String, Object> map : json) {
      if (config.opUsernameField().equals(map.get("id"))) {
        username = map.get("value").toString();
      } else if (config.opPasswordField().equals(map.get("id"))) {
        password = map.get("value").toString();
      }
    }
    if (username == null) {
      plugin.showWarning("Invalid 1Password field",
          "Failed to find the 1Password field for your OSRS username");
      return;
    }
    if (password == null) {
      plugin.showWarning("Invalid 1Password field",
          "Failed to find the 1Password field for your OSRS password");
      return;
    }
    if (config.storePasswordInSession()) {
      credentialsManager.set(username, password);
    }
    plugin.enterUsernameAndPassword(username, password);
  };

  private final Consumer<String> otpSetter = otp -> {
    if (invalidMessage(otp)) {
      return;
    }
    if (otp.length() == 6) {
      client.setOtp(otp);
    }
  };

  private boolean invalidMessage(String message) {
    log.info(message);
    if (message.contains(CMD_UNKNOWN_CMD) || message.contains(BASH_UNKNOWN_CMD)) {
      plugin.showWarning("Invalid op CLI path",
          "Failed to find an executable at the given path to the 1Password cli. Check the plugins settings");
      return true;

    } else if (message.contains(OP_CLI_VAULT_NOT_UNLOCKED)) {
      plugin.showWarning("1Password not unlocked",
          "Your 1Password vault has not been unlocked yet, please login and restart the plugin");
      return true;

    } else if (message.contains(OP_CLI_UNKNOWN_FIELD)) {
      plugin.showWarning("Unknown field",
          "Invalid field set in config: " + message.substring(message.indexOf('"')));
      return true;

    } else if (message.contains(OP_CLI_AUTH_DISMISSED)) {
      log.info("User dismissed/failed authentication, do nothing");
      return true;
    } else if (message.contains(OP_CLI_UNKNOWN_ITEM)) {
      plugin.showWarning("Unknown item",
          "Invalid runescape account item in config: " + message.substring(message.indexOf('"'))
              + " You can find the item id by executing the command 'op item ls' in bash/cmd.");
      return true;
    } else if (message.contains(OP_CLI_INTERNAL_ERROR) || message.contains(
        OP_CLI_INTERNAL_ERROR_2)) {
      plugin.showWarning("1Password Internal Error",
          "1Password cli threw an internal error, please restart the plugin");
      return true;
    } else if (message.contains(OP_CLI_INIT_ERROR)) {
      plugin.showWarning("1Password Initialization Error",
          message.substring(message.lastIndexOf(':')));
      return true;
    } else if (message.contains(OP_CLI_NOT_LOGGED_IN)) {
      plugin.showWarning("Not signed into 1Password",
          "You are not signed into the 1Password CLI. Please login and specify your account id");
      return true;
    }
    return false;
  }

  @Synchronized
  public void enterUsernameAndPassword() {
    boolean entered = credentialsManager.enterUsernameAndPassword();
    if (!entered && !cmdRunning.get()) {
      cmdRunning.set(true);
      threadExecutor.execute(() -> {
            commandExecutor.executeCommand(
                usernameAndPasswordSetter,
                "item",
                "get",
                config.opOSRSVaultItem(),
                "--field", config.opUsernameField(),
                "--field", config.opPasswordField(),
                "--format", "json"
            );
            cmdRunning.set(false);
          }
      );
    }
  }

  public void enterOTP() {
    threadExecutor.execute(() -> commandExecutor.executeCommand(
        otpSetter,
        "item",
        "get",
        config.opOSRSVaultItem(),
        "--otp"
    ));
  }
}
