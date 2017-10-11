package phantom.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Indicates that the given element is currently missing proper documentation.
 * 
 * @author Brandon D. McKay */

@Retention(RetentionPolicy.SOURCE)
public @interface Undocumented {}
