/*
 * Copyright (c) 2012-2013 Bruno Barbieri
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package mkvchap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeInterval {
    short milliSeconds;
    short seconds;
    short minutes;
    int hours;

    public TimeInterval (String dateString) {
        // HHHHHH:MI:SS.SSS
        Pattern pattern = Pattern.compile("(\\d+):(\\d\\d):(\\d\\d)\\.(\\d\\d\\d)");
        Matcher matcher = pattern.matcher(dateString);

        if (matcher.find()) {
            hours = Integer.parseInt(dateString.substring(matcher.start(1), matcher.end(1)));
            minutes = Short.parseShort(dateString.substring(matcher.start(2), matcher.end(2)));
            seconds = Short.parseShort(dateString.substring(matcher.start(3), matcher.end(3)));
            milliSeconds = Short.parseShort(dateString.substring(matcher.start(4), matcher.end(4)));
        }
    }

    private TimeInterval() { }


    public TimeInterval add(TimeInterval interval) {
        TimeInterval ret = new TimeInterval();
        ret.milliSeconds = (short) ((interval.milliSeconds + milliSeconds) % 1000);
        int carry = (interval.milliSeconds + milliSeconds) / 1000;

        ret.seconds = (short) ((interval.seconds + seconds + carry) %60 );
        carry = (interval.seconds + seconds + carry) / 60;

        ret.minutes = (short) ((interval.minutes + minutes + carry) %60);
        carry = (interval.minutes + minutes + carry) / 60;

        ret.hours = (interval.hours + hours + carry);
        return ret;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliSeconds);
    }
}
