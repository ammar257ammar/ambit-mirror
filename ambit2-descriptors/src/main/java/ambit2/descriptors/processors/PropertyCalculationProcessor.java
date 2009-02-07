/* DescriptorsCalculationProcessor.java
 * Author: nina
 * Date: Dec 28, 2008
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.descriptors.processors;



import org.openscience.cdk.qsar.IMolecularDescriptor;

import ambit2.core.data.Property;

public class PropertyCalculationProcessor extends  DescriptorCalculationProcessor {
	protected Property property = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4826827029980497125L;

	public PropertyCalculationProcessor() {
	}
	
	public Property getProperty() {
		return property;
	}


	public void setProperty(Property property) {
		this.property = property;
		if (property == null) return;
		try {
			Object o = property.getClazz().newInstance();
			if (o instanceof IMolecularDescriptor)
				setDescriptor((IMolecularDescriptor)o);
		} catch (Exception x) {
			setDescriptor(null);
		}
	}

}
