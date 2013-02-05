package org.webinos.api.mediacontent;

/**
 * Description: Filter match flag. The following values are supported:
 * "EXACTLY"- Indicates the attribute value should match exactly the given value
 * (default). For strings, the match is case-sensitive. 
 * 
 * "FULLSTRING" -
 * String-based matching. Matches the whole string (case insensitive).
 * 
 * "CONTAINS" - Indicates the attribute value should contain the given string
 * (strings only - case insensitive). 
 * 
 * "STARTSWITH" - Indicates the attribute
 * value should start with the given string (strings only - case insensitive).
 * 
 * "ENDSWITH" - Indicates the attribute value should end with the given string
 * (strings only - case insensitive). 
 * 
 * "EXISTS" - Indicates the filter should
 * match if the attribute exists.
 * 
 * @author marius
 * 
 */
public enum FilterMatchFlag {
	EXACTLY, FULLSTRING, CONTAINS, STARTSWITH, ENDSWITH, EXISTS;
}
