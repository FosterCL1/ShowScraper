package minimalProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import shows.Show;
import shows.ShowList;
import songs.Song;

public class MinimalProblem {
	private MinimalShow minimalShowList[];
	private MinimalSong minimalSongList[];
	private ArrayList<MinimalSong> unselectedSongList;
	private ArrayList<MinimalShow> unselectedShowList;
	private LinkedHashSet<Integer> selectedShows;
	private LinkedList<MinimalSong> singlePlayedSongsList;
	private Set<MinimalShow> showsToRemove;
	private Set<MinimalSong> songsToRemove;
	
	// Populate
	public MinimalProblem(List<Song> songList, ShowList showList) {
		//minimalShowList = new ArrayList<>(showList.size());
		minimalShowList = new MinimalShow[showList.size()];
		minimalSongList = new MinimalSong[songList.size()];
		selectedShows = new LinkedHashSet<>();
		
		for (Song song : songList) {
			List<Show> currentShowList = song.getShowList();
			Integer currentSongIndex = songList.indexOf(song);
			//MinimalSong currentMinimalSong = minimalSongList.get(currentSongIndex);
			minimalSongList[currentSongIndex] = new MinimalSong(currentSongIndex, currentShowList.size());
			MinimalSong currentMinimalSong = minimalSongList[currentSongIndex];
			
			for (Show show : currentShowList) {
				Integer currentShowIndex = showList.indexOf(show);
				currentMinimalSong.addShow(currentShowIndex);
				//MinimalShow currentShow = minimalShowList.get(currentShowIndex);
				if (minimalShowList[currentShowIndex] == null) {
					minimalShowList[currentShowIndex] = new MinimalShow(currentShowIndex, show.getSongList().size());
				}
				minimalShowList[currentShowIndex].addSong(currentSongIndex);
			}
		}
		
		unselectedSongList = new ArrayList(Arrays.asList(minimalSongList));
		unselectedShowList = new ArrayList(Arrays.asList(minimalShowList));
		
		System.out.println("Original Song List: " + minimalSongList.length);
		System.out.println("Original Show List: " + minimalShowList.length);
	}
	
	// Helpers
	public void printSongLists() {
		printLists(minimalSongList);
	}
	
	public void printShowLists() {
		printLists(minimalShowList);
	}
	
	private void printLists(MinimalList list[]) {
		for (MinimalList minimalList : list) {
			System.out.println(minimalList);
		}
	}
	
	// Sort by array list size (times played or number of songs in a show)
	Comparator listSizeComparator = new Comparator<MinimalList>() {
		public int compare(MinimalList list1, MinimalList list2) {
			Integer list1size = list1.listSize();
			Integer list2size = list2.listSize();
			if (list1size < list2size) {
				return -1;
			} else if (list1size == list2size) {
				return 0;
			} else {
				return 1;
			}
		}
	};

	// Sort by item index
	Comparator listIndexComparator = new Comparator<MinimalList>() {
		public int compare(MinimalList list1, MinimalList list2) {
			Integer list1index = list1.index;
			Integer list2index = list2.index;
			if (list1index < list2index) {
				return -1;
			} else if (list1index == list2index) {
				return 0;
			} else {
				return 1;
			}
		}
	};
	
	// Sort the items by the list contents. e.g. [2, 3, 5] < [2, 3, 4] < [2, 3] 
	Comparator listItemsComparator = new Comparator<MinimalList>() {
		public int compare(MinimalList list1, MinimalList list2) {
			List<Integer> sortedList1 = list1.getSortedList();
			List<Integer> sortedList2 = list2.getSortedList();
			Iterator<Integer> list1Iterator = sortedList1.iterator();
			Iterator<Integer> list2Iterator = sortedList2.iterator();
			
			while (list1Iterator.hasNext() && list2Iterator.hasNext()) {
				Integer list1Item = list1Iterator.next();
				Integer list2Item = list2Iterator.next();
				
				if (list1Item < list2Item) {
					return -1;
				} else if (list1Item > list2Item) {
					return 1;
				}
			}
			
			if (list1Iterator.hasNext()) {
				return 1;
			} else if (list2Iterator.hasNext()) {
				return -1;
			} else {
				return 0;
			}
		}
	};
	
	public void sortSongsByTimesPlayed() {
		Arrays.sort(minimalSongList, listSizeComparator); 
	}
	
	public void sortShowsByNumSongs() {
		Arrays.sort(minimalShowList, listSizeComparator);
	}
	
