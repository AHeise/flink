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

package org.apache.flink.util;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides additional methods for {@link Optional}.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class OptionalUtil {
	private OptionalUtil() {
	}

	/**
	 * Returns the optional if set or else the optional returned by the supplier.
	 */
	public static <T> Optional<T> or(Optional<? extends T> optional, Supplier<Optional<T>> alternativeSupplier) {
		return optional.<Optional<T>>map(Optional::of).orElseGet(alternativeSupplier::get);
	}
}
