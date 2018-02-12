package org.zstack.test.integration.core

import org.zstack.core.asyncbatch.While
import org.zstack.header.core.FutureCompletion
import org.zstack.header.core.NoErrorCompletion
import org.zstack.header.core.workflow.WhileCompletion
import org.zstack.testlib.SubCase
import org.zstack.testlib.util.TimeUnitUtil
import org.zstack.utils.TimeUtils
import org.zstack.utils.Utils
import org.zstack.utils.logging.CLogger

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by MaJin on 2017-05-02.
 */
class WhileCase extends SubCase{
    private static final CLogger logger = Utils.getLogger(WhileCase.class)
    static TIME_OUT = TimeUnit.SECONDS.toMillis(1)

    @Override
    void clean() {

    }

    @Override
    void setup() {
        INCLUDE_CORE_SERVICES = false
    }

    @Override
    void environment() {

    }

    @Override
    void test() {
        testRunAllWhenItemsEmpty()
        testRunAllCompletionAllDone()
        testRunStepWhenItemsEmpty()
        testRunStepCompletionDone()
        testRunStepComletionAllDone()
    }

    static void testRunAllWhenItemsEmpty(){
        FutureCompletion future = new FutureCompletion(null)

        new While<>(new ArrayList<String>()).all(new While.Do<String>() {
            @Override
            void accept(String item, WhileCompletion completion) {
                completion.done()
            }
        }).run(new NoErrorCompletion(){

            @Override
            void done() {
                future.success()
            }
        })

        future.await(TIME_OUT)

        assert future.success
    }

    static void testRunAllCompletionAllDone(){
        FutureCompletion future = new FutureCompletion(null)
        AtomicInteger count = new AtomicInteger(0)

        new While<>(["1", "2"]).all({item, completion ->
            logger.debug(String.format("item %s allDone", item))
            completion.allDone()
        }).run(new NoErrorCompletion(){
            @Override
            void done() {
                count.addAndGet(1)
                logger.debug("While is done")
                future.success()
            }
        })

        future.await(TIME_OUT)
        assert future.success
        assert count.get() == 1
    }

    static void testRunStepWhenItemsEmpty(){
        FutureCompletion future = new FutureCompletion(null)

        new While<>(new ArrayList<String>()).step({item, completion ->
            completion.done()
        }, 1).run(new NoErrorCompletion(){
            @Override
            void done() {
                future.success()
            }
        })

        future.await(TIME_OUT)
        assert future.success
    }

    static void testRunStepCompletionDone(){
        FutureCompletion future = new FutureCompletion(null)
        AtomicInteger whileDoneCount = new AtomicInteger(0)
        AtomicInteger itemDoneCount = new AtomicInteger(0)

        new While<>([1, 2, 3]).step({item, completion ->
            Thread.start {
                logger.debug("step " + item)
                TimeUnitUtil.sleepMilliseconds(TimeUnit.SECONDS.toMillis(item as long)/5 as long)
                itemDoneCount.addAndGet(1)
                completion.done()
            }
        }, 2).run(new NoErrorCompletion(){
            @Override
            void done() {
                whileDoneCount.addAndGet(1)
                logger.debug("While is done")
                future.success()
            }
        })

        future.await(TIME_OUT)
        assert future.success
        assert whileDoneCount.get() == 1
        assert itemDoneCount.get() == 3
    }

    static void testRunStepComletionAllDone(){
        FutureCompletion future = new FutureCompletion(null)
        AtomicInteger count = new AtomicInteger(0)

        new While<>(["1", "2"]).step({item, completion ->
            logger.debug(String.format("step %s allDone", item))
            completion.allDone()
        }, 2).run(new NoErrorCompletion(){
            @Override
            void done() {
                count.addAndGet(1)
                logger.debug("While is done")
                future.success()
            }
        })

        future.await(TIME_OUT)
        assert future.success
        assert count.get() == 1
    }
}
