/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.localstore;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.filesystem.FileStore;
import org.eclipse.core.filesystem.IFileStoreConstants;
import org.eclipse.core.internal.resources.ICoreConstants;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.internal.utils.Messages;
import org.eclipse.core.internal.utils.Policy;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

public class DeleteVisitor implements IUnifiedTreeVisitor, ICoreConstants {
	protected IProgressMonitor monitor;
	protected boolean force;
	protected boolean keepHistory;
	protected MultiStatus status;
	protected List skipList;

	public DeleteVisitor(List skipList, int flags, IProgressMonitor monitor) {
		this.skipList = skipList;
		this.force = (flags & IResource.FORCE) != 0;
		this.keepHistory = (flags & IResource.KEEP_HISTORY) != 0;
		this.monitor = monitor;
		status = new MultiStatus(ResourcesPlugin.PI_RESOURCES, IResourceStatus.FAILED_DELETE_LOCAL, Messages.localstore_deleteProblem, null);
	}

	/**
	 * Deletes a file from both the workspace resource tree and the file system.
	 */
	protected void delete(UnifiedTreeNode node, boolean deleteLocalFile, boolean shouldKeepHistory) {
		Resource target = (Resource) node.getResource();
		try {
			deleteLocalFile = deleteLocalFile && !target.isLinked() && node.existsInFileSystem();
			FileStore localFile = deleteLocalFile ? node.getStore() : null;
			if (shouldKeepHistory) {
				IHistoryStore store = target.getLocalManager().getHistoryStore();
				recursiveKeepHistory(store, node);
			}
			node.removeChildrenFromTree();
			//delete from disk
			if (localFile != null && !target.isLinked())
				localFile.delete(IFileStoreConstants.NONE, Policy.subMonitorFor(monitor, 1));
			else
				monitor.worked(1);
			//delete from tree
			if (target != null && node.existsInWorkspace())
				target.deleteResource(true, status);
		} catch (CoreException e) {
			status.add(e.getStatus());
		}
	}

	private void recursiveKeepHistory(IHistoryStore store, UnifiedTreeNode node) {
		if (node.isFolder()) {
			for (Iterator children = node.getChildren(); children.hasNext();)
				recursiveKeepHistory(store, (UnifiedTreeNode) children.next());
		} else {
			store.addState(node.getResource().getFullPath(), node.getStore(), node.getLastModified(), true);
		}
	}

	protected boolean equals(IResource one, IResource another) {
		return one.getFullPath().equals(another.getFullPath());
	}

	public MultiStatus getStatus() {
		return status;
	}

	protected boolean isAncestor(IResource one, IResource another) {
		return one.getFullPath().isPrefixOf(another.getFullPath()) && !equals(one, another);
	}

	protected boolean isAncestorOfResourceToSkip(IResource resource) {
		if (skipList == null)
			return false;
		for (int i = 0; i < skipList.size(); i++) {
			IResource target = (IResource) skipList.get(i);
			if (isAncestor(resource, target))
				return true;
		}
		return false;
	}

	protected void removeFromSkipList(IResource resource) {
		if (skipList != null)
			skipList.remove(resource);
	}

	protected boolean shouldSkip(IResource resource) {
		if (skipList == null)
			return false;
		for (int i = 0; i < skipList.size(); i++)
			if (equals(resource, (IResource) skipList.get(i)))
				return true;
		return false;
	}

	public boolean visit(UnifiedTreeNode node) {
		Policy.checkCanceled(monitor);
		Resource target = (Resource) node.getResource();
		if (target.getType() == IResource.PROJECT)
			return true;
		if (shouldSkip(target)) {
			removeFromSkipList(target);
			int ticks = target.countResources(IResource.DEPTH_INFINITE, false);
			monitor.worked(ticks);
			return false;
		}
		if (isAncestorOfResourceToSkip(target))
			return true;

		delete(node, true, keepHistory);
		return false;
	}
}
