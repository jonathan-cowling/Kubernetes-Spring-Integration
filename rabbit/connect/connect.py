#!/usr/bin/env python3
import pika
import os
import time

tries = 10

for i in range(tries + 1):
    try:
        pika.BlockingConnection(
            pika.ConnectionParameters(host=os.environ['RABBIT_HOST'],
                                      port=int(os.environ['RABBIT_PORT']),
                                      credentials=pika.credentials.PlainCredentials(os.environ['RABBIT_USERNAME'],
                                                                                    os.environ['RABBIT_PASSWORD'],
                                                                                    True
                                                                                    )
                                      )
        )
        break
    except pika.connection.exceptions.AMQPConnectionError:
        if i == tries:
            raise TimeoutError
        else:
            time.sleep(1)