	public void sortShowsBySongList() {
		Arrays.sort(minimalShowList, listItemsComparator);
	}
	
	public void sortShowsByIndex() {
		Arrays.sort(minimalShowList, listIndexComparator);
	}
	
	// Select single-played songs
	public boolean selectSinglePlayedSongs() {
		boolean rval = false;
		LinkedList<Integer> songIndicesToRemove = new LinkedList<>();
		
		Collections.sort(unselectedSongList, listIndexComparator);
		sortShowsByIndex();
		
		Iterator<MinimalSong> singlePlayedSongsIter = unselectedSongList.iterator();
		
		boolean bKeepGoing = true;
		
		while (bKeepGoing && singlePlayedSongsIter.hasNext()) {
			MinimalSong currentSinglePlayedSong = singlePlayedSongsIter.next();
			if (currentSinglePlayedSong.listSize() > 1) {
				bKeepGoing = false;
			} else {
				System.out.println("Song " + currentSinglePlayedSong.index + " has only been played once in show " + currentSinglePlayedSong.index + " ");
				List<Integer> currentShowList = currentSinglePlayedSong.getSortedList();
				for (Integer currentShow : currentShowList) {
					selectedShows.add(currentShow);
					System.out.print("This will remove songs ");
					for (Integer thisShowsSongs : minimalShowList[currentShow].getSortedList()) {
						if (!songIndicesToRemove.contains(thisShowsSongs)) {
							songIndicesToRemove.add(thisShowsSongs);
							System.out.print(thisShowsSongs + " ");
						}
					}
					System.out.print("\n");
				}
			}
		}
		
		Collections.sort(unselectedSongList, listIndexComparator);
		
		int unselectedSongCounter = 0;
		Iterator<Integer> songsToRemoveIter = songIndicesToRemove.iterator();
		
		System.out.print("Num unselected songs was: " + unselectedSongList.size());
		
		while (songsToRemoveIter.hasNext()) {
			Integer songToRemove = songsToRemoveIter.next();
			
			Iterator<MinimalSong> unselectedSongsIter = unselectedSongList.iterator();
			boolean bFound = false;
			
			while (!bFound && unselectedSongsIter.hasNext()) {
				MinimalSong testSong = unselectedSongsIter.next();
				if (songToRemove == testSong.index) {
					unselectedSongsIter.remove();
					bFound = true;
				}
			}
		}
		
		System.out.print(", is now: " + unselectedSongList.size());
		
		int totalSongsPlayedBeforePruning = 0; 
		int totalSongsPlayedAfterPruning = 0; 
		
		// Remove the songs from each show
		for (int i = 0; i < minimalShowList.length; i++) {
			Iterator<Integer> songIter = minimalShowList[i].getSortedList().iterator();
			
			totalSongsPlayedBeforePruning += minimalShowList[i].listSize();
			
			while (songIter.hasNext())
			{
				Integer currentSong = songIter.next();
				Iterator<MinimalSong> unselectedSongsIter = unselectedSongList.iterator();
				boolean bFound = false;
				while (!bFound && unselectedSongsIter.hasNext()) {
					MinimalSong unselectedSongForRemoval = unselectedSongsIter.next();
					if (currentSong.intValue() == unselectedSongForRemoval.index.intValue()) {
						System.out.println("Removing song " + currentSong + " from show " + i);
						songIter.remove();
						bFound = true;
					}
				}
			}
			
			totalSongsPlayedAfterPruning += minimalShowList[i].listSize();
		}
		
		float averageShowSizeBefore = totalSongsPlayedBeforePruning / minimalShowList.length;
		float averageShowSizeAfter = totalSongsPlayedAfterPruning / minimalShowList.length;
		
		System.out.println("Average show size before: " + averageShowSizeBefore + " after: " + averageShowSizeAfter);
		
		return (songIndicesToRemove.size() > 0);
	}
	
	public boolean step1_sortSongsByTimesPlayed(boolean verbose) {
		Collections.sort(unselectedSongList, listSizeComparator);
		System.out.println("Unselected Song List Length: " + unselectedSongList.size());
		
		if (verbose) {
			for (MinimalSong dummySong : unselectedSongList) {
				System.out.println(dummySong);
			}
		}
		
		return (unselectedSongList.size() > 0 && unselectedSongList.get(0).listSize() == 1);
	}
	
