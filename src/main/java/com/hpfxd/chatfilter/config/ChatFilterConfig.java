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

package com.hpfxd.chatfilter.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
public class ChatFilterConfig {
    @Setting
    private FilterConfig filters = new FilterConfig();

    @Setting
    @Comment("If this is enabled, players with the permission 'chatfilter.bypass' will not be affected by filters.")
    private boolean enableBypassPermission = false;

    @Setting
    @Comment("Remove simple leetspeak from messages before applying filters.\n" +
            "See: https://simple.wikipedia.org/wiki/Leet")
    private boolean removeLeetspeak = false;

    public FilterConfig getFilters() {
        return filters;
    }

    public boolean isEnableBypassPermission() {
        return enableBypassPermission;
    }

    public boolean isRemoveLeetspeak() {
        return removeLeetspeak;
    }

    @ConfigSerializable
    public static class FilterConfig {
        @Comment("Filters in this section will stop the message from sending completely, and send the player a message saying it was blocked.")
        private List<String> block = Arrays.asList(
                "\\bfrick",
                "\\bfr1ck"
        );

        @Comment("Filters in this section will send the chat message only to the sender, hiding it from all other players.\n" +
                "May not be compatible with all chat plugins.")
        private List<String> fake = Arrays.asList(
                "\\bheck",
                "\\bh3ck"
        );

        @Comment("Filters in this section will replace the bad words with asterisks and send the message normally.")
        private List<String> censor = Collections.singletonList(
                "\\bnugget"
        );

        public List<String> getBlock() {
            return block;
        }

        public List<String> getFake() {
            return fake;
        }

        public List<String> getCensor() {
            return censor;
        }
    }
}
