/*
 * polymap.org Copyright 2009, Polymap GmbH, and individual contributors as indicated
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
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this software; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package org.polymap.rap.openlayers.base;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import org.polymap.core.runtime.event.EventHandler;

/**
 * Base interface for all types of event listeners to be registered via ol3.
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public interface OlEventListener {

    /**
     * Gets called inside the {@link Display} thread.
     *
     * @param event
     */
    @EventHandler(display = true)
    void handleEvent( OlEvent event );


    public class PayLoad {

        private final List<Value> values = new ArrayList<Value>();


        public void add( String key, String value ) {
            values.add( new Value( key, value ) );
        }


        List<Value> values() {
            return values;
        }
    }


    public class Value {

        private final String key;

        private final String value;


        Value( String key, String value ) {
            this.key = key;
            this.value = value;
        }


        String key() {
            return key;
        }


        String value() {
            return value;
        }
    }
}
