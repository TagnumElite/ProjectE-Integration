/*
 * Copyright (c) 2019-2023 TagnumElite
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tagnumelite.projecteintegration.api;

import java.util.Random;

/**
 * Fake Random number generator, all numbers return the seed. nextBoolean always true unless specified otherwise.
 */
public class FRandom extends Random {
    private final long rng;
    private final boolean bool;

    public FRandom(long number) {
        super(number);
        this.rng = number;
        bool = true;
    }

    public FRandom(long number, boolean bool) {
        super(number);
        this.rng = number;
        this.bool = bool;
    }

    @Override
    public int nextInt() {
        return (int) rng;
    }

    @Override
    public int nextInt(int bound) {
        return (int) rng;
    }

    @Override
    public long nextLong() {
        return rng;
    }

    @Override
    public boolean nextBoolean() {
        return bool;
    }

    @Override
    public float nextFloat() {
        return rng;
    }

    @Override
    public double nextDouble() {
        return rng;
    }

    @Override
    public synchronized double nextGaussian() {
        return rng;
    }
}
