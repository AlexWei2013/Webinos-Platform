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
	UNION, INTERSECTION;
}
