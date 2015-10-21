package com.sap.lvm.virtual.util;


@SuppressWarnings("nls")
public class MiscUtil
{

	
	public static final String EMPTY_STRING = "";


        /**
	 * (S is not null) AND ( S is not empty)
	 *  
	 *  Empty in the sense of not white space content.
	 *  
	 * @param s 
	 * 
	 * @return (!(s==null)) &and; (!s.isEmpty())
	 */
    public final static boolean notNullAndEmpty(String s)
    {
		if (s==null)
			return false;
		if (s.trim().length()<=0)
			return false;
		return true;
	}	
	
	
}
