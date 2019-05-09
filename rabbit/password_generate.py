#!/usr/bin/env python3
# rabbitMQ password hashing algo as laid out in: http://lists.rabbitmq.com/pipermail/rabbitmq-discuss/2011-May/012765.html
# from https://gist.github.com/komuw/c6fb1a1c757afb43fe69bdd736d5cf63

from getpass import getpass
import base64
import os
import hashlib
import struct
import sys
import select

if sys.stdin in select.select([sys.stdin], [], [], 0)[0]:
    password = sys.stdin.read()
else:
  password = getpass(prompt="password: ")
  confirm = getpass(prompt="confirm: ")

  if password != confirm:
    raise Error("passwords do not match")

salt = os.urandom(4)

salted_hash = salt + hashlib.sha256(salt + password.encode('utf-8')).digest()

encoded_hash = base64.b64encode(salted_hash)

print(encoded_hash.decode('utf-8'))
