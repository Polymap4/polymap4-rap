/* 
 * polymap.org
 * Copyright (C) 2018, Falko Bräutigam. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.interaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Usually this does not have to be added explicitly.
 *
 * @see <a href="http://openlayers.org/en/v3.20.1/apidoc/ol.interaction.DragRotateAndZoom.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class DragRotateAndZoomInteraction
        extends Interaction {

    private static final Log log = LogFactory.getLog( DragRotateAndZoomInteraction.class );

    
    public DragRotateAndZoomInteraction() {
        super( "ol.interaction.DragRotateAndZoom" );
    }

}
