package application;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import minimalProblem.MinimalProblem;
import scraper.DataLoader;
import scraper.DataSaver;
import scraper.GetSongList;
import shows.Show;
import shows.ShowList;
import songs.Song;
import songs.SongUtils;

class ExecutionQueue<E> {
	private LinkedList<E> list = new LinkedList<E>();
	public void enqueue(E item) {
	    list.addLast(item);
	}
	public E dequeue() {
	    return list.poll();
	}
	public boolean hasItems() {
	    return !list.isEmpty();
	}
	public int size() {
	    return list.size();
	}
	public void addItems(ExecutionQueue<? extends E> q) {
	    while (q.hasItems()) list.addLast(q.dequeue());
    }
}

class ExecutionObject2 {
	public List<Integer> selectedShows;
	public HashMap<Integer, List<Integer>> unselectedSongs;
	public Integer lastSongIndex;
	
	public ExecutionObject2 (List<Integer> selectedShows, HashMap<Integer, List<Integer>> unselectedSongs, Integer lastSongIndex) {
		this.selectedShows = selectedShows;
		this.unselectedSongs = unselectedSongs;
		this.lastSongIndex = lastSongIndex;
	}
	
	public ExecutionObject2 (ExecutionObject2 original) {
		selectedShows = new LinkedList<Integer>(original.selectedShows);
		unselectedSongs = (HashMap<Integer, List<Integer>>) original.unselectedSongs.clone();
		lastSongIndex = new Integer(original.lastSongIndex);
	}
}

class ExecutionObject {
	ShowList mShowList;
	List<Song> mUnselectedSongs;
	
	public ExecutionObject(ShowList showList, List<Song> unselectedSongs) {
		this.mShowList = showList;
		this.mUnselectedSongs= unselectedSongs;
	}
	
	public ShowList getShowList() {
		return (ShowList) this.mShowList.clone();
	}
	
	public List<Song> getUnselectedSongs() {
		//return this.mUnselectedSongs.clone();
		return new LinkedList<Song>(mUnselectedSongs);
	}
}
public class MainApp {
	private static List<Song> songList;
	private static ShowList showList;
	// TODO: Calculate this number
	private static int maxSongsPerShow = 5;
	private static int numSongsLeftToSelect;
	private static boolean bGetCovers = false;
	private static long numShowsTested = 0;
	