	public void step2_createSinglePlayedSongsList(boolean verbose) {
		singlePlayedSongsList = new LinkedList();
		Iterator<MinimalSong> unselectedSongsIter = unselectedSongList.iterator();
		
		boolean bExit = false;
		
		while (unselectedSongsIter.hasNext() && !bExit) {
			MinimalSong currentSong = unselectedSongsIter.next();
			if ( currentSong.listSize() > 1 ) {
				bExit = true;
			} else {
				singlePlayedSongsList.add(currentSong);
			}
		}
		
		System.out.println("Single played songs list length: " + singlePlayedSongsList.size());
		
		if (verbose) {
			for (MinimalSong dummySong : singlePlayedSongsList) {
				System.out.println(dummySong);
			}
		}
	}
	
	public void step3_createSetOfShowsToRemove(boolean verbose) {
		showsToRemove = new LinkedHashSet<MinimalShow>();
		
		Iterator<MinimalSong> singlePlayedSongsIter = singlePlayedSongsList.iterator();
		
		while (singlePlayedSongsIter.hasNext()) {
			MinimalSong currentSong = singlePlayedSongsIter.next();
			
			List<Integer> currentSongsShowList = currentSong.getSortedList();
			
			for (Integer currentShowIndex : currentSongsShowList) {
				for (MinimalShow dummyShow : unselectedShowList) {
					if (dummyShow.index.equals(currentShowIndex)) {
						showsToRemove.add(dummyShow);
						selectedShows.add(dummyShow.index);
					}
				}
//				MinimalShow currentShow = minimalShowList[currentShowIndex];
//				showsToRemove.add(currentShow);
			}
		}
		
		System.out.println("Removing " + showsToRemove.size() + " shows");
		
		if (verbose) {
			for (MinimalShow tempShow : showsToRemove) {
				System.out.println(tempShow);
			}
		}
	}
	
	public void step4_createSetOfAllSongsToRemove(boolean verbose) {
		songsToRemove = new LinkedHashSet<MinimalSong>();
		
		LinkedList<Integer> unplayedSongIndices = new LinkedList<>();
		
		for (MinimalSong dummySong : unselectedSongList) {
			unplayedSongIndices.add(dummySong.index);
		}
		
		Iterator<MinimalShow> showsToRemoveIter = showsToRemove.iterator();
		
		while (showsToRemoveIter.hasNext()) {
			MinimalShow currentShow = showsToRemoveIter.next();
			
			List<Integer> currentShowsSongList = currentShow.getSortedList();
			
			for (Integer currentSongIndex : currentShowsSongList) {
				// Make sure we only care about songs on the unplayed list
				if (unplayedSongIndices.contains(currentSongIndex)) {
					for (MinimalSong dummySong : unselectedSongList) {
						if (dummySong.index.equals(currentSongIndex)) {
							songsToRemove.add(dummySong);
						}
					}
//					MinimalSong currentSong = minimalSongList[currentSongIndex];
//					
//					songsToRemove.add(currentSong);
				}
			}
		}
		
		System.out.println("This will remove " + songsToRemove.size() + " songs");
		
		if (verbose) {
			for (MinimalSong tempSong : songsToRemove) {
				System.out.println(tempSong);
			}
		}
	}
	
	public void step5_removeSongsFromSongList(boolean verbose) {
		LinkedList<MinimalSong> newUnselectedSongList = new LinkedList<>();
		
		for (MinimalSong currentSong : unselectedSongList) {
			if (!songsToRemove.contains(currentSong)) {
				newUnselectedSongList.add(currentSong);
			}
		}
		
		//unselectedSongList = newUnselectedSongList.toArray(new MinimalSong[newUnselectedSongList.size()]);

		unselectedSongList = new ArrayList(newUnselectedSongList);
		
		System.out.println("New unselected song list is " + unselectedSongList.size());
		
		if (verbose) {
			for (MinimalSong dummySong : unselectedSongList) {
				System.out.println(dummySong);
			}
		}
	}
	
