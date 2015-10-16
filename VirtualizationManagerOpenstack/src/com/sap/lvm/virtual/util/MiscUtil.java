package com.sap.lvm.virtual.util;


import java.util.Collection;
import java.util.Map;


/** A convenient class for often needed code snippets.
 * 
 * @author d029751
 */
@SuppressWarnings("nls")
public class MiscUtil
{

	
	public static final String EMPTY_STRING = "";
	
	public final static boolean isPrimitive(String className) {
		return 	className.equals(String.class.getName()) 	||
				className.equals(Boolean.class.getName()) 	|| className.equals(boolean.class.getName()) ||
				className.equals(Byte.class.getName()) 		|| className.equals(byte.class.getName()) ||
				className.equals(Character.class.getName()) || className.equals(char.class.getName()) ||
				className.equals(Short.class.getName()) 	|| className.equals(short.class.getName()) ||
				className.equals(Integer.class.getName()) 	|| className.equals(int.class.getName()) ||
				className.equals(Long.class.getName()) 		|| className.equals(long.class.getName()) ||
				className.equals(Float.class.getName()) 	|| className.equals(float.class.getName()) ||
				className.equals(Double.class.getName())	|| className.equals(double.class.getName());
	}
	
	public final static int compareTo(String s1, String s2)
	{
		//$JL-STRING$ this is an optimization, so disable jlin warning
		if(s1==s2)
			return 0;
		else if(s1==null)
			return 1;
		else if(s2==null)
			return -1;
		
		return s1.compareTo(s2);
	}
	
	public final static int compareToIgnoreCase(String s1, String s2)
	{
		//$JL-STRING$ this is an optimization, so disable jlin warning
		if(s1==s2)
			return 0;
		else if(s1==null)
			return 1;
		else if(s2==null)
			return -1;
		
		return s1.compareToIgnoreCase(s2);
	}
	
	public final static boolean equals(Map<?, ?> h1, Map<?, ?> h2) 
	{
		if(h1==null && h2 == null)
			return true;
		if(h1==null || h2 == null)
			return false;
		if (h1.size()!=h2.size())
			return false;
		for (Object key : h1.keySet()) {
			if (!h2.containsKey(key))
				return false;
			if (h1.get(key)==null)
				return h2.get(key)==null;
			if (!h1.get(key).equals(h2.get(key)))
				return false;
		}
		return true;
	}
	
	public final static boolean equalsIgnoreNullOrEmpty(Object obj1, Object obj2) 
	{
		boolean obj1Empty=obj1==null;
		if(!obj1Empty && obj1 instanceof String )
			obj1Empty = nullOrEmpty((String)obj1);
		
		boolean obj2Empty=obj2==null;
		if(!obj2Empty && obj2 instanceof String )
			obj2Empty = nullOrEmpty((String)obj2);
		if(obj2Empty && obj1Empty)
			return true;
		return obj1!=null && obj1.equals(obj2);
	}
	
	public final static boolean equalsIgnoreNullOrEmpty(Map<?, ?> h1, Map<?, ?> h2) 
	{
		if(MiscUtil.nullOrEmpty(h1)&&MiscUtil.nullOrEmpty(h2))
			return true;
		if(h1==null || h2 == null)
			return false;
		/*can not do this because of null or empty elements
		if (h1.size()!=h2.size())
			return false;
			*/
		Map<?, ?> map = h1;
		if(h2.size()>h1.size())
			map = h2;
		
		for (Object key : map.keySet()) 
		{
			if (!MiscUtil.equalsIgnoreNullOrEmpty(h1.get(key),(h2.get(key))))
				return false;
		}
		return true;
	}
	
	public final static boolean equals(String s1, String s2, boolean ignoreCase)
	{
		return ignoreCase? equalsIgnoreCase(s1, s2): equals(s1, s2);
	}
	
	public final static String trim(String s)
	{
		return s!=null?s.trim():s;
	}
	
	/**
	 * Avoids NPEs.
	 * Works in case s1 or s2 is null.
	 * null is not equal to any not null string and null equals null.
	 * 
	 * @param s1 might be null
	 * @param s2 might be null
	 * 
	 * @return s1.equals.s2 in case both are not null
	 */
	public final static boolean equals(String s1, String s2)
	{
		//$JL-STRING$ this is an optimization, so disable jlin warning
		if(s1==s2)
			return true;
		else if(s1==null || s2==null)
			return false;
		
		return s1.equals(s2);
	}
	
	public final static boolean equalsTrim(String s1, String s2)
	{
		//$JL-STRING$ this is an optimization, so disable jlin warning
		if(s1==s2)
			return true;
		else if(s1==null || s2==null)
			return false;
		
		return s1.trim().equals(s2.trim());
	}
	
	public final static boolean equalsIgnoreCase(String s1, String s2)
	{
		//$JL-STRING$ this is an optimization, so disable jlin warning
		if(s1==s2)
			return true;
		else if(s1==null || s2==null)
			return false;
		
		return s1.equalsIgnoreCase(s2);
	}	
	
	public final static boolean equalsIgnoreCaseTrim(String s1, String s2)
	{
		//$JL-STRING$ this is an optimization, so disable jlin warning
		if(s1==s2)
			return true;
		else if(s1==null || s2==null)
			return false;
		
		return s1.trim().equalsIgnoreCase(s2.trim());
	}	


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
	
    /**
     * (S is null) OR ( S is empty)
     *  
     *  Empty in the sense of only white space content.
     *  
     * @param s 
     * 
     * @return ((s==null)) &or; (s.isEmpty())
     */
	public final static boolean nullOrEmpty(String s) 
	{
		return !notNullAndEmpty(s);
	}	
	
	public final static boolean notNullAndEmpty(Collection<?> coll)
	{
		return coll!=null && !coll.isEmpty();
	}
	
	public final static boolean nullOrEmpty(Collection<?> coll)
	{
		return coll==null || coll.isEmpty();
	}
	
	public final static boolean notNullAndEmpty(Map<?,?> coll)
	{
		return coll!=null && !coll.isEmpty();
	}
	
	public final static boolean nullOrEmpty(Map<?,?> coll)
	{
		return coll==null || coll.isEmpty();
	}
	
	public final static boolean isSameInteger(String s1, String s2)
	{
		try
		{
			return Integer.parseInt(s1)==Integer.parseInt(s2);
		}
		catch(Exception e)
		{
			// $JL-EXC$ disable exception caught
			return false;
		}
	}
	
}
