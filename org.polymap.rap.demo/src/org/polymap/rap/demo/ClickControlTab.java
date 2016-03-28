/*
 * polymap.org
 * Copyright (C) 2009-2015 Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.widgets.Composite;
import org.polymap.rap.openlayers.base.OlMap;


/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class ClickControlTab
        extends DemoTab {

    public ClickControlTab() {
        super( "ClickControl" );
    }

    private final static Log log = LogFactory.getLog( ClickControlTab.class );


    @Override
    protected void createDemoControls( Composite parent ) {
        OlMap map = defaultMap( parent );

        map.addEventListener(OlMap.Event.click, event -> {
            JsonObject json = event.properties();
            JsonArray pixel = (JsonArray) json.get( "feature").asObject().get( "pixel" );
            JsonArray coordinate = (JsonArray) json.get( "feature").asObject().get( "coordinate" );
            StatusBar.getInstance().addInfo( parent, String.format( "%s - pixel clicked: (x=%s, y=%s) => coordinate=(x=%s, y=%s)", 
                    name(), pixel.get( 0 ), pixel.get( 1 ), coordinate.get( 0 ), coordinate.get( 1 )));
        });
    }


    @Override
    protected void createStyleControls( Composite parent ) {
        // TODO Auto-generated method stub

    }
}
