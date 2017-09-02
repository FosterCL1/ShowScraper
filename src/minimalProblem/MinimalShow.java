package minimalProblem;

import java.util.List;

public class MinimalShow extends MinimalList{
	public MinimalShow(Integer index, Integer showListSize) {
		super(index, showListSize);
	}
	
	public void addSong(Integer songIndex) {
		super.add(songIndex);
	}
}
