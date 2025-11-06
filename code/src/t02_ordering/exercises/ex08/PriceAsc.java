package t02_ordering.exercises.ex08;

import t02_ordering.exercises.common.Product;

import java.util.Comparator;

public class PriceAsc implements Comparator<Product> {
    @Override public int compare(Product a, Product b) {
        return Double.compare(a.price(), b.price());
    }
}
