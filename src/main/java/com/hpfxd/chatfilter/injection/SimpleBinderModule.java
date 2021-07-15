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

package com.hpfxd.chatfilter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hpfxd.chatfilter.ChatFilterPlugin;
import com.hpfxd.chatfilter.config.ChatFilterConfig;

public class SimpleBinderModule extends AbstractModule {
    private final ChatFilterPlugin plugin;

    public SimpleBinderModule(ChatFilterPlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(ChatFilterPlugin.class).toInstance(this.plugin);
        this.bind(ChatFilterConfig.class).toInstance(this.plugin.getConfiguration());
    }
}
