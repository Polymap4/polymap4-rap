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
package org.polymap.rap.openlayers.layer;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;
import org.polymap.rap.openlayers.base.OlEventListener;
import org.polymap.rap.openlayers.base.OlEventListener.PayLoad;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.source.Source;

/**
 * Abstract base class; normally only used for creating subclasses and not
 * instantiated in apps. A visual representation of raster or vector map data. Layers
 * group together those properties that pertain to how the data is to be displayed,
 * irrespective of the source of that data.
 *
 * @see <a
 *      href="http://openlayers.org/en/master/apidoc/ol.layer.Layer.html">OpenLayers
 *      Doc</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public abstract class Layer<S extends Source>
        extends Base {

    @Immutable
    @Mandatory
    @Concern(OlPropertyConcern.class)
    public Config2<Layer<S>,S> source;


    public Layer( String jsClassname ) {
        super( jsClassname );
    }
}
