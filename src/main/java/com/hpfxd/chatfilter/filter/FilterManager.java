/*
 * Copyright (c) 2022 hpfxd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.hpfxd.chatfilter.filter;

import com.google.inject.Inject;
import com.hpfxd.chatfilter.api.AsyncChatFilterEvent;
import com.hpfxd.chatfilter.config.ChatFilterConfig;
import com.hpfxd.natelib.config.language.BukkitLanguageKey;
import com.hpfxd.natelib.config.language.BukkitLanguageMessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;

public class FilterManager implements Listener {
    private static final BukkitLanguageKey MESSAGE_BLOCK = new BukkitLanguageKey("user.block",
            "&7&m------------------------------------------------\n" +
                    "&cYour message was blocked.\n" +
                    "&cPlease follow the server rules when chatting.\n" +
                    "&7&m------------------------------------------------");

    private static final BukkitLanguageKey MESSAGE_FAKE_CHAT_FORMAT = new BukkitLanguageKey("user.fake-chat-format",
            "This setting is purely for compatibility for some chat plugins.\n" +
                    "If you find that all players can see 'faked' chat messages, try using this.\n" +
                    "In most cases, you can keep this empty.\n" +
                    "\n" +
                    "If you need this, set it to how your chat is normally formatted.\n" +
                    "Supports PlaceholderAPI if it is installed.",

            "");

    //

    private static final BukkitLanguageKey MESSAGE_STAFF_BLOCK = new BukkitLanguageKey("staff.block",
            "&7[&c&lFilter&7] &c(Blocked) %player%&f: &7%message%");

    private static final BukkitLanguageKey MESSAGE_STAFF_FAKE = new BukkitLanguageKey("staff.fake",
            "&7[&c&lFilter&7] &c(Faked) %player%&f: &7%message%");

    private static final BukkitLanguageKey MESSAGE_STAFF_CENSOR = new BukkitLanguageKey("staff.censor",
            "&7[&c&lFilter&7] &c(Censored) %player%&f: &7%message%");

    //

    private final ChatFilterConfig config;

    private final Map<FilterAction, FilterPattern> patternMap = new EnumMap<>(FilterAction.class);
    private final StringBuffer stringBuffer = new StringBuffer(); // Warning: Not thread safe

    @Inject
    public FilterManager(ChatFilterConfig config) {
        this.config = config;

        ChatFilterConfig.FilterConfig filters = this.config.getFilters();

        this.patternMap.put(FilterAction.BLOCK, FilterPattern.compile(filters.getBlock()));
        this.patternMap.put(FilterAction.FAKE, FilterPattern.compile(filters.getFake()));
        this.patternMap.put(FilterAction.CENSOR, FilterPattern.compile(filters.getCensor()));
    }

    private String transformMessage(String message) {
        if (this.config.isRemoveLeetspeak()) {
            // just remove some simple variations
            message = message
                    .replace('4', 'a')
                    .replace('3', 'e')
                    .replace('5', 's')
                    .replace('7', 't')
                    .replace('1', 'i')
                    .replace('0', 'o');
        }

        return message;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // check for bypass permission
        if (this.config.isEnableBypassPermission() && player.hasPermission("chatfilter.bypass")) return;

        String message = this.transformMessage(event.getMessage());

        for (FilterAction action : FilterAction.values()) {
            FilterPattern filterPattern = this.patternMap.get(action);
            if (filterPattern == null) continue;

            // reset matcher
            Matcher matcher = filterPattern.getMatcher();
            matcher.reset(message);

            if (action == FilterAction.CENSOR) {
                if (this.doStaffMessage(matcher)) {
                    // return if the chat filter event was cancelled
                    if (this.callFilterEvent(player, event.getMessage(), action)) return;

                    String highlighted = this.stringBuffer.toString();
                    MESSAGE_STAFF_CENSOR.builder("chatfilter.notifications.censor")
                            .setPlaceholder("player", player.getDisplayName())
                            .setPlaceholder("message", highlighted, false)
                            .send();

                    // clear string buffer & matcher (currently contains staff message), then do censoring
                    this.stringBuffer.setLength(0);
                    matcher.reset(message);

                    this.doCensor(matcher);
                    event.setMessage(this.stringBuffer.toString());

                    this.stringBuffer.setLength(0);
                }

                return; // 'CENSOR' should be the last iteration anyways, but still return
            } else {
                // action is BLOCK or FAKE

                if (this.doStaffMessage(matcher)) { // match was found

                    // return if the chat filter event was cancelled
                    if (this.callFilterEvent(player, event.getMessage(), action)) return;

                    String highlighted = this.stringBuffer.toString();

                    if (action == FilterAction.BLOCK) {
                        event.setCancelled(true);

                        MESSAGE_BLOCK.builder(player)
                                .setPlaceholder("message", event.getMessage(), false)
                                .setPlaceholder("message-highlighted", event.getMessage(), false)
                                .send();

                        MESSAGE_STAFF_BLOCK.builder("chatfilter.notifications.block")
                                .setPlaceholder("player", player.getDisplayName())
                                .setPlaceholder("message", highlighted, false)
                                .send();
                    } else if (action == FilterAction.FAKE) {
                        if (MESSAGE_FAKE_CHAT_FORMAT.isEmpty()) {
                            // remove all recipients except the player
                            event.getRecipients().removeIf(p -> p != player);

                            MESSAGE_STAFF_FAKE.builder("chatfilter.notifications.fake")
                                    .setPlaceholder("player", player.getDisplayName())
                                    .setPlaceholder("message", highlighted, false)
                                    .send();
                        } else {
                            // If the 'Fake Chat Format' message is not empty, send a fake message manually,
                            // instead of removing all recipients. This is mainly for compatibility with
                            // chat plugins that don't respect the recipients list.

                            BukkitLanguageMessageBuilder builder = MESSAGE_FAKE_CHAT_FORMAT.builder(player)
                                    .setPlaceholder("player", player.getDisplayName())
                                    .setPlaceholder("message", event.getMessage());

                            // allow PlaceholderAPI support
                            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                builder.withPlaceholderAPI(player);
                            }

                            builder.send();
                        }
                    }

                    this.stringBuffer.setLength(0); // reset string buffer

                    // return, because if the next iteration were to continue, no action would be taken anyways
                    // order of iteration is: BLOCK, FAKE, CENSOR
                    return;
                }
            }
        }
    }

    private boolean doStaffMessage(Matcher matcher) {
        boolean found = false;

        while (matcher.find()) {
            found = true; // a match was found, so block the message

            String part = matcher.group();

            // highlight matched parts of message
            String colored = ChatColor.WHITE + "" + ChatColor.BOLD + part + ChatColor.GRAY;

            matcher.appendReplacement(this.stringBuffer, "");
            this.stringBuffer.append(colored);
        }

        if (found) matcher.appendTail(this.stringBuffer);
        return found;
    }

    private void doCensor(Matcher matcher) {
        while (matcher.find()) {
            String part = matcher.group();

            char[] chars = part.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];

                // skip if this is the last char and it's a space
                if (c == ' ' && i == chars.length - 1) continue;

                chars[i] = '*'; // replace with asterisk
            }

            matcher.appendReplacement(this.stringBuffer, "");
            this.stringBuffer.append(chars);
        }

        matcher.appendTail(this.stringBuffer);
    }

    private boolean callFilterEvent(Player player, String message, FilterAction action) {
        AsyncChatFilterEvent filterEvent = new AsyncChatFilterEvent(player, message, action);
        Bukkit.getPluginManager().callEvent(filterEvent);
        return filterEvent.isCancelled();
    }
}
