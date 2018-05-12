/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.andor.zktest.sentryleader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LeaderStatusMonitor extends LeaderSelectorListenerAdapter implements AutoCloseable {
    private static final Log LOG =
            LogFactory.getLog(LeaderStatusMonitor.class);
    private static final String path = "/andor_sentr_leader";

    private final ReentrantLock lock;
    private final Condition cond;
    private final CuratorFramework client;

    private LeaderSelector leaderSelector;
    private boolean isLeader = false;

    LeaderStatusMonitor(CuratorFramework client) {
        this.client = client;
        lock = new ReentrantLock();
        cond = lock.newCondition();

        LOG.info("Created LeaderStatusMonitor");
    }

    void start() {
        leaderSelector = new LeaderSelector(client, path, this);
        leaderSelector.start();
    }

    void stop() {
        lock.lock();
        try {
            cond.signal();
        } finally {
            lock.unlock();
        }
    }

    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        LOG.info("LeaderStatusMonitor: becoming active.");
        lock.lock();
        try {
            isLeader = true;
            cond.await();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            LOG.info("LeaderStatusMonitor: interrupted");
        } finally {
            isLeader = false;
            lock.unlock();
            LOG.info("LeaderStatusMonitor: becoming standby");
        }
    }

    public void close() {
        if (leaderSelector != null) {
            leaderSelector.close();
        }
    }

    public boolean isLeader() {
        lock.lock();
        boolean leader = isLeader;
        lock.unlock();
        return leader;
    }
}
