/* MoleculesIteratorReader.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-23 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit.data.molecule;

import java.io.IOException;

import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

/**
 * A class to read {@link ambit.data.molecule.IMoleculesIterator} by
 * {@link org.openscience.cdk.io.iterator.IIteratingChemObjectReader} interface.
 * Used as a reader in various batch processing {@link ambit.io.batch.IBatch}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-23
 */
public class MoleculesIteratorReader extends DefaultIteratingChemObjectReader {
    protected IMoleculesIterator molecules;
    /**
     * 
     */
    public MoleculesIteratorReader(IMoleculesIterator molecules) {
        super();
        this.molecules = molecules;
        molecules.setSelectedIndex(-1);
    }


    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#close()
     */
    public void close() throws IOException {
        molecules.first();

    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return molecules.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        return molecules.next();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {

        return null;
    }
    @Override
    public String toString() {
    	return "Reads current set of structures";
    }
}