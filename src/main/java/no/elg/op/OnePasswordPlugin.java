/*
 * Copyright (c) 2022 Elg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package no.elg.op;

import com.google.inject.Provides;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "1Password Login")
public class OnePasswordPlugin extends Plugin {

  public static final int ENTER_CREDENTIALS_FORM_INDEX = 2;
  public static final int ENTER_OTP_FORM_INDEX = 4;

  @Inject
  private Client client;

  @Inject
  private OnePasswordService onePasswordService;
  @Inject
  private ClientThread clientThread;

  @Nullable
  private JDialog enteringCredentialsDialog;

  private boolean triedCredentials;
  private boolean triedOTP;

  private void tried() {
    triedCredentials = false;
    triedOTP = false;
  }

  @Override
  protected void startUp() {
    tried();
  }

  public void tryLogIn(GameState gameState) {
    switch (gameState) {
      case LOGIN_SCREEN:
        if (client.getLoginIndex() == ENTER_CREDENTIALS_FORM_INDEX && !triedCredentials) {
          triedCredentials = true;
          onePasswordService.enterUsernameAndPassword();
        }
        break;
      case LOGIN_SCREEN_AUTHENTICATOR:
        if (client.getLoginIndex() == ENTER_OTP_FORM_INDEX && !triedOTP) {
          triedOTP = true;
          onePasswordService.enterOTP();
        }
        break;
      default:
        //Falls through
    }
  }

  @Subscribe
  public void onBeforeRender(BeforeRender clientTick) {
    tryLogIn(client.getGameState());
  }

  @Subscribe
  public void onGameStateChanged(GameStateChanged gameStateChanged) {
    tried();
  }

  @Subscribe
  public void onConfigChanged(ConfigChanged configChanged) {
    if (OnePasswordConfig.GROUP.equals(configChanged.getGroup())) {
      tried();
    }
  }

  public void showWarning(String title, String message) {
    closeEnteringCredentialsDialog();
    SwingUtilities.invokeLater(
        () -> JOptionPane.showMessageDialog(client.getCanvas(), message, title,
            JOptionPane.WARNING_MESSAGE));
  }

  public void showEnteringCredentialsDialog() {
    SwingUtilities.invokeLater(
        () -> {
          closeEnteringCredentialsDialog(enteringCredentialsDialog);
          JOptionPane pane = new JOptionPane();
          pane.setMessage(
              "The 1Password login plugin is entering your credentials for you, please wait.\nA prompt will appear for you to enter your Windows Hello");
          pane.setMessageType(JOptionPane.PLAIN_MESSAGE);
          enteringCredentialsDialog = pane.createDialog(client.getCanvas(),
              "Waiting for 1Password CLI response");
          enteringCredentialsDialog.setVisible(true);
        });
  }

  public void closeEnteringCredentialsDialog() {
    closeEnteringCredentialsDialog(enteringCredentialsDialog);
  }

  private void closeEnteringCredentialsDialog(final JDialog enteringCredentialsDialog) {
    if (enteringCredentialsDialog != null) {
      SwingUtilities.invokeLater(enteringCredentialsDialog::dispose);
    }
  }

  /* @ThreadSafe */
  public void enterUsernameAndPassword(String username, String password) {
    clientThread.invoke(() -> {
      log.debug("Entering credentials for the user");
      client.setUsername(username);
      client.setPassword(password);
      closeEnteringCredentialsDialog();
    });
  }

  /* @ThreadSafe */
  public void enterOTP(String otp) {
    clientThread.invoke(() -> {
      log.debug("Entering OTP for the user");
      client.setOtp(otp);
      closeEnteringCredentialsDialog();
    });
  }

  @Provides
  OnePasswordConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(OnePasswordConfig.class);
  }
}
