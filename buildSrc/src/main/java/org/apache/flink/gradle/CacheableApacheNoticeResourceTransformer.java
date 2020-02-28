/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.gradle;

import com.github.jengelman.gradle.plugins.shadow.transformers.*;
import groovy.transform.Generated;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import shadow.org.apache.tools.zip.ZipOutputStream;

@CacheableTransformer
public class CacheableApacheNoticeResourceTransformer implements Transformer {
	private final ApacheNoticeResourceTransformer inner = new ApacheNoticeResourceTransformer();

	public CacheableApacheNoticeResourceTransformer() {
		inner.setEncoding("UTF-8");
	}

	@Internal
	private ApacheNoticeResourceTransformer getInner() {
		return inner;
	}

	@Override
	public boolean canTransformResource(final FileTreeElement element) {
		return inner.canTransformResource(element);
	}

	@Override
	public void transform(final TransformerContext context) {
		inner.transform(context);
	}

	@Override
	public boolean hasTransformedResource() {
		return inner.hasTransformedResource();
	}

	@Override
	public void modifyOutputStream(final ZipOutputStream os, final boolean preserveFileTimestamps) {
		inner.modifyOutputStream(os, preserveFileTimestamps);
	}

	@Input
	public String getProjectName() {
		return inner.getProjectName();
	}

	public void setProjectName(final String s) {
		inner.setProjectName(s);
	}

	@Input
	public boolean isAddHeader() {
		return inner.isAddHeader();
	}

	public void setAddHeader(final boolean b) {
		inner.setAddHeader(b);
	}

	@Input
	public String getPreamble1() {
		return inner.getPreamble1();
	}

	public void setPreamble1(final String s) {
		inner.setPreamble1(s);
	}

	@Input
	public String getPreamble2() {
		return inner.getPreamble2();
	}

	public void setPreamble2(final String s) {
		inner.setPreamble2(s);
	}

	@Input
	public String getPreamble3() {
		return inner.getPreamble3();
	}

	public void setPreamble3(final String s) {
		inner.setPreamble3(s);
	}

	@Input
	public String getOrganizationName() {
		return inner.getOrganizationName();
	}

	public void setOrganizationName(final String s) {
		inner.setOrganizationName(s);
	}

	@Input
	public String getOrganizationURL() {
		return inner.getOrganizationURL();
	}

	public void setOrganizationURL(final String s) {
		inner.setOrganizationURL(s);
	}

	@Input
	public String getInceptionYear() {
		return inner.getInceptionYear();
	}

	public void setInceptionYear(final String s) {
		inner.setInceptionYear(s);
	}

	@Input
	public String getEncoding() {
		return inner.getEncoding();
	}

	public void setEncoding(final String s) {
		inner.setEncoding(s);
	}
}