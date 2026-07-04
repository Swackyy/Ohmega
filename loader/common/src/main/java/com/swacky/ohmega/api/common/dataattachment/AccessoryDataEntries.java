package com.swacky.ohmega.api.common.dataattachment;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class AccessoryDataEntries extends ArrayList<AccessoryDataEntry> {
    private final AccessoryData data;

    public AccessoryDataEntries(int initialCapacity, AccessoryData data) {
        super(initialCapacity);

        this.data = data;
    }

    public AccessoryDataEntries(AccessoryData data) {
        super();

        this.data = data;
    }

    public AccessoryDataEntries(@NonNull Collection<? extends AccessoryDataEntry> collection, AccessoryData data) {
        super(collection);

        this.data = data;
    }

    private void onRemove(AccessoryDataEntry entry) {

    }

    private void onSet(AccessoryDataEntry entry) {

    }

    @Override
    public AccessoryDataEntry remove(int index) {
        onRemove(get(index));
        return super.remove(index);
    }

    @Override
    public AccessoryDataEntry removeFirst() {
        onRemove(getFirst());
        return super.removeFirst();
    }

    @Override
    public AccessoryDataEntry removeLast() {
        onRemove(getLast());
        return super.removeLast();
    }

    @Override
    public boolean remove(Object object) {
        if (object instanceof AccessoryDataEntry entry) {
            onRemove(entry);
        }

        return super.remove(object);
    }

    @Override
    public boolean removeIf(Predicate<? super AccessoryDataEntry> filter) {
        for (AccessoryDataEntry entry : this) {
            if (filter.test(entry)) {
                onRemove(entry);
            }
        }

        return super.removeIf(filter);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        for (Object object : collection) {
            if (contains(object) && object instanceof AccessoryDataEntry entry) {
                onRemove(entry);
            }
        }

        return super.removeAll(collection);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            onRemove(get(i));
        }

        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public AccessoryDataEntry set(int index, AccessoryDataEntry element) {
        onSet(element);
        return super.set(index, element);
    }

    @Override
    public void clear() {
        for (AccessoryDataEntry entry : this) {
            onRemove(entry);
        }

        super.clear();
    }

    @Override
    public void replaceAll(UnaryOperator<AccessoryDataEntry> operator) {
        for (AccessoryDataEntry entry : this) {
            onRemove(entry);
        }

        super.replaceAll(operator);
    }
}
