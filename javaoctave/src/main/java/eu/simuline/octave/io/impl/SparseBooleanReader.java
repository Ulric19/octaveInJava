/*
 * Copyright 2010 Ange Optimization ApS
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

import java.io.BufferedReader;

import eu.simuline.octave.exception.OctaveParseException;

import eu.simuline.octave.io.OctaveIO;

import eu.simuline.octave.io.spi.OctaveDataReader;
import eu.simuline.octave.type.OctaveSparseBoolean;

/**
 * The reader for the octave type "sparse bool matrix" 
 * reading an {@link OctaveSparseBoolean} from a {@link BufferedReader}. 
 *
 * <pre>
 * # name: x
 * # type: sparse bool matrix
 * # nnz: 1
 * # rows: 1
 * # columns: 1
 * 1 1 1
 * </pre>
 */
public final class SparseBooleanReader extends OctaveDataReader {

    @Override
    public String octaveType() {
        return "sparse bool matrix";
    }

    /**
     * @throws OctaveParseException
     */
    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    // 3 is clear from SparseBooleanWriter 
    // **** seems buggy: headline "# type: sparse bool matrix\n" not read. 
    public OctaveSparseBoolean read(final BufferedReader reader) {
        final int nnz     = parseHeader("# nnz: ", 
					OctaveIO.readerReadLine(reader));
        final int rows    = parseHeader("# rows: ", 
					OctaveIO.readerReadLine(reader));
        final int columns = parseHeader("# columns: ", 
					OctaveIO.readerReadLine(reader));

        final OctaveSparseBoolean sparse = 
	    new OctaveSparseBoolean(rows, columns, nnz);
        for (int n = 0; n < nnz; ++n) {
            final String line = OctaveIO.readerReadLine(reader);
            final String[] split = line.split(" ");
            if (split.length != 3) { // NOPMD 
                throw new OctaveParseException("split.length != 3");
            }
            try {
                final int row       = Integer.parseInt(split[0]);
                final int column    = Integer.parseInt(split[1]);
                final boolean value = BooleanReader.parseBoolean(split[2]);
                sparse.set(value, row, column);
            } catch (final NumberFormatException e) {
                throw new OctaveParseException(e);
            }
        }

        return sparse;
    }

    private int parseHeader(final String prefix, final String line) {
        if (line == null || !line.startsWith(prefix)) {
            throw new OctaveParseException
		("Expected a line that should start with '" + prefix + 
		 "', got '" + line + "'");
        }
        try {
            return Integer.parseInt(line.substring(prefix.length()));
        } catch (final NumberFormatException e) {
            throw new OctaveParseException(e);
        }
    }

}
