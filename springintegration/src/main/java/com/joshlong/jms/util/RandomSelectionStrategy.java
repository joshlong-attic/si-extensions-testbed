package com.joshlong.jms.util;

import org.apache.commons.lang.math.RandomUtils;

import java.util.List;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class RandomSelectionStrategy<T> implements SelectionStrategy<T> {
    @Override
    public T which(final List<T> choices) {
        int randInt = RandomUtils.nextInt(choices.size() - 1);

        return choices.get(randInt);
    }
}
