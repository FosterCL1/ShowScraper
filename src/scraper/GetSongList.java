package scraper;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import songs.Song;
import songs.SongUtils;

public class GetSongList {
	static String site = "http://www.phish.net/songs";
	
	public static void main(String[] args) {
		try {
			Document document = Jsoup.connect(site).get();
			
			Elements elements = document.select(".originals :first-of-type a");
			
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
	
	private static List<Song> getSongListHelper(String searchQuery) {
		List<Song> songList = new LinkedList<Song>();
		
		try {
			Document document = Jsoup.connect(site).get();
			
			Elements elements = document.select(searchQuery +" :first-of-type a");
			
			for (Element element : elements) {
				// TODO: Make sure that these aren't aliases
				
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
	
	private static void removeAliases(List<Song> songList) {
		List<Song> aliasList = getSongListHelper(".aliases");
		
		for (Song alias : aliasList) {
			int aliasIndex = SongUtils.getIndexOfSongByName(songList, alias);
			if (aliasIndex >= 0) {
				songList.remove(aliasIndex);
			}
		}
	}
	
	private static void addCovers(List<Song> songList) {
		List<Song> coversList = getSongListHelper(".covers");
		
		for (Song cover : coversList) {
			songList.add(cover);
		}
	}
	
	public static List<Song> getOriginalsList() {
		return getSongListHelper(".originals");
	}

	public static List<Song> getCoversList() {
		return getSongListHelper(".covers");
	}

	public static List<Song> getShortSongList(int numItems) {
		List<Song> songList = getSongList(false);
		
		return songList.subList(0, numItems);
	}
	
	public static List<Song> getSongList(boolean bGetCovers) {
		List<Song> songList = getOriginalsList();
		
		if (bGetCovers) 
		{
			addCovers(songList);
		}
		
		removeAliases(songList);
		return songList;
	}
}
