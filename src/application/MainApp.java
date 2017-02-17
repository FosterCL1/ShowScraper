package application;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import scraper.DataLoader;
import scraper.DataSaver;
import scraper.GetSongList;
import shows.Show;
import shows.ShowList;
import songs.Song;
import songs.SongUtils;

public class MainApp {
	private static List<Song> songList;
	private static ShowList showList;
	// TODO: Calculate this number
	private static int maxSongsPerShow = 30;
	private static int numSongsLeftToSelect;
	
	private static boolean getListsFromInternet() {
		showList = new ShowList();
		songList = GetSongList.getSongList();
		//songList = GetSongList.getShortSongList(2);
		
		for (Song song : songList) {
			System.out.println("Songs: " + song.toString());
			song.populateShowList(songList);
		}
		
		songList.sort(null);

		for (Song song : songList) {
			System.out.println("Getting list for song: " + song.getName());
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
		
		return false;
	}
	
	// NOTE: Trying this with 57 songs took 20,000 seconds. Try something else
	private static boolean chooseShow(final int numShowsToSelect) {
		boolean rval = false;
		
		// Check if we can exit early
		if (numShowsToSelect * maxSongsPerShow < numSongsLeftToSelect) {
			//System.out.println("Early exit - Need to select " + numSongsLeftToSelect + " songs in only " + numShowsToSelect + " shows");
			return false;
		}
		
		Song nextUnplayedSong = SongUtils.getNextUnplayedSong(songList);
		if (nextUnplayedSong == null) {
			// The "we're done" return
			return true;
		} else {
			List<Show> currentShowList = nextUnplayedSong.getShowList();
			
			for (Show currentShow : currentShowList) {
				int numSongsAdded = currentShow.select();
				
				numSongsLeftToSelect -= numSongsAdded;
				
				if (	numSongsLeftToSelect == 0
						|| chooseShow(numShowsToSelect - 1)) {
					return true;
				} else {
					int numSongsRemoved = currentShow.unselect();
					numSongsLeftToSelect += numSongsRemoved;
				}
			}
		}
		
		return false;
	}
	
	private static void doGreedyRoutine() {
		Song nextUnplayedSong;
		while ((nextUnplayedSong = SongUtils.getNextUnplayedSong(songList)) != null) {
			List<Show> currentShowList = nextUnplayedSong.getShowList();
			
			if (currentShowList == null) {
				continue;
			}
			
			double showWeight = 0;
			int optimalIndex = -1;
			for (int i = 0; i < currentShowList.size(); i++) {
				Show currentShow = currentShowList.get(i);
				double tempShowWeight = currentShow.getShowWeight();
				if (tempShowWeight > showWeight) {
					showWeight = tempShowWeight;
					optimalIndex = i;
				}
			}
			
			if (optimalIndex > -1) {
				Show selectedShow = currentShowList.get(optimalIndex);
				selectedShow.select();
			}
		}
		
		int i = 0;
		for (Show showDisplay : showList) {
			if (showDisplay.isSelected()) {
				i++;
				System.out.println(i + ": " + showDisplay.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		songList = new LinkedList<Song>();
		showList = new ShowList();
	    
		if (DataLoader.loadListsFromFiles(songList, showList)) {
			System.out.println("Error reading from files. Pulling from the interwebs");
			if (getListsFromInternet()) {
				System.out.println("Error getting info from files. Abort");
				return;
			} else {
				if (DataSaver.saveListsToFiles(songList, showList)) {
					System.out.println("Error saving lists to files");
				}
			}
		}
		
		numSongsLeftToSelect = songList.size();
		
		doGreedyRoutine();
		
		/*
		// This is the recursive algorithm. Takes too long!
		// Start with a single show. Loop until we have tried almost every show. Hopefully it'll be much less than that!
		for (int numShowsToSelect = 1; numShowsToSelect < songList.size(); numShowsToSelect++) {
			
			long startTime = System.nanoTime();
			
			if (chooseShow(numShowsToSelect)) {
				// Success!
				System.out.println("Successfully got it with " + numShowsToSelect + " shows!");
				break;
			}
			
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			
			System.out.println("Tried with " + numShowsToSelect + " shows - took: " + (duration / 1000000000.0) + " seconds");
		}
		*/
		
	}
	
}
