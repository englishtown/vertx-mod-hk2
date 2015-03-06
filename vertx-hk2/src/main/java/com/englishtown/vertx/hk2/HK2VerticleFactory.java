/*
 * The MIT License (MIT)
 * Copyright © 2013 Englishtown <opensource@englishtown.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.englishtown.vertx.hk2;

import io.vertx.core.Verticle;
import io.vertx.core.impl.IsolatingClassLoader;
import io.vertx.core.spi.VerticleFactory;

import java.lang.reflect.Constructor;

/**
 * Implements {@link io.vertx.core.spi.VerticleFactory} using an HK2 verticle wrapper for dependency injection.
 */
public class HK2VerticleFactory implements VerticleFactory {

    public static final String PREFIX = "java-hk2";

    @Override
    public String prefix() {
        return PREFIX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        verticleName = VerticleFactory.removePrefix(verticleName);

        classLoader = getClassLoader(classLoader);

        // Use the provided class loader to create an instance of HK2VerticleLoader.  This is necessary when working with vert.x IsolatingClassLoader
        @SuppressWarnings("unchecked")
        Class<Verticle> loader = (Class<Verticle>) classLoader.loadClass(HK2VerticleLoader.class.getName());
        Constructor<Verticle> ctor = loader.getConstructor(String.class, ClassLoader.class);

        if (ctor == null) {
            throw new IllegalStateException("Could not find HK2VerticleLoad constructor");
        }

        return ctor.newInstance(verticleName, classLoader);

    }

    protected ClassLoader getClassLoader(ClassLoader classLoader) {

        if (classLoader instanceof IsolatingClassLoader) {
            return new WrappedIsolatingClassLoader((IsolatingClassLoader) classLoader);
        } else {
            return classLoader;
        }

    }

}
