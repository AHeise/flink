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

import org.apache.flink.shaded.curator4.org.apache.curator.shaded.com.google.common.collect.Iterators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

@Internal
public final class PrioritizedDeque<T> implements Iterable<T> {
	private final Deque<T> deque;
	private int numPriorityElements;

	public PrioritizedDeque() {
		this.deque = new ArrayDeque<>();
	}

	public void addPriorityElement(T element) {
		final ArrayDeque<T> priorPriority = new ArrayDeque<>(numPriorityElements);
		for (int index = 0; index < numPriorityElements; index++) {
			priorPriority.addFirst(deque.poll());
		}
		deque.addFirst(element);
		numPriorityElements++;
		for (final T priorityEvent : priorPriority) {
			deque.addFirst(priorityEvent);
		}
	}

	public void addPriorityElement(T element, boolean prioritize) {
		if (prioritize) {
			prioritize(element);
		} else {
			addPriorityElement(element);
		}
	}

	public void add(T element) {
		deque.add(element);
	}

	public void add(T element, boolean priority, boolean prioritize) {
		if (!priority) {
			add(element);
		} else {
			addPriorityElement(element, prioritize);
		}
	}

	public void prioritize(T element) {
		if (containsPriorityElement(element)) {
			return;
		}
		if (Iterators.get(deque.iterator(), numPriorityElements, null) == element) {
			// if the next non-priority element is the given element, we can simply include it in our priority section
			numPriorityElements++;
			return;
		}
		deque.remove(element);
		addPriorityElement(element);
	}

	public Deque<T> getDeque() {
		return deque;
	}

	public T poll() {
		final T polled = deque.poll();
		if (polled != null) {
			decreasePriorityElements();
		}
		return polled;
	}

	private void decreasePriorityElements() {
		if (numPriorityElements > 0) {
			numPriorityElements--;
		}
	}

	public T peek() {
		return deque.peek();
	}

	public int getNumPriorityElements() {
		return numPriorityElements;
	}

	public boolean containsPriorityElement(T element) {
		if (numPriorityElements == 0) {
			return false;
		}
		final Iterator<T> iterator = deque.iterator();
		for (int i = 0; i < numPriorityElements && iterator.hasNext(); i++) {
			if (iterator.next() == element) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		return deque.size();
	}

	public int getNumUnprioritizedElements() {
		return size() - getNumPriorityElements();
	}

	public Iterator<T> iterator() {
		return deque.iterator();
	}

	public void clear() {
		deque.clear();
		numPriorityElements = 0;
	}

	public boolean isEmpty() {
		return deque.isEmpty();
	}

	public boolean contains(T element) {
		return deque.contains(element);
	}

	@Override
	public boolean equals(Object o) {
		return deque.equals(o);
	}

	@Override
	public int hashCode() {
		return deque.hashCode();
	}

	@Override
	public String toString() {
		return deque.toString();
	}

	public T peekLast() {
		return deque.peekLast();
	}
}
