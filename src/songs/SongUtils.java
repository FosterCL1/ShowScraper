package songs;

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

}
