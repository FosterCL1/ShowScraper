package minimalProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import shows.Show;
import shows.ShowList;
import songs.Song;

public class MinimalProblem {
	private MinimalShow minimalShowList[];
	private MinimalSong minimalSongList[];
	
	// Populate
	public MinimalProblem(List<Song> songList, ShowList showList) {
		//minimalShowList = new ArrayList<>(showList.size());
		minimalShowList = new MinimalShow[showList.size()];
		minimalSongList = new MinimalSong[songList.size()];
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
	}
	
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
	
	// Sort by array list size (times played or number of songs in a show
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
	
	// Select single-selected songs
	// Sort by array list entries
	// Remove duplicate shows
}
