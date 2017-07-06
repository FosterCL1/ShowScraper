package shows;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import scraper.GetSongListOfShow;
import songs.Song;

public class Show implements Comparable<Show> {

	private Date date;
	private List<Song> songs;
	private String url;
	private int selected = 0;
	
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
	
	public void addSong(Song song) {
		if (songs == null) {
			songs = new LinkedList<Song>();
		}
		songs.add(song);
	}
	
	public void populateSongList() {
		songs = GetSongListOfShow.getSongListOfShow(this);
	}
	
	public List<Song> getSongList() {
		return songs;
	}
	
	public void pruneSongList() {
		// Make this an array instead of a linked list
		// Remove anything that has been played already.
		List<Song> prunedSongList = new ArrayList<Song>(songs.size());
		//List<Song> prunedSongList = new LinkedList<Song>();
		
		for(Song currentSong : songs) {
			if (!currentSong.isSelected()) {
				prunedSongList.add(currentSong);
			}
		}
		
		songs = prunedSongList;
	}
	
	public int select() {
		int numSongsSelected = 0;
		for (Song song : songs) {
			if (song.select()) {
				numSongsSelected++;
			}
		}
		selected++;
		return numSongsSelected;
	}

	public int unselect() {
		int numSongsUnselected = 0;
		for (Song song : songs) {
			if (song.unselect()) {
				numSongsUnselected++;
			}
		}
		if (selected > 0) {
			selected--;
		}
		
		return numSongsUnselected;
	}
	
	public double getShowWeight() {
		double rval = 0;
		for (Song song : songs) {
			if (!song.isSelected()) {
				// TODO: This should look into the absolute size of the song selections and adjust the weight accordingly
				rval += 1;
			}
		}
		return rval;
	}
	
	public boolean isSelected() {
		return (selected > 0);
	}
}
