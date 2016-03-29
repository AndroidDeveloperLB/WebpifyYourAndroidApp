package webpifier;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CollectionUtil
  {
  public static <T> boolean isEmpty(final T[] arr)
    {
    return arr==null||arr.length==0;
    }

  public static <T> boolean isEmpty(final Collection<T> collection)
    {
    return collection==null||collection.isEmpty();
    }

  public static <T,S> boolean isEmpty(final Map<T,S> map)
    {
    return map==null||map.isEmpty();
    }

  public static <T> int size(final Collection<T> collection)
    {
    return collection==null?0:collection.size();
    }

  public static <T,S> int size(final Map<T,S> map)
    {
    return map==null?0:map.size();
    }

  /**
   * returns true iff all of the items on each set exist on the other set .
   */
  public static <T> boolean areSetsIdentical(final Set<T> set1,final Set<T> set2)
    {
    // both are the same , so return true
    if(set1==set2)
      return true;
    // size is different so return false
    if(CollectionUtil.size(set1)!=CollectionUtil.size(set2))
      return false;
    // both are empty , so return true
    if(CollectionUtil.isEmpty(set1))
      return true;
    // size is the same , so compare items
    for(final T t : set1)
      if(!set2.contains(t))
        return false;
    return true;
    }

  public static String toString(final Object[] objects)
    {
    if(objects==null)
      return "null";
    final StringBuilder sb=new StringBuilder("{");
    boolean isFirst=true;
    for(final Object object : objects)
      {
      if(!isFirst)
        sb.append(',');
      else isFirst=false;
      sb.append(object);
      }
    sb.append('}');
    return sb.toString();
    }

  public static String toString(final Collection<?> collection)
    {
    if(collection==null)
      return "null";
    final StringBuilder sb=new StringBuilder("{");
    boolean isFirst=true;
    for(final Object object : collection)
      {
      if(!isFirst)
        sb.append(',');
      else isFirst=false;
      sb.append(object);
      }
    sb.append('}');
    return sb.toString();
    }

  /** adds all items from src to dst */
  public static <T> void addAll(final T[] src,final Collection<T> dst)
    {
    if(src!=null)
      java.util.Collections.addAll(dst,src);
    }

  public static void addAll(final Collection<Long> src,final long[] dst)
    {
    if(src==null)
      return;
    int i=0;
    for(final long l : src)
      dst[i++]=l;
    }

  public static void addAll(final long[] src,final Collection<Long> dst)
    {
    if(src==null)
      return;
    for(final long l : src)
      dst.add(l);
    }

  public static void addAll(final int[] src,final Collection<Integer> dst)
    {
    if(src==null)
      return;
    for(final int l : src)
      dst.add(l);
    }

  /** adds all items from src to dst */
  public static <T> void addAll(final Collection<T> src,final Collection<T> dst)
    {
    if(src!=null)
      dst.addAll(src);
    }

  }