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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class DemoApplication
        implements ApplicationConfiguration {

    public void configure( Application application ) {
        Map<String,String> properties = new HashMap<String,String>();
        properties.put( WebClient.PAGE_TITLE, "Polymap RAP Openlayers Demo" );
        application.addEntryPoint( "/demo", DemoEntryPoint.class, properties );
        application.addEntryPoint( "/demo2", DemoEntryPoint2.class, properties );
        
        application.addResource( "/polygon-samples.geojson", resourceName -> {
            return load( "./resources/polygon-samples.geojson" );
        } );
        
        // to overwrite the CSS simple add your own CSS resource as ol.css here
        application.addResource( "ol/css/ol.css", resourceName -> {
            return load( "./resources/css/ol-3.7.0.css" );
        } );
        application.addResource( "ol/css/progress.css", resourceName -> {
            return load( "./resources/css/progress.css" );
        } );
    }


    private InputStream load( String resourceName ) {
        return this.getClass().getClassLoader().getResourceAsStream( resourceName );
    }
}
