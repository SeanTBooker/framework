
package org.kevoree.modeling.memory.chunk.impl;

import org.kevoree.modeling.KConfig;
import org.kevoree.modeling.memory.KChunkFlags;
import org.kevoree.modeling.memory.chunk.KObjectChunk;
import org.kevoree.modeling.memory.chunk.KObjectIndexChunk;
import org.kevoree.modeling.memory.chunk.KStringLongMapCallBack;
import org.kevoree.modeling.memory.space.KChunkSpace;
import org.kevoree.modeling.memory.space.KChunkTypes;
import org.kevoree.modeling.meta.KMetaClass;
import org.kevoree.modeling.meta.KMetaModel;
import org.kevoree.modeling.util.Base64;
import org.kevoree.modeling.util.PrimitiveHelper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class HeapObjectIndexChunk implements KObjectIndexChunk {

    protected volatile int elementCount;

    protected volatile int droppedCount;

    protected volatile InternalState state = null;

    protected int threshold;

    private final int initialCapacity = 16;

    private static final float loadFactor = ((float) 75 / (float) 100);

    private final AtomicLong _flags;

    private final AtomicInteger _counter;

    private final KChunkSpace _space;

    private final long _universe;

    private final long _time;

    private final long _obj;

    private int _metaClassIndex = -1;

    private AtomicReference<long[]> _dependencies;

    final class InternalState {

        public final int elementDataSize;

        public final String[] elementK;

        public final long[] elementV;

        public final int[] elementNext;

        public final int[] elementHash;

        public InternalState(int elementDataSize, String[] elementK, long[] elementV, int[] elementNext, int[] elementHash) {
            this.elementDataSize = elementDataSize;
            this.elementK = elementK;
            this.elementV = elementV;
            this.elementNext = elementNext;
            this.elementHash = elementHash;
        }

        public InternalState clone() {
            String[] clonedElementK = new String[elementK.length];
            System.arraycopy(elementK, 0, clonedElementK, 0, elementK.length);
            long[] clonedElementV = new long[elementV.length];
            System.arraycopy(elementV, 0, clonedElementV, 0, elementV.length);
            int[] clonedElementNext = new int[elementNext.length];
            System.arraycopy(elementNext, 0, clonedElementNext, 0, elementNext.length);
            int[] clonedElementHash = new int[elementHash.length];
            System.arraycopy(elementHash, 0, clonedElementHash, 0, elementHash.length);
            return new InternalState(elementDataSize, clonedElementK, clonedElementV, clonedElementNext, clonedElementHash);
        }
    }

    public HeapObjectIndexChunk(long p_universe, long p_time, long p_obj, KChunkSpace p_space) {
        this._universe = p_universe;
        this._time = p_time;
        this._obj = p_obj;
        this._flags = new AtomicLong(0);
        this._counter = new AtomicInteger(0);
        this._space = p_space;
        this.elementCount = 0;
        this.droppedCount = 0;
        InternalState newstate = new InternalState(initialCapacity, new String[initialCapacity], new long[initialCapacity], new int[initialCapacity], new int[initialCapacity]);
        for (int i = 0; i < initialCapacity; i++) {
            newstate.elementNext[i] = -1;
            newstate.elementHash[i] = -1;
        }
        this.state = newstate;
        this.threshold = (int) (newstate.elementDataSize * loadFactor);
        this._dependencies = new AtomicReference<long[]>();
    }

    @Override
    public KObjectChunk clone(long p_universe, long p_time, long p_obj, KMetaModel p_metaClass) {
        HeapObjectIndexChunk cloned = new HeapObjectIndexChunk(p_universe, p_time, p_obj, _space);
        cloned._metaClassIndex = this._metaClassIndex;
        cloned.state = this.state.clone();
        cloned.elementCount = this.elementCount;
        cloned.droppedCount = this.droppedCount;
        cloned.threshold = this.threshold;
        cloned.internal_set_dirty();
        return cloned;
    }

    @Override
    public int metaClassIndex() {
        return this._metaClassIndex;
    }

    @Override
    public String toJSON(KMetaModel metaModel) {
        return null;
    }

    @Override
    public void setPrimitiveType(int index, Object content, KMetaClass metaClass) {

    }

    @Override
    public Object getPrimitiveType(int index, KMetaClass metaClass) {
        return null;
    }

    @Override
    public long[] getLongArray(int index, KMetaClass metaClass) {
        return new long[0];
    }

    @Override
    public int getLongArraySize(int index, KMetaClass metaClass) {
        return 0;
    }

    @Override
    public long getLongArrayElem(int index, int refIndex, KMetaClass metaClass) {
        return 0;
    }

    @Override
    public boolean addLongToArray(int index, long newRef, KMetaClass metaClass) {
        return false;
    }

    @Override
    public boolean removeLongToArray(int index, long previousRef, KMetaClass metaClass) {
        return false;
    }

    @Override
    public void clearLongArray(int index, KMetaClass metaClass) {

    }

    @Override
    public double[] getDoubleArray(int index, KMetaClass metaClass) {
        return new double[0];
    }

    @Override
    public int getDoubleArraySize(int index, KMetaClass metaClass) {
        return 0;
    }

    @Override
    public double getDoubleArrayElem(int index, int arrayIndex, KMetaClass metaClass) {
        return 0;
    }

    @Override
    public void setDoubleArrayElem(int index, int arrayIndex, double valueToInsert, KMetaClass metaClass) {

    }

    @Override
    public void extendDoubleArray(int index, int newSize, KMetaClass metaClass) {

    }

    @Override
    public void clearDoubleArray(int index, KMetaClass metaClass) {

    }

    @Override
    public final int counter() {
        return this._counter.get();
    }

    @Override
    public final int inc() {
        return this._counter.incrementAndGet();
    }

    @Override
    public final int dec() {
        return this._counter.decrementAndGet();
    }

    public final void clear() {
        if (elementCount > 0) {
            this.elementCount = 0;
            this.droppedCount = 0;
            InternalState newstate = new InternalState(initialCapacity, new String[initialCapacity], new long[initialCapacity], new int[initialCapacity], new int[initialCapacity]);
            for (int i = 0; i < initialCapacity; i++) {
                newstate.elementNext[i] = -1;
                newstate.elementHash[i] = -1;
            }
            this.state = newstate;
            this.threshold = (int) (newstate.elementDataSize * loadFactor);
        }
    }

    protected final void rehashCapacity(int capacity) {
        int length = (capacity == 0 ? 1 : capacity << 1);
        String[] newElementK = new String[length * 2];
        long[] newElementV = new long[length * 2];
        System.arraycopy(state.elementK, 0, newElementK, 0, state.elementK.length);
        System.arraycopy(state.elementV, 0, newElementV, 0, state.elementV.length);
        int[] newElementNext = new int[length];
        int[] newElementHash = new int[length];
        for (int i = 0; i < length; i++) {
            newElementNext[i] = -1;
            newElementHash[i] = -1;
        }
        //rehashEveryThing
        for (int i = 0; i < state.elementNext.length; i++) {
            if (state.elementNext[i] != -1) { //there is a real value
                int index = (PrimitiveHelper.stringHash(state.elementK[i]) & 0x7FFFFFFF) % length;
                int currentHashedIndex = newElementHash[index];
                if (currentHashedIndex != -1) {
                    newElementNext[i] = currentHashedIndex;
                } else {
                    newElementNext[i] = -2; //special char to tag used values
                }
                newElementHash[index] = i;
            }
        }
        //setPrimitiveType value for all
        state = new InternalState(length, newElementK, newElementV, newElementNext, newElementHash);
        this.threshold = (int) (length * loadFactor);
    }

    @Override
    public final void each(KStringLongMapCallBack callback) {
        InternalState internalState = state;
        for (int i = 0; i < internalState.elementNext.length; i++) {
            if (internalState.elementNext[i] != -1) { //there is a real value
                callback.on(internalState.elementK[i], internalState.elementV[i]);
            }
        }
    }

    @Override
    public final boolean contains(String key) {
        InternalState internalState = state;
        if (state.elementDataSize == 0) {
            return false;
        }
        int hash = PrimitiveHelper.stringHash(key);
        int index = (hash & 0x7FFFFFFF) % internalState.elementDataSize;
        int m = internalState.elementHash[index];
        while (m >= 0) {
            if (key == internalState.elementK[m * 2] /* getKey */) {
                return true;
            }
            m = internalState.elementNext[m];
        }
        return false;
    }

    @Override
    public final long get(String key) {
        InternalState internalState = state;
        if (state.elementDataSize == 0) {
            return KConfig.NULL_LONG;
        }
        int index = (PrimitiveHelper.stringHash(key) & 0x7FFFFFFF) % internalState.elementDataSize;
        int m = internalState.elementHash[index];
        while (m >= 0) {
            if (PrimitiveHelper.equals(key, internalState.elementK[m] /* getKey */)) {
                return internalState.elementV[m]; /* getValue */
            } else {
                m = internalState.elementNext[m];
            }
        }
        return KConfig.NULL_LONG;
    }

    @Override
    public final synchronized void put(String key, long value) {
        int entry = -1;
        int index = -1;
        int hash = PrimitiveHelper.stringHash(key);
        if (state.elementDataSize != 0) {
            index = (hash & 0x7FFFFFFF) % state.elementDataSize;
            entry = findNonNullKeyEntry(key, index);
        }
        if (entry == -1) {
            if (++elementCount > threshold) {
                rehashCapacity(state.elementDataSize);
                index = (hash & 0x7FFFFFFF) % state.elementDataSize;
            }
            int newIndex = (this.elementCount + this.droppedCount - 1);
            state.elementK[newIndex] = key;
            state.elementV[newIndex] = value;
            int currentHashedIndex = state.elementHash[index];
            if (currentHashedIndex != -1) {
                state.elementNext[newIndex] = currentHashedIndex;
            } else {
                state.elementNext[newIndex] = -2; //special char to tag used values
            }
            //now the object is reachable to other thread everything should be ready
            state.elementHash[index] = newIndex;
        } else {
            state.elementV[entry] = value;/*setValue*/
        }
        internal_set_dirty();
    }

    final int findNonNullKeyEntry(String key, int index) {
        int m = state.elementHash[index];
        while (m >= 0) {
            if (PrimitiveHelper.equals(key, state.elementK[m] /* getKey */)) {
                return m;
            }
            m = state.elementNext[m];
        }
        return -1;
    }

    //TODO check intersection of remove and put
    @Override
    public synchronized final void remove(String key) {
        InternalState internalState = state;
        if (state.elementDataSize == 0) {
            return;
        }
        int index = (PrimitiveHelper.stringHash(key) & 0x7FFFFFFF) % internalState.elementDataSize;
        int m = state.elementHash[index];
        int last = -1;
        while (m >= 0) {
            if (PrimitiveHelper.equals(key, state.elementK[m] /* getKey */)) {
                break;
            }
            last = m;
            m = state.elementNext[m];
        }
        if (m == -1) {
            return;
        }
        if (last == -1) {
            if (state.elementNext[m] > 0) {
                state.elementHash[index] = m;
            } else {
                state.elementHash[index] = -1;
            }
        } else {
            state.elementNext[last] = state.elementNext[m];
        }
        state.elementNext[m] = -1;//flag to dropped value
        this.elementCount--;
        this.droppedCount++;
    }

    public final int size() {
        return this.elementCount;
    }

    /* warning: this method is not thread safe */
    @Override
    public void init(String payload, KMetaModel metaModel, int metaClassIndex) {
        if (this._metaClassIndex == -1) {
            this._metaClassIndex = metaClassIndex;
        }
        if (payload == null || payload.length() == 0) {
            return;
        }
        int initPos = 1;
        int cursor = 0;
        while (cursor < payload.length() && payload.charAt(cursor) != '/') {
            cursor++;
        }
        int nbElement = Base64.decodeToIntWithBounds(payload, initPos, cursor);
        //reset the map
        int length = (nbElement == 0 ? 1 : nbElement << 1);
        String[] newElementK = new String[length];
        long[] newElementV = new long[length];
        int[] newElementNext = new int[length];
        int[] newElementHash = new int[length];
        for (int i = 0; i < length; i++) {
            newElementNext[i] = -1;
            newElementHash[i] = -1;
        }
        //setPrimitiveType value for all
        InternalState temp_state = new InternalState(length, newElementK, newElementV, newElementNext, newElementHash);
        if (nbElement > 0) {
            while (cursor < payload.length()) {
                cursor++;
                int beginChunk = cursor;
                while (cursor < payload.length() && payload.charAt(cursor) != ':') {
                    cursor++;
                }
                int middleChunk = cursor;
                while (cursor < payload.length() && payload.charAt(cursor) != ',') {
                    cursor++;
                }
                String loopKey = Base64.decodeToStringWithBounds(payload, beginChunk, middleChunk);
                long loopVal = Base64.decodeToLongWithBounds(payload, middleChunk + 1, cursor);
                int index = (PrimitiveHelper.stringHash(loopKey) & 0x7FFFFFFF) % temp_state.elementDataSize;
                //insert K/V
                int newIndex = this.elementCount;
                temp_state.elementK[newIndex] = loopKey;
                temp_state.elementV[newIndex] = loopVal;
                int currentHashedIndex = temp_state.elementHash[index];
                if (currentHashedIndex != -1) {
                    temp_state.elementNext[newIndex] = currentHashedIndex;
                } else {
                    temp_state.elementNext[newIndex] = -2; //special char to tag used values
                }
                temp_state.elementHash[index] = newIndex;
                this.elementCount++;
            }
        }
        this.elementCount = nbElement;
        this.droppedCount = 0;
        this.state = temp_state;//TODO check with CnS
        this.threshold = (int) (length * loadFactor);

    }

    @Override
    public String serialize(KMetaModel metaModel) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("#");
        Base64.encodeIntToBuffer(elementCount, buffer);
        buffer.append('/');
        boolean isFirst = true;
        HeapObjectIndexChunk.InternalState internalState = state;
        for (int i = 0; i < internalState.elementNext.length; i++) {
            if (internalState.elementNext[i] != -1) { //there is a real value
                String loopKey = internalState.elementK[i];
                long loopValue = internalState.elementV[i];
                if (!isFirst) {
                    buffer.append(",");
                }
                isFirst = false;
                Base64.encodeStringToBuffer(loopKey, buffer);
                buffer.append(":");
                Base64.encodeLongToBuffer(loopValue, buffer);
            }
        }
        return buffer.toString();
    }

    @Override
    public void free(KMetaModel metaModel) {
        clear();
    }

    @Override
    public short type() {
        return KChunkTypes.LONG_LONG_MAP;
    }

    @Override
    public KChunkSpace space() {
        return _space;
    }

    private void internal_set_dirty() {
        if (_space != null) {
            if ((_flags.get() & KChunkFlags.DIRTY_BIT) != KChunkFlags.DIRTY_BIT) {
                _space.declareDirty(this);
                //the synchronization risk is minim here, at worse the object will be saved twice for the next iteration
                setFlags(KChunkFlags.DIRTY_BIT, 0);
            }
        } else {
            setFlags(KChunkFlags.DIRTY_BIT, 0);
        }
    }

    @Override
    public long getFlags() {
        return _flags.get();
    }

    @Override
    public void setFlags(long bitsToEnable, long bitsToDisable) {
        long val;
        long nval;
        do {
            val = _flags.get();
            nval = val & ~bitsToDisable | bitsToEnable;
        } while (!_flags.compareAndSet(val, nval));
    }

    @Override
    public long universe() {
        return this._universe;
    }

    @Override
    public long time() {
        return this._time;
    }

    @Override
    public long obj() {
        return this._obj;
    }

    @Override
    public long[] dependencies() {
        return this._dependencies.get();
    }

    @Override
    public void addDependency(long universe, long time, long uuid) {
        long[] previousVal;
        long[] newVal;
        do {
            previousVal = _dependencies.get();
            if (previousVal == null) {
                newVal = new long[]{universe, time, uuid};
            } else {
                newVal = new long[previousVal.length + 3];
                int previousLength = previousVal.length;
                System.arraycopy(previousVal, 0, newVal, 0, previousLength);
                newVal[previousLength] = universe;
                newVal[previousLength + 1] = time;
                newVal[previousLength + 2] = uuid;
            }
        } while (!_dependencies.compareAndSet(previousVal, newVal));
    }
}



