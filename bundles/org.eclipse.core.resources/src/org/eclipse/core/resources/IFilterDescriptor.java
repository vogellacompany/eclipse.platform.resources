/*******************************************************************************
 * Copyright (c) 2009 Freescale Semiconductor and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Serge Beauchamp(Freescale Semiconductor) - initial API and implementation
 *     IBM Corporation - ongoing development
 *******************************************************************************/
package org.eclipse.core.resources;

/**
 * A filter descriptor contains information about a filter factory
 * obtained from the plug-in manifest (<code>plugin.xml</code>) files.
 * <p>
 * Filter descriptors are platform-defined objects that exist
 * independent of whether that filter's bundle has been started. 
 * </p>
 * 
 * @see IFileInfoFilterFactory
 * @see IWorkspace#getFilterDescriptor(String)
 * @see IWorkspace#getFilterDescriptors()
 * @since 3.6
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IFilterDescriptor {

	public static final String ARGUMENT_TYPE_FILTER = "filter"; //$NON-NLS-1$
	public static final String ARGUMENT_TYPE_FILTERS = "filters"; //$NON-NLS-1$
	public static final String ARGUMENT_TYPE_NONE = "none"; //$NON-NLS-1$
	public static final String ARGUMENT_TYPE_STRING = "string"; //$NON-NLS-1$

	/**
	 * Returns the fully qualified id of the filter extension.
	 * @return The fully qualified id of the filter extension.
	 */
	public abstract String getId();

	/**
	 * Returns a translated, human-readable name for this filter extension.
	 * @return The human-readable filter name
	 */
	public abstract String getName();

	/**
	 * Returns a translated, human-readable description for this filter extension.
	 * @return The human-readable filter description
	 */
	public abstract String getDescription();

	/**
	 * Returns the argument type expected by this filter. The result will be one of the
	 * <tt>ARGUMENT_TYPE_*</tt> constants declared on this class.
	 * @return The argument type of this filter extension
	 */
	public abstract String getArgumentType();

	/**
	 * Returns the factory for creating filters of this type.
	 * 
	 * @return The factory for creating filters of this type.
	 */
	public abstract IFileInfoFilterFactory getFactory();

	/**
	 * TODO What is this?
	 */
	public abstract boolean isFirstOrdering();

}