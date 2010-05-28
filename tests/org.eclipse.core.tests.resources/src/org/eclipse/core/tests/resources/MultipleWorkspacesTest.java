/*******************************************************************************
 *  Copyright (c) 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.resources;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class MultipleWorkspacesTest extends ResourceTest {

	public MultipleWorkspacesTest() {
		super();
	}

	public MultipleWorkspacesTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(MultipleWorkspacesTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		ensureDoesNotExistInWorkspace(getWorkspace().getRoot());
		super.tearDown();
	}

	public void testCreateAndListProjects() {

		// create and open a non-default workspace
		IFileStore workspace1Location = getTempStore().getChild(getUniqueString());
		IWorkspace newWorkspace1 = null;
		try {
			newWorkspace1 = getWorkspaceFactory().constructWorkspace(workspace1Location.toURI());
			newWorkspace1.open(getMonitor());
		} catch (CoreException e) {
			fail("0.99", e);
		}

		// create a project in the non-default workspace
		IProject project1 = newWorkspace1.getRoot().getProject(getUniqueString());
		try {
			project1.create(getMonitor());
		} catch (CoreException e1) {
			fail("1.99", e1);
		}

		// verify the location of the project
		assertTrue("2.0", FileUtil.isPrefixOf(workspace1Location.toURI(), project1.getLocationURI()));

		IProject[] projects = newWorkspace1.getRoot().getProjects();
		assertEquals("3.0", 1, projects.length);
		assertEquals("4.0", project1, projects[0]);

		try {
			newWorkspace1.close(getMonitor());
		} catch (Exception e) {
			fail("5.99", e);
		}
	}
}