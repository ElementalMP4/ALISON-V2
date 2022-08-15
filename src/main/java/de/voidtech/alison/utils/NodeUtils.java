package main.java.de.voidtech.alison.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.jsoup.Jsoup;

import main.java.de.voidtech.alison.Alison;
import net.dv8tion.jda.api.entities.User;

//Used exclusively for communicating with the Node.JS API
//Sometimes it's more convenient to use JavaScript alright...

public class NodeUtils {

	public static byte[] getQuoteImage(User user, String quote) {
		try {
			String cardURL = Alison.getConfig().getApiUrl() + "quote/?avatar_url=" + user.getEffectiveAvatarUrl() + "?size=2048"
					+ "&username=" + URLEncoder.encode(user.getName(), StandardCharsets.UTF_8.toString())
					+ "&quote=" + URLEncoder.encode(quote, StandardCharsets.UTF_8.toString());
			URL url = new URL(cardURL);
			//Remove the data:image/png;base64 part
			String response = Jsoup.connect(url.toString()).get().toString().split(",")[1];
			byte[] imageBytes = DatatypeConverter.parseBase64Binary(response);
			return imageBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getAlisonMentionIntent(String message) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(Alison.getConfig().getApiUrl() + "intent/?message="
					+ URLEncoder.encode(message, StandardCharsets.UTF_8.toString())).openConnection();
            con.setRequestMethod("GET");
            if (con.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    return in.lines().collect(Collectors.joining());
                }
            }
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Neutral";
    }
}