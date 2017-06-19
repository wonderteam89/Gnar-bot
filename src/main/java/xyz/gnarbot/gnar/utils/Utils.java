package xyz.gnarbot.gnar.utils;

import net.dv8tion.jda.core.entities.Message;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final Pattern TIMESTAMP_PATTERN =
            Pattern.compile("(-?\\d+)\\s*((?:d(?:ay(?:s)?)?)|(?:h(?:our(?:s)?)?)|(?:m(?:in(?:ute(?:s)?)?)?)|(?:s(?:ec(?:ond(?:s)?)?)?))?");

    public static long parseTimestamp(String s) {
        s = s.toLowerCase();
        long ms = 0;

        Matcher matcher = TIMESTAMP_PATTERN.matcher(s);

        while (matcher.find()) {
            String numStr = matcher.group(1);
            String unitStr = matcher.group(2);

            TimeUnit unit;
            if (unitStr == null) {
                unit = TimeUnit.SECONDS;
            } else switch (unitStr) {
                case "d":
                case "day":
                case "days":
                    unit = TimeUnit.DAYS;
                    break;
                case "h":
                case "hour":
                case "hours":
                    unit = TimeUnit.HOURS;
                    break;
                case "m":
                case "min":
                case "minute":
                case "minutes":
                    unit = TimeUnit.MINUTES;
                    break;
                case "s":
                case "sec":
                case "second":
                case "seconds":
                default:
                    unit = TimeUnit.SECONDS;
                    break;
            }
            ms += unit.toMillis(Long.parseLong(numStr));
        }
        return ms;
    }

    public static Consumer<Message> deleteMessage(final int seconds) {
        return msg -> msg.delete().queueAfter(seconds, TimeUnit.SECONDS);
    }

    public static String[] stringSplit(String s) {
        List<String> f = new ArrayList<>();

        int p = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                f.add(s.substring(p, i));
                p = i + 1;
                while (i < s.length() - 1 && s.charAt(i + 1) == ' ') {
                    i++;
                    p++;
                }
            } else if (s.charAt(i) == '`') {
                if (s.substring(i, Math.min(i + 3, s.length())).equals("```")) {
                    int start = i + 3;
                    i += 3;
                    while (i < s.length() - 3 && !s.substring(i, Math.min(i + 3, s.length())).equals("```")) {
                        i++;
                    }
                    if (s.substring(i, Math.min(i + 3, s.length())).equals("```")) {
                        int end = i;
                        f.add(s.substring(start, end));
                        i += 3;
                        p = i;
                    } else { // unterminated
                        f.add(s.substring(start, i));
                    }
                }
            } else if (s.charAt(i) == '\n'
                    || s.charAt(i) == '\r' && s.charAt(i + 1) == '\n') {
                f.add(s.substring(p, i + 1));
                p = i + 1;
            }
        }
        f.add(s.substring(p));

        return f.toArray(new String[f.size()]);
    }

    public static String getTimestamp(long milliseconds) {
        long seconds = milliseconds / 1000 % 60;
        long minutes = milliseconds / (1000 * 60) % 60;
        long hours = milliseconds / (1000 * 60 * 60) % 24;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String hasteBin(String content) {
        Request request = new Request.Builder().url("https://hastebin.com/documents")
                .header("User-Agent", "Gnar")
                .header("Content-Type", "text/plain")
                .post(RequestBody.create(null, content))
                .build();

        try (Response response = HttpUtils.CLIENT.newCall(request).execute()) {
            JSONObject jso = new JSONObject(new JSONTokener(response.body().byteStream()));

            response.close();

            return "https://hastebin.com/" + jso.get("key");
        } catch (IOException e) {
            e.printStackTrace();
            return "Error posting to HasteBin.";
        }
    }
}
