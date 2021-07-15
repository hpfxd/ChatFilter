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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterPattern {
    private final Pattern pattern;
    private final Matcher matcher;

    public FilterPattern(Pattern pattern) {
        this.pattern = pattern;
        this.matcher = this.pattern.matcher("");
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public static FilterPattern compile(List<String> patterns) {
        if (patterns.isEmpty()) return null;

        // Join all patterns together into one big pattern using the | operator
        // Offers a bit less flexibility, but a great speed improvement over
        // checking every pattern individually.

        return new FilterPattern(Pattern.compile(String.join("|", patterns),
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
    }
}
