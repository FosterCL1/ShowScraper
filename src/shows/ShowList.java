package shows;

import java.util.LinkedList;

public class ShowList extends LinkedList<Show> {
	public boolean contains (Show show) {
		for (Show testShow : this) {
			if (testShow.getURL().equals(show.getURL())) {
				return true;
			}
		}
		return false;
	}
}
