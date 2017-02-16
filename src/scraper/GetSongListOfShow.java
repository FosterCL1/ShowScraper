package scraper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import shows.Show;
import songs.Song;

public class GetSongListOfShow {
	static String site = "http://phish.net/setlists/phish-december-31-1998-madison-square-garden-new-york-ny-usa.html?highlight=653";
	
	public static void main(String[] args) {
		try {
			Document document = Jsoup.connect(site).get();
			
			Elements elements = document.select(".setlist-song");
			
 			System.out.println("Number of elements: " + elements.size());
			
			for (Element element : elements) {
				//List<DataNode> dataNodes = element.dataNodes();
				//element.text();
				//if (dataNodes.size() > 0) {
				//	DataNode node = dataNodes.get(0);
				//	System.out.println("node: " + node.toString());
				//}
				//System.out.println("attr: " + element.attr("a"));
				//System.out.println("data: " + element.data());
				System.out.println("Found song: \"" + element.text() + "\" link: \"" + element.baseUri() + element.attr("href") + "\"");
			}
			
		} catch (IOException e) {
			System.out.println("Exception thrown");
			e.printStackTrace();
		}
	}
	
	public static List<Song> getSongListOfShow(Show show) {
		List<Song> songList = new LinkedList<Song>();
		try {
			Document document = Jsoup.connect(show.getURL()).get();
			
			Elements elements = document.select(".setlist-song");
			
 			//System.out.println("Number of elements: " + elements.size());
			
			for (Element element : elements) {
				Song song = new Song(element.text(), "http://www.phish.net/" + element.attr("href"));
				songList.add(song);
				//System.out.println("Found song: \"" + element.text() + "\" link: \"" + element.baseUri() + element.attr("href") + "\"");
			}
			
		} catch (IOException e) {
			System.out.println("Exception thrown");
			e.printStackTrace();
		}
		return songList;
	}
}
