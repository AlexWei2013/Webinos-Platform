package org.webinos.api.mediacontent;

import org.meshpoint.anode.idl.Dictionary;

/**
 * Description: SortMode is a common interface used for sorting of queried data. Note that the sorting result
 * of list type attributes is not determined.
 * 
 * @author marius
 * 
 */
public class SortMode implements Dictionary {
	/**
	 * The name of the object attribute used for sorting.
	 */
	public String attributeName;

	/**
	 * The type of the sorting. By default, this attribute is set to "ASC".
	 */
	public SortModeOrder order = SortModeOrder.ASC;

//	/**
//	 * 
//	 * @param attributeName
//	 *            - The name of the object attribute used for sorting.
//	 */
//	public SortMode(String attributeName) {
//		this.attributeName = attributeName;
//		this.order = SortModeOrder.ASC;
//	}

//	/**
//	 * 
//	 * @param attributeName
//	 *            - The name of the object attribute used for sorting.
//	 * @param order
//	 *            - The type of the sorting.
//	 */
//	public SortMode(String attributeName, SortModeOrder order) {
//		this.attributeName = attributeName;
//		this.order = order;
//	}
}