package songs;

import java.util.LinkedList;
import java.util.List;

import scraper.GetShowListOfSong;
import shows.Show;

public class Song implements Comparable<Song> {
	private List<Show> showList;
	private String name;
	private String url;
	private int playedCount = 0;
	
	public Song(String name, String url){
		//System.out.println("Creating the song object for " + name);
		this.name = name;
		this.url = url;
	}
	
	public boolean isSelected() {
		if (	showList == null 
				|| showList.size() == 0
				|| playedCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean select() {
		playedCount++;
		if (playedCount == 1) {
			return true;
		}
		return false;
	}
	
	public boolean unselect() {
		if (playedCount == 0) {
			return false;
		} else {
			playedCount--;
			if (playedCount == 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int compareTo(Song o) {
		if (listSize() < o.listSize())
			return -1;
		else if (listSize() > o.listSize())
			return 1;
		else return 0;
	}
	
	public int listSize() {
		if (showList == null) {
			return 0;
		} else {
			return showList.size();
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Song: ");
		sb.append(name);
		sb.append("\n");
		if (showList != null) {
			for (Show show : showList) {
				sb.append("\t");
				sb.append(show.toString());
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
	public void addShow(Show show) {
		if (this.showList == null) {
			showList = new LinkedList<Show>();
		}
		showList.add(show);
	}
	
	public void populateShowList(List<Song> songList){
		showList = GetShowListOfSong.getListOfShows(url, songList);
	}
	
	public List<Show> getShowList() {
		return showList;
	}
	
	public String getName() {
		return new String(this.name);
	}
	
	public String getURL() {
		return new String(this.url);
	}
}
