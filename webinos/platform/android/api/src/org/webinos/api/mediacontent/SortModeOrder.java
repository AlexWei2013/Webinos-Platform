package org.webinos.api.mediacontent;

/**
 * Description: Sort order. The following values are supported: "ASC" - Indicates the sorting order is ascending "DESC" - Indicates the sorting order is descending
 * 
 * @author marius
 * 
 */
public enum SortModeOrder {
	ASC(0), DESC(1);
	private int index;

	private SortModeOrder(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		switch (this) {
		case ASC:
			return "ASC";
		case DESC:
			return "DESC";
		default:
			throw new IllegalStateException("Not a predefined value of SortModeOrder enum");
		}
	}

	public int toInteger() {
		// TODO Auto-generated method stub
		switch (this) {
		case ASC:
			return 0;
		case DESC:
			return 1;
		default:
			throw new IllegalStateException("Not a predefined value of SortModeOrder enum");
		}
	}

}
