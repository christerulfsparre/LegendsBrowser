package legends.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Filter<T> {
    private ArrayList<Predicate<T>> filters = new ArrayList<Predicate<T>>();

    public void add(Predicate<T> predicate) {
        this.filters.add(predicate);
    }

    public boolean empty() {
        return this.filters.isEmpty();
    }

    public Predicate<T> predicate() {
        return (T t) -> {
            boolean result = true;
            for (Predicate<T> p : this.filters) {
                if(!p.test(t)) {
                    result = false;
                    break;
                }
            }
            return result;
        };
    }

    public Collection<T> on(Collection<T> collection) {
        if(this.empty()) {
            return collection;
        } else {
            return collection.stream().filter(this.predicate()).collect(Collectors.toList());
        }
    }
}