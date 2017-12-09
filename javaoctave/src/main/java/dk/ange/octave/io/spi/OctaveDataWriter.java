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
package dk.ange.octave.io.spi;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.spi.ServiceRegistry;

import dk.ange.octave.type.OctaveObject;

/**
 * Service Provider Interface for the IO handler 
 * that can write {@link OctaveObject}s. 
 * The according implementations are in package {@link dk.ange.octave.io.impl} 
 * and extend this class. 
 * These classes are registered in the jar-file 
 * under <code>META-INF/services/dk.ange.octave.io.OctaveDataReader</code>.
 *
 * @param <T>
 *    the type extending {@link OctaveObject} this writer can write. 
 */
public abstract class OctaveDataWriter<T extends OctaveObject> {

    /**
     * Maps the {@link #javaType()} 
     * which represents an octave type 
     * of an {@link OctaveDataWriter} to the {@link OctaveDataWriter} itself 
     * which is able to write the octave type to a writer. 
     */
    private static 
	Map<Class<? extends OctaveObject>, OctaveDataWriter<?>> writers;

    /**
     * @param <T>
     * @param example
     * @return The OctaveDataWriter or null if it does not exist
     */
    @SuppressWarnings("unchecked")
    public static <T extends OctaveObject> 
	OctaveDataWriter<T> getOctaveDataWriter(final T example) {
        initIfNecessary();
        return (OctaveDataWriter<T>) writers.get(example.getClass());
    }

    private static synchronized void initIfNecessary() {
        if (writers == null) {
            writers = new HashMap
		<Class<? extends OctaveObject>, OctaveDataWriter<?>>();
            @SuppressWarnings("rawtypes")
            final Iterator<OctaveDataWriter> sp = 
		ServiceRegistry.lookupProviders(OctaveDataWriter.class);
            while (sp.hasNext()) {
                final OctaveDataWriter<?> odw = sp.next();
                writers.put(odw.javaType(), odw);
            }
        }
    }

    /**
     * Could be OctaveScalar or OctaveMatrix. 
     *
     * @return the {@link Class} of the {@link OctaveObject} 
     *    that this IO handler loads and saves
     */
    public abstract Class<T> javaType();

    /**
     * @param writer
     *            the Writer to write to
     * @param octaveType
     *            the value to write
     * @throws IOException
     */
    public abstract void write(Writer writer, T octaveType) throws IOException;

}
