/*
 * polymap.org Copyright (C) 2009-2015, Polymap GmbH. All rights reserved.
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

/**
 * Abstract base class; normally only used for creating subclasses and not
 * instantiated in apps. Base class for sources providing a single image.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.source.Image.html">OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public abstract class ImageSource
        extends Source {

    // TODO options

    protected ImageSource( String jsClassname ) {
        super( jsClassname );
    }

}
