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

import static no.elg.op.OnePasswordConfig.GROUP;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(GROUP)
public interface OnePasswordConfig extends Config {

  String GROUP = "1password";

  String CLI_PATH_CONFIG = "cliPath";
  String STORE_CREDENTIALS_IN_MEMORY_KEY = "inMemoryCredentials";
  String OP_ACCOUNT_PATH_CONFIG = "opAccount";
  String OP_OSRS_ITEM_PATH_CONFIG = "opItem";
  String OP_OSRS_ITEM_USERNAME_FIELD_PATH_CONFIG = "opFieldUsername";
  String OP_OSRS_ITEM_PASSWORD_FIELD_PATH_CONFIG = "opFieldPassword";

  @ConfigItem(
      keyName = CLI_PATH_CONFIG,
      name = "1Password CLI Path",
      position = 0,
      description = "Path to the 1Password CLI"
  )
  default String cliPath() {
    return "op";
  }


  @ConfigItem(
      keyName = OP_OSRS_ITEM_USERNAME_FIELD_PATH_CONFIG,
      name = "Username field name",
      position = 1,
      description = "Select the account to execute the command by account shorthand, sign-in address, account ID, or user ID."
  )
  default String opUsernameField() {
    return "username";
  }

  @ConfigItem(
      keyName = OP_OSRS_ITEM_PASSWORD_FIELD_PATH_CONFIG,
      name = "Password field name",
      position = 2,
      description = "Select the account to execute the command by account shorthand, sign-in address, account ID, or user ID."
  )
  default String opPasswordField() {
    return "password";
  }

  @ConfigItem(
      keyName = STORE_CREDENTIALS_IN_MEMORY_KEY,
      name = "Store credentials in-memory",
      position = 3,
      description = "Store credentials in-memory, reducing the time to login in within the same session."
  )
  default boolean storePasswordInSession() {
    return false;
  }

  @ConfigItem(
      keyName = OP_ACCOUNT_PATH_CONFIG,
      name = "1Password Account",
      secret = true,
      position = 101,
      description = "Select the account to execute the command by account shorthand, sign-in address, account ID, or user ID."
  )
  default String opAccount() {
    return "";
  }

  @ConfigItem(
      keyName = OP_OSRS_ITEM_PATH_CONFIG,
      name = "Runescape account item",
      secret = true,
      position = 102,
      description = "Select the account to execute the command by account shorthand, sign-in address, account ID, or user ID."
  )
  default String opOSRSVaultItem() {
      return "";
  }
}
