package songs;

import java.util.Iterator;
import java.util.List;

public class SongUtils {

	public static int getIndexOfSongByURL(List<Song> songList, Song song) {
		if (songList == null) {
			return -1;
		}
		
		for (int i = 0; i < songList.size(); i++) {
			Song tempSong = songList.get(i);
			if (tempSong.getURL().equals(song.getURL())) {
				return i;
			}
		}
		return -1;
	}
	
	public static int getIndexOfSongByName(List<Song> songList, Song song) {
		if (songList == null) {
			return -1;
		}
		
		for (int i = 0; i < songList.size(); i++) {
			Song tempSong = songList.get(i);
			if (tempSong.getName().equals(song.getName())) {
				return i;
			}
		}
		return -1;
	}
	
	public static Song getNextUnplayedSong(List<Song> songList) {
		Song rval = null;
		for (Song song : songList) {
			if (!song.isSelected()) {
				rval = song;
				break;
			}
		}
		return rval;
	}
	
	public static Song getNextUnplayedSong(List<Song> songList, Integer startIndex) {
		Song rval = null;
		Song song;
		
		Iterator<Song> songIter = songList.iterator();
		
		while (songIter.hasNext()) {
			song = songIter.next();
			if (!song.isSelected()) {
				rval = song;
				break;
			}
		}
		
		//for (Integer counter = startIndex; counter < songList.size(); counter++) {
		//for (Song song : songList) {
		//	song = songList.get(counter);
		//	if (!song.isSelected()) {
		//		rval = song;
		//		break;
		//	}
		//}
		
		return rval;
	}

}
