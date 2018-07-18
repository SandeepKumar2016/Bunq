package com.support.android.designlibdemo;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Mocked API for listing {@link Cheese}s.
 *
 * Created by wessel on 21/03/16.
 */
public class CheeseApi {
    private static final Random RANDOM = new Random();

    /**
     * List the {@link Cheese}s.
     *
     * @param amount The amount of {@link Cheese}s to list.
     * @return The listed {@link Cheese}s.
     */
    @NonNull
    public static List<Cheese> listCheeses(int amount) throws IOException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException exception) {
            // Ignore.
        } finally {
            if (RANDOM.nextInt(10) >= 7) {
                throw new IOException("API problem!");
            } else {
                return Cheeses.getRandomSublist(amount);
            }
        }
    }
}
