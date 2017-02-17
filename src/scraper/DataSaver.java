package scraper;

import java.io.PrintWriter;
import java.util.List;

import shows.Show;
import songs.Song;
import songs.SongUtils;
import shows.ShowUtils;

public class DataSaver extends DataPaths {

	public static boolean saveListsToFiles(List<Song> songList, List<Show> showList) {

		// Save the lists to files
		try {
			PrintWriter songListWriter = new PrintWriter(songListFile, "UTF-8");			
			PrintWriter showListOfSongsWriter = new PrintWriter(showsPerSongFile, "UTF-8");
			PrintWriter showListWriter = new PrintWriter(showListFile, "UTF-8");
			PrintWriter songListOfShowsWriter = new PrintWriter(songsPerShowFile, "UTF-8");
			
			// Write the song and the corresponding show lists
			for (Song song : songList) {
				int songIndex = SongUtils.getIndexOfSongByURL(songList, song);
				if (songIndex < 0) {
					System.out.println("Error: Song not found in list: " + song.toString());
					continue;
				}
				songListWriter.println(songIndex + "," + song.getName() + "," + song.getURL());
				
				List<Show> showListOfSong = song.getShowList();
				if (showListOfSong == null) {
					continue;
				}
				
				for (Show show : showListOfSong) {
					int showIndex = ShowUtils.getIndexOfShow(showList, show);
					if (showIndex < 0) {
						System.out.println("Error: Show not found in show list of song: " + song);
						continue;
					}
					showListOfSongsWriter.println(songIndex + "," + showIndex);
				}
			}
			songListWriter.close();
			showListOfSongsWriter.close();
			
			// Write the show and the corresponding song lists
			for (Show show : showList) {
				int showIndex = ShowUtils.getIndexOfShow(showList, show);
				if (showIndex < 0) {
					System.out.println("Error: Show not found in list: " + show.toString());
					continue;
				}
				showListWriter.println(showIndex + "," + show.getURL() );
				
				List<Song> songListOfShow = show.getSongList();
				
				if (songListOfShow == null) {
					continue;
				}
				
				for (Song song : songListOfShow) {
					int songIndex = SongUtils.getIndexOfSongByURL(songList, song);
					if (songIndex < 0) {
						System.out.println("Error: Song " + song.getName() + " not found in song list of show: " + show.toString());
						continue;
					}
					songListOfShowsWriter.println(showIndex + "," + songIndex);
				}
			}
			showListWriter.close();
			songListOfShowsWriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		
		return false;
	}
	
}
