import socket
from _thread import *
import time

HOST = '127.0.0.1'
PORT = 8080

def threaded(conn, id):
    data = "1\n" + str(id) +"\n"

    conn.send(data.encode())
    count = 0 
    while True:
        data = conn.recv(1024).decode()
        if not data:
            break
        print(data)
        print()
        break
    
    time.sleep(3)
    data = "4\na\n\n"

    conn.send(data.encode())
    print("send answer\n")
    while True:
        data = conn.recv(1024).decode()
        if not data:
            break
        print(data)
        print()

    conn.close()

for i in range(1):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((HOST, PORT))

    start_new_thread(threaded, (s, i))

while True:
    time.sleep(5)
