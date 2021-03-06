package org.kevoree.modeling.memory.space.impl;

import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.KChunk;
import org.kevoree.modeling.memory.chunk.KObjectChunk;
import org.kevoree.modeling.memory.resolver.KResolver;
import org.kevoree.modeling.memory.space.KChunkSpace;
import org.kevoree.modeling.memory.space.KChunkSpaceManager;
import org.kevoree.modeling.meta.KMetaModel;

public class NoopChunkSpaceManager implements KChunkSpaceManager {

    private KChunkSpace _space;

    @Override
    public void setSpace(KChunkSpace p_space) {
        this._space = p_space;
    }

    @Override
    public KChunk getAndMark(long universe, long time, long obj) {
        return this._space.get(universe, time, obj);
    }

    @Override
    public void unmark(long universe, long time, long obj) {

    }

    @Override
    public KChunk createAndMark(long universe, long time, long obj, short type) {
        return this._space.create(universe, time, obj, type, _metaModel);
    }

    @Override
    public void unmarkMemoryElement(KChunk element) {

    }

    @Override
    public void markMemoryElement(KChunk element) {

    }

    @Override
    public void unmarkAllMemoryElements(KChunk[] elements) {

    }

    @Override
    public KObjectChunk cloneAndMark(KObjectChunk previous, long newUniverse, long newTime, long obj, KMetaModel metaModel) {
        return this._space.clone(previous, newUniverse, newTime, obj, metaModel);
    }

    @Override
    public void clear() {

    }

    private KMetaModel _metaModel;

    @Override
    public void register(KObject kobj) {
        if (_metaModel == null) {
            _metaModel = kobj.manager().model().metaModel();
        }
    }

    @Override
    public void registerAll(KObject[] objects) {

    }

}
