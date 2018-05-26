package minimalProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class MinimalList {
	private List<Integer> thisList;
	protected Integer index;
	
	public MinimalList(Integer index, Integer showListSize) {
		this.index = index;
		this.createList(showListSize);
	}
	
	private final Integer DEFAULT_SHOW_LIST_SIZE = 256;
	
	protected void createList(Integer numShows)
	{
		thisList = new ArrayList<>(numShows);
	}
	
	protected void add(Integer itemNumber){
		if (thisList == null) {
			thisList = new ArrayList<>(DEFAULT_SHOW_LIST_SIZE);
		}
		if (!thisList.contains(itemNumber)) {
			if (!thisList.add(itemNumber)){
				System.out.println("Error adding show to a song!");
			}
		}
	}

	public boolean hasBeenInstantiated() {
		return (thisList == null); 
	}
	
	public Integer getIndex() {
		return index;
	}
	
	public Integer listSize() {
		return thisList.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(index + " (" + thisList.size() + "): \t");
		boolean bIsFirst = true;
		for (Integer item : thisList) {
			if (!bIsFirst) {
				sb.append(", ");
			}
			sb.append(item);
			bIsFirst = false;
		}
		return sb.toString();
	}
	
	private Comparator listItemComparator = new Comparator<Integer>() {
		public int compare(Integer int1, Integer int2) {
			if (int1 < int2) {
				return -1;
			} else if (int1 == int2) {
				return 0;
			} else {
				return 1;
			}
		}
	};
	
	public List<Integer> getSortedList() {
		Collections.sort(thisList, listItemComparator);
		return new ArrayList<Integer> (thisList);
	}
}
