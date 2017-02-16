package shows;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import scraper.GetSongListOfShow;
import songs.Song;

public class Show implements Comparable<Show> {

	private Date date;
	private List<Song> songs;
	private String url;
	
	public Show(String link) {
		url = link.split("\\?")[0];
	}
	
	public String getURL() {
		return url;
	}
	
	@Override
	public int compareTo(Show o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString() {
		if (date != null) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
		} else {
			return url;
		}
	}
	
	public void populateSongList() {
		songs = GetSongListOfShow.getSongListOfShow(this);
	}
	
	public List<Song> getSongList() {
		return songs;
	}

}
