package org.kevoree.modeling.memory.space;

import org.kevoree.modeling.memory.KChunk;
import org.kevoree.modeling.memory.chunk.KObjectChunk;
import org.kevoree.modeling.memory.manager.KDataManager;
import org.kevoree.modeling.meta.KMetaModel;

public interface KChunkSpace {

    void setManager(KDataManager dataManager);

    KChunk get(long universe, long time, long obj);

    KChunk create(long universe, long time, long obj, short type, KMetaModel metaModel);

    KObjectChunk clone(KObjectChunk previousElement, long newUniverse, long newTime, long newObj, KMetaModel metaModel);

    void clear(KMetaModel metaModel);

    void free(KMetaModel metaModel);

    void remove(long universe, long time, long obj, KMetaModel metaModel);

    int size();

    KChunkIterator detachDirties();

    void declareDirty(KChunk dirtyChunk);

    void printDebug(KMetaModel p_metaModel);

}
