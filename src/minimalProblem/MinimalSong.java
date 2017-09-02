package minimalProblem;

import java.util.ArrayList;
import java.util.List;

public class MinimalSong extends MinimalList {
	public MinimalSong(Integer index, Integer showListSize) {
		super(index, showListSize);
	}
	
	public void addShow(Integer showIndex) {
		super.add(showIndex);
	}
}
