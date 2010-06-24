package com.joshlong.jms.util;

import java.util.List;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public interface SelectionStrategy<T> {
    T which(List<T> choices);
}
