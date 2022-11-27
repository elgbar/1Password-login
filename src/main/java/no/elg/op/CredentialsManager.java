package no.elg.op;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Synchronized;

@Singleton
public class CredentialsManager {

  private String username;
  private String password;

  @Inject
  private OnePasswordPlugin plugin;

  @Inject
  private OnePasswordConfig config;

  @Synchronized
  public void reset() {
    this.username = null;
    this.password = null;
  }

  @Synchronized
  public void set(String username, String password) {
    if (config.storePasswordInSession()) {
      this.username = username;
      this.password = password;
    } else {
      reset();
    }
  }

  @Synchronized
  public boolean enterUsernameAndPassword() {
    if (username == null || password == null) {
      return false;
    }
    if (config.storePasswordInSession()) {
      plugin.enterUsernameAndPassword(username, password);
      return true;
    } else {
      reset();
      return false;
    }
  }
}
