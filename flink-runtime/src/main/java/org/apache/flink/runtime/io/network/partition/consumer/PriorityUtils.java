/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.io.network.partition.consumer;

import org.apache.flink.annotation.Internal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Predicate;

@Internal
public class PriorityUtils {
	public static <T> int enqueueAfter(Deque<T> arrayDeque, Predicate<T> priorityPredicate, T element) {
		final ArrayDeque<T> priorPriority = new ArrayDeque<>(arrayDeque.size());
		while (testFirst(arrayDeque, priorityPredicate)) {
			priorPriority.addFirst(arrayDeque.poll());
		}
		arrayDeque.addFirst(element);
		for (final T priorityEvent : priorPriority) {
			arrayDeque.addFirst(priorityEvent);
		}
		return priorPriority.size();
	}

	public static <T> boolean testFirst(Deque<T> arrayDeque, Predicate<T> priorityPredicate) {
		final T lastElement = arrayDeque.peek();
		return lastElement != null && priorityPredicate.test(lastElement);
	}

	public static <T> boolean contains(Deque<T> arrayDeque, int limit, T channel) {
		final Iterator<T> iterator = arrayDeque.iterator();
		for (int i = 0; i < limit && iterator.hasNext(); i++) {
			if (iterator.next() == channel) {
				return true;
			}
		}
		return false;
	}
}

