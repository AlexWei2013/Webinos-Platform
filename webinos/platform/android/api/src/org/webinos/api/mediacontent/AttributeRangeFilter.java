package org.webinos.api.mediacontent;

/**
 * Description: Filter which matches objects containing an attribute whose values are within a particular
 * range. Range filters where only one boundary is set MUST be supported.
 * 
 * @author marius
 * 
 */
public abstract class AttributeRangeFilter implements AbstractFilter {

	public String attributeName;
	public Object initialValue;
	public Object endValue;

	/**
	 * 
	 * @param attributeName
	 *            - The name of the object attribute used for filtering. This is the name of the object
	 *            attribute exactly as it is defined in the object's interface. For attributes of complex
	 *            type, use fully-qualified names (such as 'organizations.role' to filter on a contact's role
	 *            in an organization). For attributes of array type, the filter will match if any value in the
	 *            array matches.
	 */
	public AttributeRangeFilter(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * 
	 * @param attributeName
	 *            - The name of the object attribute used for filtering. This is the name of the object
	 *            attribute exactly as it is defined in the object's interface. For attributes of complex
	 *            type, use fully-qualified names (such as 'organizations.role' to filter on a contact's role
	 *            in an organization). For attributes of array type, the filter will match if any value in the
	 *            array matches.
	 * 
	 * @param initialValue
	 *            - Objects whose attribute is greater than or equal to initialValue will match. By default,
	 *            this attribute is set to null.
	 * 
	 * @param endValue
	 *            - Objects whose attribute is strictly lower than to endValue will match. By default, this
	 *            attribute is set to null.
	 */
	public AttributeRangeFilter(String attributeName, Object initialValue, Object endValue) {
		this.attributeName = attributeName;
		this.initialValue = initialValue;
		this.endValue = endValue;
	}

}
