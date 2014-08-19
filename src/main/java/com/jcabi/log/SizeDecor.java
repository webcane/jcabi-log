/**
 * Copyright (c) 2012-2014, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Size decorator.
 * @author Marina Kosenko (marina.kosenko@gmail.com)
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@ToString
@EqualsAndHashCode(of = "size")
final class SizeDecor implements Formattable {

    /**
     * Highest power supported by this SizeDecor.
     */
    private static final int MAX_POWER = 8;

    /**
     * Map of prefixes for powers of 1024.
     */
    private static final ConcurrentMap<Integer, String> SUFFIXES =
        new ConcurrentHashMap<Integer, String>();

    /**
     * The size to work with.
     */
    private final transient Long size;

    static {
        // @checkstyle MagicNumber (9 lines)
        SizeDecor.SUFFIXES.put(0, "b");
        SizeDecor.SUFFIXES.put(1, "Kb");
        SizeDecor.SUFFIXES.put(2, "Mb");
        SizeDecor.SUFFIXES.put(3, "Gb");
        SizeDecor.SUFFIXES.put(4, "Tb");
        SizeDecor.SUFFIXES.put(5, "Pb");
        SizeDecor.SUFFIXES.put(6, "Eb");
        SizeDecor.SUFFIXES.put(7, "Zb");
        SizeDecor.SUFFIXES.put(8, "Yb");
    }

    /**
     * Public ctor.
     * @param sze The size
     */
    public SizeDecor(final Long sze) {
        this.size = sze;
    }

    /**
     * {@inheritDoc}
     * @checkstyle ParameterNumber (4 lines)
     */
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.size == null) {
            formatter.format("NULL");
        } else {
            final StringBuilder format = new StringBuilder().append('%');
            if ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags
                .LEFT_JUSTIFY) {
                format.append('-');
            }
            if (width > 0) {
                format.append(Integer.toString(width));
            }
            if ((flags & FormattableFlags.UPPERCASE) == FormattableFlags
                .UPPERCASE) {
                format.append('S');
            } else {
                format.append('s');
            }
            formatter.format(
                format.toString(), this.formatSizeWithSuffix(precision)
            );
        }
    }

    /**
     * Format the size, with suffix.
     * @param precision The precision to use
     * @return The formatted size
     */
    private String formatSizeWithSuffix(final int precision) {
        int power = 0;
        final StringBuilder format = new StringBuilder().append('%');
        format.append('.');
        if (precision > 0) {
            format.append(precision);
        } else {
            format.append(0);
        }
        format.append("f%s");
        double displayed = this.size;
        // @checkstyle MagicNumber (2 lines)
        while (displayed / 1024 >= 1 && power < MAX_POWER) {
            displayed = displayed / 1024;
            power += 1;
        }
        final String suffix = SUFFIXES.get(power);
        final String output = String.format(
            format.toString(), displayed, suffix
        );
        return output;
    }

}
