package com.joshlong.jms.util;

import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class RoundRobinSelectionStrategy<T> implements SelectionStrategy<T> {
    private AtomicInteger integer = new AtomicInteger();

    @Override
    public T which(final List<T> choices) {
        Assert.notEmpty(choices, "choices can't be empty");

        int len = choices.size(); // should be constant

        integer.compareAndSet(len, 0); // if by chance its overrun the safe level and is now equal to lenght + 1, then wrap it around to 0

        int indx = Math.max(0, Math.min(integer.getAndIncrement(), len - 1));

        //there should be no way to be < 0 and no way to be > len -1
        // theres a slight thread safety issue here, so again to be sure, the final value is ensured to be < the total length
        return choices.get(indx);
    }
}
