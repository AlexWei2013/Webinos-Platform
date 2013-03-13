package org.webinos.api.mediacontent;

/**
 * Description: represents a set of filters. The composite filters can be one of
 * the 2 types: The union - used to filter objects that matches more than one
 * filter it includes. The intersection - used to filter objects that matches
 * all filters it includes.
 * 
 * @author marius
 * 
 */
public abstract class CompositeFilter extends AbstractFilter {

	public String CompositeFilterType;
	public AbstractFilter[] filters;

	/**
	 * 
	 * @param compositeFilterType
	 *            - the composite filter type;
	 * @param filters
	 *            - the list of filters in the composite filter.
	 */
	public CompositeFilter(String compositeFilterType, AbstractFilter[] filters) {
		this.CompositeFilterType = compositeFilterType;
		this.filters = filters;
	}
}
