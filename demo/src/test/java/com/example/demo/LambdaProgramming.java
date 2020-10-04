package com.example.demo;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 설명 :
 *
 * @author 이민호(Mark) / minholee93@sk.com
 * 2020/10/04
 * 6:12 오후
 */
public class LambdaProgramming {

    @Test
    public void logging_if_loglevel_is_INFO() {
        info(Logger.getLogger("test"), () -> "x : " + 1);
    }

    public static void info(Logger logger, Supplier<String> message) {
        if (logger.isLoggable(Level.INFO)) logger.info(message.get());
    }

    String[] names = {"test1", "test2"};

    @Test
    public void IntConsumer_with_parameter() {
        Arrays.sort(names, (s, t) -> Integer.compare(s.length(), t.length()));

        repeat(10, i -> System.out.println("Countdown : " + (9 - i)));
    }

    public static void repeat(int n, IntConsumer action) {
        for (int i = 0; i < n; i++) action.accept(i);
    }

    @Test
    public void generic_functional_interface() {
        // Image brigthenedImage = transform(image, Color::brighter);
    }

    public static Image transform(Image in, UnaryOperator<Color> f) {
        int width = (int) in.getWidth();
        int height = (int) in.getHeight();
        WritableImage out = new WritableImage(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                out.getPixelWriter().setColor(x, y,
                        f.apply(in.getPixelReader().getColor(x, y)));
            }
        }
        return out;
    }

    @FunctionalInterface
    public interface ColorTransformer {
        Color apply(int x, int y, Color colorAtXY);
    }

    @Test
    public void return_method() {
        // Image brightenedImage = transform(image, brighten(1.2));
    }

    public static UnaryOperator<Color> brighten(double factor) {
        return c -> c.deriveColor(0, 1, factor, 1);
    }

    @Test
    public void compose_method() {
        // Image finalImage =  transform(image, compose(Color::brighter, Color::grayscale));
    }

    public static <T> UnaryOperator<T> compose(UnaryOperator<T> op1, UnaryOperator<T> op2) {
        return t -> op2.apply(op1.apply(t));
    }

    @Test
    public void latent_delay_method() {
        // LatentImage latentImage = LatentImage.from(image).transform(Color::brighter).transform(Color::grayscale);
    }

    public static class LatentImage {
        private Image in;
        private List<UnaryOperator<Color>> pendingOperations;

        LatentImage transform(UnaryOperator<Color> f) {
            pendingOperations.add(f);
            return this;
        }

        public LatentImage (Image image){
            this.in = image;
        }

        public static LatentImage from(Image image){
            return new LatentImage(image);
        }
    }

    public static void doInOrder(Runnable first, Runnable second) {
        first.run();
        second.run();
    }

    public static void doInOrderAsync(Runnable first, Runnable second) {
        Thread t = new Thread() {
            public void run() {
                first.run();
                second.run();
            }
        };
        t.start();
    }

    public static void doInOrderAsyncWithHandler(Runnable first, Runnable second, Consumer<Throwable> handler) {
        Thread t = new Thread() {
            public void run() {
                try {
                    first.run();
                    second.run();
                } catch (Throwable t) {
                    handler.accept(t);
                }
            }
        };
        t.start();
    }
}
