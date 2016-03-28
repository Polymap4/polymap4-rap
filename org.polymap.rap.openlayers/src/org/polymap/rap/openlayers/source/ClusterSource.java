/*
 * polymap.org Copyright (C) 2009-2014, Polymap GmbH. All rights reserved.
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

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Mandatory;
import org.polymap.rap.openlayers.base.OlFeature;
import org.polymap.rap.openlayers.base.OlPropertyConcern;

import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * Layer source to cluster vector data.
 * 
 * @see <a href="http://openlayers.org/en/v3.6.0/apidoc/ol.source.Cluster.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://mapzone.io">Steffen Stundzig</a>
 */
public class ClusterSource
        extends VectorSource {

    // TODO more properties

    @Immutable
    @Mandatory
    @Concern(OlPropertyConcern.class)
    public Config2<ClusterSource,VectorSource> source;

    @Immutable
    @Mandatory
    @Concern(OlPropertyConcern.class)
    public Config2<ClusterSource,Integer>      distance;


    public ClusterSource( VectorSource source, Integer distance ) {
        super( "ol.source.Cluster" );
        this.source.set( source );
        this.distance.set( distance );
    }

//
//    public void addFeature( OlFeature feature ) {
//        source.get().addFeature( feature );
//    }
//
//
//    public void addFeatures( OlFeature... features ) {
//        source.get().addFeatures( features );
//    }
//
//
//    public void removeFeature( OlFeature feature ) {
//        source.get().removeFeature( feature );
//    }

}
