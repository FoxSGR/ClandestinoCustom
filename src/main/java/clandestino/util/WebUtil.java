package clandestino.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public final class WebUtil {

    private WebUtil() {

    }

    public static String htmlFromPage(String path) throws IOException {
        String pw = "bk[*.JbQfdNjikx";
        StringBuilder actualPw = new StringBuilder();
        for (int i = 0; i < pw.length(); i++) {
            actualPw.append((char) (pw.charAt(i) - i + 10));
        }

        URL url = new URL("ftp://b33_24332929:" + actualPw + "@ftpupload.net/" + path);
        URLConnection con = url.openConnection();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            return in.lines().collect(Collectors.joining());
        }
    }
}
