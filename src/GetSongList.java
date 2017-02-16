import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetSongList {
	static String site = "http://www.phish.net/songs";
	
	public static void main(String[] args) {
		try {
			Document document = Jsoup.connect(site).get();
			
			Elements elements = document.select(".originals :first-of-type a");
			
 			System.out.println("Number of elements: " + elements.size());
			
			for (Element element : elements) {
				//List<DataNode> dataNodes = element.dataNodes();
				//element.text();
				//if (dataNodes.size() > 0) {
				//	DataNode node = dataNodes.get(0);
				//	System.out.println("node: " + node.toString());
				//}
				//System.out.println("attr: " + element.attr("a"));
				//System.out.println("data: " + element.data());
				System.out.println("Found song: \"" + element.text() + "\" link: \"" + element.baseUri() + element.attr("href") + "\"");
			}
		} catch (IOException e) {
			System.out.println("Exception thrown");
			e.printStackTrace();
		}
	}

}
