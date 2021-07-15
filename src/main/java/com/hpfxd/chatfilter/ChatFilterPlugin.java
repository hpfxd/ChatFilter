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

package com.hpfxd.chatfilter;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hpfxd.chatfilter.config.ChatFilterConfig;
import com.hpfxd.chatfilter.filter.FilterManager;
import com.hpfxd.chatfilter.injection.SimpleBinderModule;
import com.hpfxd.natelib.ExtendedJavaPlugin;
import com.hpfxd.natelib.config.ConfigFile;
import com.hpfxd.natelib.config.language.LanguageManager;

public class ChatFilterPlugin extends ExtendedJavaPlugin {
    private ChatFilterConfig config;

    @Inject private FilterManager filterManager;

    @Override
    protected void enable() throws Exception {
        this.setupConfig();
        LanguageManager.init(this.createConfig("language"));

        this.setupGuice();
        this.addListener(this.filterManager);

        LanguageManager.save();
    }

    @Override
    protected void disable() throws Exception {
    }

    private void setupConfig() throws Exception {
        ConfigFile configFile = this.createConfig("config");
        this.config = configFile.map(ChatFilterConfig.class);
        configFile.save();
    }

    private void setupGuice() {
        SimpleBinderModule module = new SimpleBinderModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);
    }

    public ChatFilterConfig getConfiguration() {
        return config;
    }
}
