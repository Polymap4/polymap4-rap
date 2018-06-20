/*
 * polymap.org
 * Copyright 2009-2018, Polymap GmbH, and individual contributors as indicated
 * by the @authors tag.
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

import org.eclipse.swt.widgets.Display;

import org.polymap.core.runtime.event.EventHandler;

/**
 * An (optional) default interface for all types of event listeners.
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public interface OlEventListener {

    /**
     * Gets called inside the {@link Display} thread.
     *
     * @param ev
     */
    @EventHandler(display = true)
    void handleEvent( OlEvent ev );

}
