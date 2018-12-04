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
package org.polymap.rap.openlayers.style;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A list of {@link Style}s. 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class StyleArray
        extends AbstractCollection<Style>
        implements Base {

    /**
     * Creates a new instance with the given styles added to it.
     *
     * @param styles
     * @return Newly created instance.
     */
    public static StyleArray of( Style... styles ) {
        return new StyleArray( styles );
    }

    
    // instance *******************************************
    
    private Style[]         styles;
    
    public StyleArray( Style... styles ) {
        this.styles = styles;
    }

    @Override
    public int size() {
        return styles.length;
    }

    @Override
    public Iterator<Style> iterator() {
        return Arrays.asList( styles ).iterator();
    }

}
