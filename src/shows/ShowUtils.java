package shows;

import java.util.List;

public class ShowUtils {

	public static int getIndexOfShow(List<Show> showList, Show show) {
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
