#!/usr/bin/env python

from kazoo.client import KazooClient
import logging
import time
import string
import random

logging.basicConfig()

zk = KazooClient(hosts='127.0.0.1:2181')
zk.start()

# Ensure a path, create if necessary
zk.ensure_path("/andor/load/test")

# Create a node with data
random.seed()
start = time.time()
future = start + 1
NUM = 100000
cnt = 0

for x in range(0, NUM):  
  data = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(random.randint(10, 500)))
  zk.set("/andor/load/test", data)
  cnt = cnt + 1
  now = time.time()
  if now >= future:    
    future = now + 1
    print("{} = {} tps".format(x, cnt))
    cnt = 0
print("done.")

zk.stop()
