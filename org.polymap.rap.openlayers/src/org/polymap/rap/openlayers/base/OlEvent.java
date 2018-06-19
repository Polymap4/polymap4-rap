/*
 * polymap.org
 * Copyright (C) 2009-2018, Polymap GmbH. All rights reserved.
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
package org.polymap.rap.openlayers.base;

import java.util.EventObject;

import org.json.JSONObject;

/**
 * The base event used by the listeners.
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class OlEvent
        extends EventObject {

    private final String     name;

    private final JSONObject properties;


    public OlEvent( OlObject src, String name, JSONObject properties ) {
        super( src );
        this.name = name;
        this.properties = properties;
    }


    @Override
    public OlObject getSource() {
        return (OlObject)super.getSource();
    }


    public String name() {
        return name;
    }


    public JSONObject properties() {
        return properties;
    }
}
