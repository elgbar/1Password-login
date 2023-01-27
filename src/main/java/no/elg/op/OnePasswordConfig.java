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
import net.runelite.client.config.ConfigSection;

@ConfigGroup(GROUP)
public interface OnePasswordConfig extends Config {

  String GROUP = "1password";

  @ConfigSection(
      name = "Item Fields Names",
      description = "The name of fields within your runescape vault item. You probably don't need to change these.",
      closedByDefault = true,
      position = 10
  )
  String ITEM_FIELDS_SECTION = "1password-item-fields";
  @ConfigSection(
      name = "1Password CLI",
      description = "1Password CLI related settings",
      closedByDefault = true,
      position = 20
  )
  String CLI_SECTION = "1password-cli";

  String CLI_PATH_CONFIG = "cliPath";
  String OP_CACHE_CONFIG = "enableOPCache";
  String OP_ACCOUNT_PATH_CONFIG = "opAccount";
  String OP_OSRS_ITEM_PATH_CONFIG = "opItem";
  String OP_OSRS_ITEM_USERNAME_FIELD_PATH_CONFIG = "opFieldUsername";
  String OP_OSRS_ITEM_PASSWORD_FIELD_PATH_CONFIG = "opFieldPassword";

  @ConfigItem(
      keyName = OP_ACCOUNT_PATH_CONFIG,
      name = "1Password User ID",
      secret = true,
      position = 0,
      description = "The 1Password account to execute the command by account shorthand, sign-in address, account ID, or user ID."
  )
  default String opAccount() {
    return "";
  }

  @ConfigItem(
      keyName = OP_OSRS_ITEM_PATH_CONFIG,
      name = "1Password RuneScape Item",
      secret = true,
      position = 1,
      description = "The item ID of your OSRS item. Find it by executing 'op item list'"
  )
  default String opOSRSVaultItem() {
    return "";
  }

  ////////////////////////////////////////////////////

  @ConfigItem(
      keyName = CLI_PATH_CONFIG,
      name = "1Password CLI Path",
      position = 0,
      section = CLI_SECTION,
      description = "Path to the 1Password CLI"
  )
  default String cliPath() {
    return "op";
  }

  @ConfigItem(
      keyName = OP_CACHE_CONFIG,
      name = "Enable 1Password CLI Cache",
      position = 2,
      section = CLI_SECTION,
      description =
          "1Password CLI can use its daemon process to cache items, vault information, and the keys to access information in an account."
              + "<p>This helps maximize performance and reduce the number of API calls."
              + "<p>The daemon stores encrypted information in memory using the same encryption methods as on 1Password.com. It can read the information to pass to 1Password CLI, but can’t decrypt it."
              + "<p>"
              + "<p>Caching is not currently available on Windows."
  )
  default boolean enableOPCache() {
    return true;
  }

  ////////////////////////////////////////////////////

  @ConfigItem(
      keyName = OP_OSRS_ITEM_USERNAME_FIELD_PATH_CONFIG,
      name = "Username Field Name",
      section = ITEM_FIELDS_SECTION,
      position = 1,
      description = "The field name of the username/email section of the vault item"
  )
  default String opUsernameField() {
    return "username";
  }

  @ConfigItem(
      keyName = OP_OSRS_ITEM_PASSWORD_FIELD_PATH_CONFIG,
      name = "Password Field Name",
      section = ITEM_FIELDS_SECTION,
      position = 2,
      description = "The field name of the username/email section of the vault item"
  )
  default String opPasswordField() {
    return "password";
  }
}
