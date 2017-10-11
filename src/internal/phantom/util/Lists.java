package phantom.util;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

@Undocumented
public class Lists
{
	public static <T> T first(T[] array)
	{
		return array[0];
	}
	
	public static <T> T first(List<T> list)
	{
		return list.get(0);
	}
	
	public static <T> T last(T[] array)
	{
		return array[array.length - 1];
	}
	
	public static <T> T last(List<T> list)
	{
		return list.get(list.size() - 1);
	}
	
	public static <B, A> A[] map(B[] beforeArray, Function<B, A> function, A[] afterArray)
	{
		for(int i = 0; i < beforeArray.length; i++) afterArray[i] = function.apply(beforeArray[i]);
		return afterArray;
	}
	
	public static <B, A> A[] map(B[] beforeArray, Function<B, A> function, IntFunction<A[]> afterArrayConstructor)
	{
		return map(beforeArray, function, afterArrayConstructor.apply(beforeArray.length));
	}
	
	public static <B, A> A accumulate(B[] array, BiFunction<A, B, A> function, A identity)
	{
		for(B b : array) identity = function.apply(identity, b);
		return identity;
	}
}
