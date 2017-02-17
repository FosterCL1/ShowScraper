package scraper;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import shows.Show;
import shows.ShowList;
import shows.ShowUtils;
import songs.Song;
import songs.SongUtils;

import com.opencsv.CSVReader;

public class DataLoader extends DataPaths {
	public static boolean loadListsFromFiles(List<Song> songList, ShowList showList) {
		boolean rval = false;
		
		//songList = new LinkedList<Song>();
		//showList = new ShowList();
	    
		try {
			// Read the song list
			CSVReader reader = new CSVReader(new FileReader(songListFile));
			String [] nextLine;
			while ((nextLine = reader.readNext()) != null) {
			    // nextLine[] is an array of values from the line
				Song currentSong = new Song(nextLine[1], nextLine[2]);
				songList.add(currentSong);
				
				// Sanity check:
				int songIndex = SongUtils.getIndexOfSongByName(songList, currentSong);
				if (songIndex != Integer.parseInt(nextLine[0])) {
					System.out.println("Song item is not inserted at the right index");
					throw new Exception();
				}
			}
			reader.close();
			
			// Read the show list
			CSVReader showReader = new CSVReader(new FileReader(showListFile));
			while ((nextLine = showReader.readNext()) != null) {
				Show currentShow = new Show(nextLine[1]);
				showList.add(currentShow);
				
				int showIndex = ShowUtils.getIndexOfShow(showList, currentShow);
				if (showIndex != Integer.parseInt(nextLine[0])) {
					System.out.println("Show item is not inserted at the right index");
					throw new Exception();
				}
			}
			showReader.close();
			
			//TODO: This theoretically can use either reader or both. I'll just use one for now.
			// Read the show / song link
			CSVReader showsPerSongReader = new CSVReader(new FileReader(showsPerSongFile));
			while ((nextLine = showsPerSongReader.readNext()) != null) {
				int songIndex = Integer.parseInt(nextLine[0]);
				int showIndex = Integer.parseInt(nextLine[1]);
				
				Song currentSong = songList.get(songIndex);
				Show currentShow = showList.get(showIndex);
				
				currentSong.addShow(currentShow);
				currentShow.addSong(currentSong);
			}
			showsPerSongReader.close();
			
			
		} catch (Exception e) {
			System.out.println("Error reading files");
			e.printStackTrace();
			rval = true;
		}
		return rval;
	}
}
