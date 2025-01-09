package ca.odell.glazedlists;

import java.util.Comparator;

public interface SortableEventList<E> extends EventList<E> {
    Comparator<? super E> getComparator();
    void setComparator(Comparator<? super E> comparator);
}
