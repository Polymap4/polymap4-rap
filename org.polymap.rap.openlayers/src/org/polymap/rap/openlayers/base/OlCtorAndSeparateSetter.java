/*
 * polymap.org Copyright 2015, Falko Bräutigam. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.base;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes the name of the setter method to modify an JavaClass property used by
 * {@link OlPropertyConcern}. But initially the data must be set via c'tor if the
 * OlObject isn't created yet.
 * 
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface OlCtorAndSeparateSetter {

    public String value();

}
