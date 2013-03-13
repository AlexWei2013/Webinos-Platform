package org.webinos.api.mediacontent;

/**
 * Description: Filter match flag. The following values are supported: "EXACTLY"- Indicates the attribute value should match exactly the given value (default). For strings, the
 * match is case-sensitive.
 * 
 * "FULLSTRING" - String-based matching. Matches the whole string (case insensitive).
 * 
 * "CONTAINS" - Indicates the attribute value should contain the given string (strings only - case insensitive).
 * 
 * "STARTSWITH" - Indicates the attribute value should start with the given string (strings only - case insensitive).
 * 
 * "ENDSWITH" - Indicates the attribute value should end with the given string (strings only - case insensitive).
 * 
 * "EXISTS" - Indicates the filter should match if the attribute exists.
 * 
 * @author marius
 * 
 */
public enum FilterMatchFlag {
	EXACTLY(0), FULLSTRING(1), CONTAINS(2), STARTSWITH(3), ENDSWITH(4), EXISTS(5);

	private int index;

	private FilterMatchFlag(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		switch (this) {
		case EXACTLY:
			return "EXACTLY";
		case FULLSTRING:
			return "FULLSTRING";
		case CONTAINS:
			return "CONTAINS";
		case STARTSWITH:
			return "STARTSWITH";
		case ENDSWITH:
			return "ENDSWITH";
		case EXISTS:
			return "ENDSWITH";
		default:
			throw new IllegalStateException("Not a predefined value of FilterMatchFlag enum!.");
		}
	}

	public int toInteger() {
		// TODO Auto-generated method stub
		switch (this) {
		case EXACTLY:
			return 0;
		case FULLSTRING:
			return 1;
		case CONTAINS:
			return 2;
		case STARTSWITH:
			return 3;
		case ENDSWITH:
			return 4;
		case EXISTS:
			return 5;
		default:
			throw new IllegalStateException("Not a predefined value of FilterMatchFlag enum!.");
		}
	}

}
