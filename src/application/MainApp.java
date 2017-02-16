package application;

import java.io.PrintWriter;
import java.util.List;

import scraper.GetSongList;
import shows.Show;
import shows.ShowList;
import songs.Song;

public class MainApp {
	private static List<Song> songList;
	private static ShowList showList;
	
	public static void main(String[] args) {
		showList = new ShowList();
		songList = GetSongList.getSongList();
		
		for (Song song : songList) {
		/*
		if (true) {
			Song song = songList.get(1);
			*/			
			System.out.println("Songs: " + song.toString());
			song.populateShowList(songList);

			List<Show> currentShowList = song.getShowList();
			for (Show show : currentShowList) {
				if (!showList.contains(show)) {
					show.populateSongList();
					showList.add(show);
					System.out.println("Shows: " + showList.size());
				} else {
					System.out.println("Skipping show already in list");
				}
			}
		}
		
		// Save the lists to files
		try {
			PrintWriter songListWriter = new PrintWriter("SongList.csv", "UTF-8");			
			PrintWriter showListOfSongsWriter = new PrintWriter("ListOfShowsPerSong.csv", "UTF-8");
			PrintWriter showListWriter = new PrintWriter("ShowList.csv", "UTF-8");
			PrintWriter songListOfShowsWriter = new PrintWriter("ListOfSongsPerShow.csv", "UTF-8");
			
			// Write the song and the corresponding show lists
			for (Song song : songList) {
				int songIndex = getIndexOfSong(song);
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
					int showIndex = getIndexOfShow(show);
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
				int showIndex = getIndexOfShow(show);
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
					int songIndex = getIndexOfSong(song);
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
		}
		
		/*
		for (Song song : songList) {
			System.out.print(song.toString());
			System.out.print("\n");
		}
		*/
		
		//songList.sort(null);
	}
	
	private static int getIndexOfSong(Song song) {
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

	private static int getIndexOfShow(Show show) {
		if (showList == null) {
			return -1;
		}
		
		for (int i = 0; i < showList.size(); i++) {
			Show tempShow = showList.get(i);
			if (tempShow.getURL().equals(show.getURL())) {
				return i;
			}
		}
		return -1;
	}
}