	private static boolean getListsFromInternet(boolean bGetCovers) {
		showList = new ShowList();
		songList = GetSongList.getSongList(bGetCovers);
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
	private static boolean chooseShow(final int numShowsToSelect, final int numShowsSelected) {
		boolean rval = false;
		numShowsTested++;
		
		// Check if we can exit early
		if (numShowsToSelect * maxSongsPerShow < numSongsLeftToSelect) {
		//if (numShowsToSelect == 0) {
			//System.out.println("Early exit - Need to select " + numSongsLeftToSelect + " songs in only " + numShowsToSelect + " shows");
			return false;
		}
		
		Song nextUnplayedSong = SongUtils.getNextUnplayedSong(songList, numShowsSelected);
		if (nextUnplayedSong == null) {
			// The "we're done" return
			for (Show show : showList) {
				if (show.isSelected()) {
					System.out.println(show.getURL());
				}
			}
			return true;
		} else {
			List<Show> currentShowList = nextUnplayedSong.getShowList();
			
			for (Show currentShow : currentShowList) {
				int numSongsAdded = currentShow.select();
				
				numSongsLeftToSelect -= numSongsAdded;
				
				if (	numSongsLeftToSelect == 0
						|| chooseShow(numShowsToSelect - 1, numShowsSelected + 1)) {
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
				System.out.println(i + ". " + showDisplay.toString());
			}
		}
	}
	

	/**
	 * This is a breadth first search algorithm to try to find everything. By 
	 * the time it gets to 45 songs deep (3 times played) we're at a queue 
	 * length of about 1 million. I suspect we'll run out of RAM quickly with
	 * this scenario, unless it is optimized
	 * @param unselectedSongs A list of all the unselected songs to start
	 * @return
	 */
	public static ShowList doBreadthFirstSearch(List<Song> unselectedSongs) {
		ExecutionQueue<ExecutionObject> executionQueue = new ExecutionQueue<ExecutionObject>();
		ShowList currentShowList = null;
		
		executionQueue.enqueue(new ExecutionObject(new ShowList(), unselectedSongs));
		
		int maxDepth = -1;
		
		boolean bIsComplete = false;
		
		while (!bIsComplete) {
			// Pop the "song" off the queue
			ExecutionObject currentExecutionObject = executionQueue.dequeue();
			
			if (currentExecutionObject.mShowList.size() > maxDepth) {
				maxDepth = maxDepth + 1;
				System.out.println("Depth: " + maxDepth + " QueueLen = " + executionQueue.size());
			}
			
			Song currentSong = currentExecutionObject.mUnselectedSongs.get(0);
			
			for (Show currentShow:currentSong.getShowList()) {
				List<Song> newUnselectedSongList = currentExecutionObject.getUnselectedSongs();
				
				// Manually here, but optimize later
				for (Song tempSong:currentShow.getSongList()) {
					newUnselectedSongList.remove(tempSong);
				}
				
				currentShowList = currentExecutionObject.getShowList();
				currentShowList.add(currentShow);
				
				if (newUnselectedSongList.isEmpty()) {
					// We're done
					bIsComplete = true;
					currentShowList.print();
					break;
				} else {
					ExecutionObject nextExecutionObject = new ExecutionObject(currentShowList, newUnselectedSongList);
					executionQueue.enqueue(nextExecutionObject);
				}
			}
		}
		
		return currentShowList;
	}
	
	public static Integer songToInt(Song song, List<Song> songList) {
		return songList.indexOf(song);
	}
	
	public static Song intToSong(Integer songInt, List<Song> songList) {
		return songList.get(songInt);
	}
	
	public static Integer showToInt(Show show, ShowList showList) {
		return showList.indexOf(show);
	}
	
	public static Show intToShow(Integer showInt, ShowList showList) {
		return showList.get(showInt);
	}
	
	public static ExecutionObject2 doBFS_2(List<Song> songList) {
		// Create the dumbed-down list of songs
		HashMap<Integer, List<Integer>> songHashMap = new HashMap<>();
		Integer songListSize = songList.size();
		List<Integer> songListByTimesPlayed = new ArrayList(songListSize);
		for (Song song : songList) {
			List<Integer> songsShowList = new LinkedList<Integer>();
			for (Show show : song.getShowList()) {
				songsShowList.add(showToInt(show, showList));
			}
			Integer songInt = songToInt(song, songList);
			songHashMap.put(songInt, songsShowList);
			songListByTimesPlayed.add(songInt);
		}
		
		// Create the dumbed-down list of shows
		HashMap<Integer, List<Integer>> showHashMap = new HashMap<>();
		for (Show show : showList) {
			List<Integer> showsSongList = new LinkedList<Integer>();
			for (Song song : show.getSongList()) {
				showsSongList.add(songToInt(song, songList));
			}
			showHashMap.put(showToInt(show, showList), showsSongList);
		}
		
		List<Integer> selectedShows = new LinkedList<>();
		boolean bIsComplete = false;
		
		ExecutionQueue executionQueue = new ExecutionQueue();
		
		ExecutionObject2 executionObject = new ExecutionObject2(selectedShows, songHashMap, -1);
		executionQueue.enqueue(executionObject);
		
		Integer maxDepth = -1;
		
		Long lastDepthTime = System.nanoTime();
		Long currentTime;
		
		while (!bIsComplete) {
			ExecutionObject2 currentExecutionObject = (ExecutionObject2) executionQueue.dequeue();
			
			Integer currentDepth = currentExecutionObject.selectedShows.size();
			if (currentDepth > maxDepth) {
				currentTime = System.nanoTime();
				System.out.println("Current Depth: " + currentDepth 
								+ " Queue Size: " + executionQueue.size() 
								+ " Songs Left: " + currentExecutionObject.unselectedSongs.size() 
								+ " This duration: " + ((currentTime - lastDepthTime) / 1000000000.0));
				maxDepth = currentDepth;
			}
			
			List<Integer> nextShowList = null;
			for (Integer nextUnselectedSong = currentExecutionObject.lastSongIndex; nextUnselectedSong < songListSize; nextUnselectedSong++) {
				if (currentExecutionObject.unselectedSongs.containsKey(nextUnselectedSong)) {
					nextShowList = currentExecutionObject.unselectedSongs.get(nextUnselectedSong);
					currentExecutionObject.lastSongIndex = nextUnselectedSong;
					break;
				}
			}
			if (nextShowList == null) {
				return currentExecutionObject;
			} else { 
				for (Integer showCounter : nextShowList) {
					ExecutionObject2 nextExecutionObject = new ExecutionObject2(currentExecutionObject);
					List<Integer> playedSongsList = showHashMap.get(showCounter);
					//List<Integer> playedSongsList;
					for (Integer songCounter : playedSongsList) {
						nextExecutionObject.unselectedSongs.remove(songCounter);
					}
					nextExecutionObject.selectedShows.add(showCounter);
					executionQueue.enqueue(nextExecutionObject);
				}
			}
		}
		
		return null;
	}
	
	private static void doRecursiveRoutine() {

		for (int numShowsToSelect = 1; numShowsToSelect < songList.size(); numShowsToSelect++) {
			
			long startTime = System.nanoTime();
			
			if (chooseShow(numShowsToSelect, 0)) {
				// Success!
				System.out.println("Successfully got it with " + numShowsToSelect + " shows!");
				break;
			}
			
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			
			System.out.println("Tried with " + numShowsToSelect + " shows - took: " + (duration / 1000000000.0) + " seconds");
		}
		
	}
	
	private static int selectAllSinglePlayedSongs() {
		int numShowsSelected = 0;
		
		for (Song song : songList) {
			if (song.listSize() == 1) {
				Show currentShow = song.getShowList().get(0);
				if (!currentShow.isSelected()) {
					numSongsLeftToSelect -= currentShow.select();
					numShowsSelected++;
				}
			}
		}
		
		return numShowsSelected;
	}
	
	private static void pruneShowSetlists() {
		for (Show show : showList) {
			show.pruneSongList();
		}
	}
	
	private static void prunePlayedSongs() {
		Iterator<Song> songIter = songList.iterator();
		
		while(songIter.hasNext()) {
			Song currentSong = songIter.next();
			
			if (currentSong.isSelected()) {
				songIter.remove();
			}
		}
	}
	
	private static void doRecursiveRoutine2() {
		numShowsTested = 0;
		
		// Select all of the single-played shows
		// Select all songs covered by those shows
		int numShowsSelected = selectAllSinglePlayedSongs();
		
		// Remove all those songs from the show setlists
		pruneShowSetlists();
		
		prunePlayedSongs();
		
		songList.sort(null);
		
		printListSizes(songList, showList);
		
		long startTime = System.nanoTime();

		for (int numShowsToSelect = 1; numShowsToSelect < songList.size(); numShowsToSelect++) {
			
			long thisRunsStartTime = System.nanoTime();
			
			if (chooseShow(numShowsToSelect, numShowsSelected)) {
				// Success!
				System.out.println("Successfully got it with " + (numShowsToSelect + numShowsSelected) + " shows!");
				System.out.println(showList);
				break;
			}
			
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			double NS_PER_SEC = 1000000000.0;
			double seconds = duration / NS_PER_SEC;
			double thisRunsSeconds = (endTime - thisRunsStartTime) / NS_PER_SEC;
			
			System.out.println("Tried with " 
			+ (numShowsToSelect + numShowsSelected) 
			+ " shows - took: " 
			+ thisRunsSeconds
			+ " seconds at "
			+ (numShowsTested / seconds) 
			+ " tests per second");
		}
		
	}
	
	private static void printListSizes(List<Song> songList, ShowList showList) {
		System.out.println("Number of songs: " + songList.size());
		System.out.println("Number of shows: " + showList.size());
		
		int totalSongsPlayed = 0;
		for (Show show : showList) {
			totalSongsPlayed += show.getSongList().size();
		}
		System.out.println("Number of songs played: " + totalSongsPlayed);
		System.out.println("Average songs per show: " + totalSongsPlayed / showList.size());
		
		int numSongsInvestigated = 0;
		int numTimesPlayed = 1;
		while (numSongsInvestigated < songList.size()) {
			int numSongsPlayedThisManyTimes = 0;
			for (Song song : songList) {
				if (song.getShowList().size() == numTimesPlayed) {
					numSongsPlayedThisManyTimes++;
				}
			}
			if (numSongsPlayedThisManyTimes > 0) {
				System.out.println(numSongsPlayedThisManyTimes + " songs have been played " + numTimesPlayed + " times");
			}
			numSongsInvestigated += numSongsPlayedThisManyTimes;
			numTimesPlayed++;
		}
	}
	
	public static void main(String[] args) {
		//songList = new LinkedList<Song>();
		songList = new ArrayList<Song>(300);
		showList = new ShowList();
	    
		if (DataLoader.loadListsFromFiles(songList, showList, bGetCovers)) {
			System.out.println("Error reading from files. Pulling from the interwebs");
			if (getListsFromInternet(bGetCovers)) {
				System.out.println("Error getting info from files. Abort");
				return;
			} else {
				if (DataSaver.saveListsToFiles(songList, showList, bGetCovers)) {
					System.out.println("Error saving lists to files");
				}
			}
		}
		
		numSongsLeftToSelect = songList.size();
		// Make sure this doesn't count the unplayed songs
		for (Song currentSong : songList) {
			List<Show> currentShowList = currentSong.getShowList();
			if (	currentShowList == null ||
					currentShowList.size() < 1) {
				numSongsLeftToSelect--;
			}
		}
		
		//printListSizes(songList, showList);
		
		//doBFS_2(songList);
		
		//doBreadthFirstSearch(songList);
		
		//doGreedyRoutine();
		
		// This is the recursive algorithm. Takes too long!
		// Start with a single show. Loop until we have tried almost every show. Hopefully it'll be much less than that!
		//doRecursiveRoutine();
		
		//doRecursiveRoutine2();
		
		// CLF: Try to use the new technique
		MinimalProblem minimalProblem = new MinimalProblem(songList, showList);
		minimalProblem.sortSongsByTimesPlayed();
		minimalProblem.printSongLists();
		minimalProblem.sortShowsBySongList();
		minimalProblem.printShowLists();
		minimalProblem.selectSinglePlayedSongs();
	}
	
}
