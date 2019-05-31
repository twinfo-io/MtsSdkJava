/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class AmqpSendResultHelper {

    public static void removeDone(List<AmqpSendResult> input) {
        if ((input == null) || (input.size() == 0)) {
            return;
        }

        input.removeIf(Future::isDone);
    }

    public static List<AmqpSendResult> waitForAll(List<AmqpSendResult> input, boolean returnOnlyFailed) {
        if ((input == null) || (input.size() == 0)) {
            return input;
        }

        if (returnOnlyFailed) {
            return waitAndReturnFailed(input);
        } else {
            return waitAndReturnAll(input);
        }
    }

    public static List<AmqpSendResult> filterCompleted(List<AmqpSendResult> input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        List<AmqpSendResult> results = new ArrayList<>();
        ListIterator<AmqpSendResult> iterator = input.listIterator();
        while (iterator.hasNext()) {
            AmqpSendResult result = iterator.next();
            if (result.isDone()) {
                results.add(result);
                iterator.remove();
            }
        }

        return results;
    }

    private static List<AmqpSendResult> waitAndReturnAll(List<AmqpSendResult> input) {

        int numOfChanges = 1;
        List<AmqpSendResult> tmp = new LinkedList<>(input);
        while (numOfChanges > 0) {
            numOfChanges = 0;
            ListIterator<AmqpSendResult> iterator = tmp.listIterator();
            while (iterator.hasNext()) {
                AmqpSendResult item = iterator.next();
                if (item == null) {
                    iterator.remove();
                } else if (item.isDone()) {
                    numOfChanges++;
                    iterator.remove();
                }
            }
        }

        for (AmqpSendResult item : tmp) {
            if (!item.isDone()) {
                try {
                    item.get();
                } catch (InterruptedException | ExecutionException ignored) {
                }
            }
        }

        return input;
    }

    private static List<AmqpSendResult> waitAndReturnFailed(List<AmqpSendResult> input) {
        List<AmqpSendResult> result = new LinkedList<>();

        int numOfChanges = 1;
        List<AmqpSendResult> tmp = new LinkedList<>(input);
        while (numOfChanges > 0) {
            numOfChanges = 0;
            ListIterator<AmqpSendResult> iterator = tmp.listIterator();
            while (iterator.hasNext()) {
                AmqpSendResult item = iterator.next();
                if (item == null) {
                    iterator.remove();
                } else if (item.isDone()) {
                    try {
                        if (!Boolean.TRUE.equals(item.get())) {
                            result.add(item);
                        }
                    } catch (InterruptedException | ExecutionException ignored) {
                        result.add(item);
                    }
                    numOfChanges++;
                    iterator.remove();
                }
            }
        }

        for (AmqpSendResult item : tmp) {
            try {
                if (!Boolean.TRUE.equals(item.get())) {
                    result.add(item);
                }
            } catch (InterruptedException | ExecutionException ignored) {
                result.add(item);
            }
        }

        return result;
    }
}
