/*
 * Copyright 2008 Ange Optimization ApS
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
/**
 * @author Kim Hansen
 */
package eu.simuline.octave.exec;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import eu.simuline.octave.util.IOUtils;

/**
 * Reads all from the reader and writes it to the writer. 
 */
public final class WriterReadFunctor implements ReadFunctor {

    private final Writer writer;

    /**
     * @param writer
     */
    public WriterReadFunctor(final Writer writer) {
        this.writer = writer;
    }

    /**
     * @param reader
     * @throws IOException if any.
     */
    @Override
    public void doReads(final Reader reader) throws IOException {
        IOUtils.copy(reader, writer);
    }

}
