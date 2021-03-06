package org.kevoree.modeling.memory.space.impl;

import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.memory.manager.KDataManager;
import org.kevoree.modeling.memory.space.BaseKChunkSpaceCleanerTest;
import org.kevoree.modeling.memory.space.impl.press.PressOffHeapChunkSpace;

/**
 * @ignore ts
 */
public class OffHeapChunkSpaceCleanerTest extends BaseKChunkSpaceCleanerTest {

    @Override
    public KDataManager createDataManager() {
        return new DataManagerBuilder().withSpace(new PressOffHeapChunkSpace(1000)).build();
    }
}
