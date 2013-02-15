package org.webinos.api.mediacontent;

/**
 * Description: AttributeFilter represents a filter based on an object attribute. If no matchValue is defined,
 * the filter will match all objects that have the attribute defined (same as the "EXISTS" filter works),
 * otherwise, it will only match objects whose attribute matches the given value.
 * 
 * @author marius
 * 
 */
public abstract class AttributeFilter extends AbstractFilter {

	/**
	 * The name of the object attribute used for filtering. This is the name of the object attribute exactly
	 * as it is defined in the object's interface. For attributes of complex type, use fully-qualified names
	 * (such as 'organizations.role' to filter on a contact's role in an organization). For attributes of
	 * array type, the filter will match if any value in the array matches.
	 */
	public String attributeName;

	/**
	 * The match flag used for attribute-based filtering.
	 */
	public FilterMatchFlag matchFlag;

	/**
	 * The value used for matching. The filter will match if the attribute value matches the given matchValue.
	 * This value is not used if the matchFlag is set to "EXISTS". By default, this attribute is set to null.
	 */
	public Object matchValue;

	/**
	 * 
	 * @param attributeName
	 *            - The name of the object attribute used for filtering. This is the name of the object
	 *            attribute exactly as it is defined in the object's interface. For attributes of complex
	 *            type, use fully-qualified names (such as 'organizations.role' to filter on a contact's role
	 *            in an organization). For attributes of array type, the filter will match if any value in the
	 *            array matches.
	 */
	public AttributeFilter(String attributeName) {
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
	 * @param matchFlag
	 *            - The match flag used for attribute-based filtering.
	 * 
	 */
	public AttributeFilter(String attributeName, FilterMatchFlag matchFlag) {
		this.attributeName = attributeName;
		this.matchFlag = matchFlag;
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
	 * 
	 * @param matchValue
	 *            - The value used for matching. The filter will match if the attribute value matches the
	 *            given matchValue. This value is not used if the matchFlag is set to "EXISTS". By default,
	 *            this attribute is set to null.
	 */
	public AttributeFilter(String attributeName, Object matchValue) {
		this.attributeName = attributeName;
		this.matchValue = matchValue;
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
	 * @param matchFlag
	 *            - The match flag used for attribute-based filtering.
	 * 
	 * @param matchValue
	 *            - The value used for matching. The filter will match if the attribute value matches the
	 *            given matchValue. This value is not used if the matchFlag is set to "EXISTS". By default,
	 *            this attribute is set to null.
	 */
	public AttributeFilter(String attributeName, FilterMatchFlag matchFlag, Object matchValue) {
		this.attributeName = attributeName;
		this.matchFlag = matchFlag;
		this.matchValue = matchValue;
	}
}