	public void step6_removeSongsFromShowLists(boolean verbose) {
		LinkedList<MinimalShow> newUnselectedShowList = new LinkedList<>();
		
		Set<Integer> indicesOfSongsToRemove = new LinkedHashSet<>();
		for (MinimalSong dummySong : songsToRemove) {
			indicesOfSongsToRemove.add(dummySong.index);
		}
		
		for (MinimalShow currentShow : unselectedShowList) {
			List<Integer> initialSongList = currentShow.getSortedList();
			
			Set<Integer> newShowsSongList = new LinkedHashSet<>();
			
			for (Integer currentSong : initialSongList) {
				if (!indicesOfSongsToRemove.contains(currentSong)) {
					newShowsSongList.add(currentSong);
				} else {
					if (verbose) {
						System.out.println("Not adding song " + currentSong + " to show " + currentShow.index);
					}
				}
			}
			
			if (newShowsSongList.size() > 0) {
				MinimalShow newShow = new MinimalShow(currentShow.index, newShowsSongList.size());
				for (Integer song : newShowsSongList) {
					newShow.addSong(song);
				}
				newUnselectedShowList.add(newShow);
			}
		}
		
		unselectedShowList = new ArrayList(newUnselectedShowList);
		
		System.out.println("New unselected show list is " + unselectedShowList.size());
	}
	
	public void step7_sortShowListBySongList(boolean verbose) {
		//Arrays.sort(unselectedShowList, listItemsComparator);
		unselectedShowList.sort(listItemsComparator);
		
		if (verbose) {
			for (MinimalShow currentShow : unselectedShowList) {
				System.out.println(currentShow);
			}
		}
	}
	
	public void step9_removeDuplicateShows(boolean verbose) {
		LinkedList<MinimalShow> newUnselectedShowList = new LinkedList<>();
		
		List<Integer> previousSongList = null;
		MinimalShow previousShow = new MinimalShow(-1, 0);
		
		for (MinimalShow currentShow : unselectedShowList) {
			List<Integer> currentSongList = currentShow.getSortedList();
			
			if (!currentSongList.equals(previousSongList)) {
				newUnselectedShowList.add(currentShow);
				previousSongList = currentSongList;
				previousShow = currentShow;
			} else {
				if (verbose) {
					System.out.println("Duplicate found with show " + previousShow.index + " and " + currentShow.index);
					System.out.println(previousShow);
					System.out.println(currentShow);
				}
				// Remove this from the song list as well
				step9b_removeShowReferencesFromSongList(currentShow, verbose);
			}
		}
		
		unselectedShowList = new ArrayList(newUnselectedShowList);
		System.out.println("After duplicates the show list is " + unselectedShowList.size());
		
		if (verbose) {
			unselectedShowList.sort(listSizeComparator);
			for (MinimalShow dummyShow : unselectedShowList) {
				System.out.println(dummyShow);
			}
		}
	}
	
	private void step9b_removeShowReferencesFromSongList(MinimalShow currentShow, boolean verbose) {
		LinkedList<MinimalSong> newUnselectedSongList = new LinkedList<>();
		//unselectedSongList.sort(listIndexComparator);
		
		for (MinimalSong currentSong : unselectedSongList) {
			List<Integer> currentShowsSongList = currentShow.getSortedList();
			if (currentShowsSongList.contains(currentSong.index)) {
				MinimalSong newSong = new MinimalSong(currentSong.index, currentSong.listSize()-1);
				List<Integer> originalShowList = currentSong.getSortedList();
				for (Integer showIndex : originalShowList) {
					if (!showIndex.equals(currentShow.index)) {
						newSong.addShow(showIndex);
					} else {
						if (verbose) {
							System.out.println("Removing show index " + showIndex + " from current song");
						}
					}
				}
				if (verbose) {
					System.out.println("Song " + newSong.index + " was played " + currentSong.listSize() + " times, but now has been played " + newSong.listSize() + " times");
					System.out.println(currentSong);
					System.out.println(newSong);
				}
				
				if (newSong.listSize().equals(1))
				{
					System.out.println("New single-played song for song " + currentSong.index);
				}
				newUnselectedSongList.add(newSong);
			} else {
				newUnselectedSongList.add(currentSong);
			}
		}
		
		unselectedSongList = new ArrayList(newUnselectedSongList);
	}
	
