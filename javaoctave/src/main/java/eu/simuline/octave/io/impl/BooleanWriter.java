/*
 * Copyright 2008, 2009 Ange Optimization ApS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.simuline.octave.io.impl;

import java.io.IOException;
import java.io.Writer;

import eu.simuline.octave.type.OctaveBoolean;

/**
 * The writer for the octave type "bool matrix" (matrix with boolean entries) 
 * writing an {@link OctaveBoolean} to a {@link Writer}. 
 */
public final class BooleanWriter 
    extends AbstractPrimitiveMatrixWriter<OctaveBoolean> {

    @Override
    public Class<OctaveBoolean> javaType() {
        return OctaveBoolean.class;
    }

    @Override
    public void write(final Writer writer, 
		      final OctaveBoolean octaveBoolean) throws IOException {
        writer.write("# type: bool matrix\n");
        if (octaveBoolean.getSizeLength() > 2) {
            saveDataVectorized(writer, octaveBoolean);
        } else {
            saveData2d(writer, octaveBoolean);
        }
    }

    private void saveData2d(final Writer writer, 
			    final OctaveBoolean octaveBoolean) 
	throws IOException {

        final boolean[] data = octaveBoolean.getData();
        final int nrows = octaveBoolean.getSize(1);
        final int ncols = octaveBoolean.getSizeLength() > 1 
	    ? octaveBoolean.getSize(2) : 1;
        writer.write(NROWS + nrows + "\n");
        writer.write(NCOLUMNS + ncols + "\n");
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                writer.write(" " + (data[row + col * nrows] ? "1" : "0"));
            }
            writer.write('\n');
        }
    }

    private void saveDataVectorized(final Writer writer, 
				    final OctaveBoolean octaveMatrix) 
	throws IOException {

        final boolean[] data = octaveMatrix.getData();
        writer.write(NDIMS + octaveMatrix.getSizeLength() + "\n");
        for (int idx = 1; idx <= octaveMatrix.getSizeLength(); idx++) {
            writer.write(" " + octaveMatrix.getSize(idx));
        }
        for (final boolean b : data) {
            writer.write("\n " + (b ? "1" : "0"));
        }
        writer.write("\n");
    }

}
