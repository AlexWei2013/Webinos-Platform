package org.webinos.api.mediacontent;

/**
 * Description: Composite filter type.
 * 
 * Values:
 * 
 * "UNION" - A union of filters ("OR" operator)
 * 
 * "INTERSECTION" - An intersection of filters ("AND" operator)
 * 
 * @author marius
 * 
 */
public enum CompositeFilterType {
	UNION(0), INTERSECTION(1);

	private int index;

	private CompositeFilterType(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub

		switch (this) {
		case UNION:
			return "UNION";
		case INTERSECTION:
			return "INTERSECTION";
		default:
			throw new IllegalStateException("Not a predefined value of CompositeFilterType enum!.");
		}
	}

	public int toInteger() {
		// TODO Auto-generated method stub

		switch (this) {
		case UNION:
			return 0;
		case INTERSECTION:
			return 1;
		default:
			throw new IllegalStateException("Not a predefined value of CompositeFilterType enum!.");
		}
	}

}