	public void step_9_2_removeSubsets(boolean verbose) {
		LinkedList<MinimalShow> newUnselectedShowList = new LinkedList<>();

		unselectedShowList.sort(listSizeComparator);
		
		Iterator<MinimalShow> showIterator = unselectedShowList.iterator();
		
		List<Integer> previousShowList = new ArrayList<>(1);
		previousShowList.add(-1);
		
		while (showIterator.hasNext()) {
			MinimalShow currentShow = showIterator.next();
			List<Integer> currentList = currentShow.getSortedList();
			
			boolean bFoundMatch = false;
			
			for (MinimalShow showTester : unselectedShowList) {
				List<Integer> testList = showTester.getSortedList();
				if (
						(testList.size() >= currentList.size()) && (currentShow.index != showTester.index))
				{
					if (testList.containsAll(currentList)) {
						if (verbose) {
							System.out.println("Show " + currentShow.index + " found to be a subset of show " + showTester.index);
							System.out.println(currentShow);
							System.out.println(showTester);
						}
						step9b_removeShowReferencesFromSongList(currentShow, verbose);
						bFoundMatch = true;
						break;
					}
				}
			}	
			if (!bFoundMatch) {
				newUnselectedShowList.add(currentShow);
			}
		}
		
		unselectedShowList = new ArrayList(newUnselectedShowList);
		System.out.println("After subsets the show list is " + unselectedShowList.size());
		
		if (verbose) {
			unselectedShowList.sort(listSizeComparator);
			for (MinimalShow dummyShow : unselectedShowList) {
				System.out.println(dummyShow);
			}
		}
	}
	
	public void printStatus(boolean verbose) {
		System.out.println("Unselected Shows: " + unselectedShowList.size());
		unselectedShowList.sort(listItemsComparator);
		for (MinimalShow currentShow : unselectedShowList) {
			System.out.println(currentShow);
		}
		
		System.out.println("Shows selected: " + selectedShows.size());
		for (Integer selectedShow : selectedShows) {
			System.out.print(selectedShow + ", ");
		}
		System.out.println("");
	}
	
	private boolean tryBruteForceRecursion(List<MinimalShow> existingShowList, 
			List<MinimalShow> selectedShowList,
			List<Integer> currentUnselectedSongList,
			int numRecursionsLeft, 
			boolean verbose) {
		boolean success = false;
		
		if (numRecursionsLeft == 0) {
			return false;
		} else if (7 * numRecursionsLeft < currentUnselectedSongList.size()) {
			return false;
		} else {
			// Start with the show with the most unplayed songs
			existingShowList.sort(listSizeComparator);
			
			for (int i = existingShowList.size(); i > 0; i--) {
				MinimalShow currentShow = existingShowList.get(i-1); 
				selectedShowList.add(currentShow);
				List<Integer> songsAddedList = currentShow.getSortedList();
				LinkedList<Integer> newUnselectedSongList = new LinkedList<>(currentUnselectedSongList);
				for (Integer currentSong : songsAddedList)
				{
					newUnselectedSongList.remove(currentSong);
				}
				if (newUnselectedSongList.size() == 0) {
					for (MinimalShow showCounter : selectedShowList) {
						System.out.println(showCounter);
					}
					return true;
				} else {
					if (newUnselectedSongList.size() < minHit) {
						minHit = newUnselectedSongList.size();
						System.out.println("New minimum: " + minHit);
					}
					LinkedList<MinimalShow> newExistingShowList = new LinkedList<>(existingShowList);
					newExistingShowList.remove(i-1);
					if (tryBruteForceRecursion(
							newExistingShowList,
							selectedShowList,
							newUnselectedSongList,
							numRecursionsLeft - 1,
							verbose)) {
						return true;
					} else {
						selectedShowList.remove(currentShow);
					}
				}
			}
		}
		
		return false;
	}
	
	public List<MinimalSong> getSongList() {
		return unselectedSongList;
	}
	
	public List<MinimalShow> getShowList() {
		return unselectedShowList;
	}
	
	public List<Integer> getSelectedShowList() {
		return new ArrayList(selectedShows);
	}
	
	private int minHit;
	
	public void step10_tryBruteForce(boolean verbose) {
		boolean bSolved = false;
		LinkedList<MinimalShow> selectedShowList = new LinkedList<>();
		LinkedList<Integer> unselectedSongIndices = new LinkedList<>();
		
		for (MinimalSong songCounter : unselectedSongList) {
			unselectedSongIndices.add(songCounter.index);
		}
		
		minHit = unselectedSongIndices.size();
		
		for (int i = 0; i < unselectedShowList.size() && !bSolved; i++) {
		//for (int i = 0; i < 3 && !bSolved; i++) {
			System.out.println("Trying " + i + " levels deep in recursion");
			bSolved = tryBruteForceRecursion(unselectedShowList, selectedShowList, unselectedSongIndices, i, verbose);
		}
	}
	
	// Sort by array list entries
	// Remove duplicate shows
}
