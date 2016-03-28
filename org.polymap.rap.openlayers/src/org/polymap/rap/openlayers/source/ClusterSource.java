/*
 * <<<<<<< HEAD polymap.org Copyright (C) 2009-2014, Polymap GmbH. All rights
 * reserved. ======= polymap.org Copyright (C) @year@ individual contributors as
 * indicated by the @authors tag. All rights reserved. >>>>>>>
 * 6a8ac5780fb40f90177477dc3c07d601caabbe90
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.source;

import java.util.Arrays;
import java.util.function.Consumer;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Mandatory;
import org.polymap.rap.openlayers.base.OlPropertyConcern;

/**
 * @author "Joerg Reichert <joerg@mapzone.io>"
 *
 */
public class ClusterSource
        extends VectorSource {

    @Concern(OlPropertyConcern.class)
    public Config2<ClusterSource,VectorSource> source;

    @Concern(OlPropertyConcern.class)
    public Config2<ClusterSource,Double>       distance;


    /**
     * Constructs a new instance.
     *
     * @param initializers Initialize at least all {@link Mandatory} properties.
     */
    public ClusterSource( Consumer<ClusterSource>... initializers ) {
        super( "ol.source.Cluster" );
        Arrays.asList( initializers ).forEach( initializer -> initializer.accept( this ) );
    }
}
