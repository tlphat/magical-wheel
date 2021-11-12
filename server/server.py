import socket
from _thread import *
import time
from game import Game
from config import *
from constants import *

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

while True:
    try:
        sock.bind((HOST, PORT))
        sock.listen()
        print("Server Started! Waiting for connection...")
        break
    except socket.error as err:
        print(err)
        time.sleep(1)

def threaded_client(conn, addr, game, connection_id):
    while True:
        try:
            if game.network_manager.is_expire_connection(connection_id):
                break

            data = conn.recv(4096).decode()
            if not data:
                break

            game.event_manager.push_request(RawRequestData(data, connection_id))
        except Exception as e:
            print(e)
            break

    print("Lost connection to: ", addr)
    game.network_manager.remove_connection(connection_id)
    conn.close()

def run():
    game = Game()
    start_new_thread(game.run, ())

    while game.network_manager.is_open_for_connection():
        conn, addr = sock.accept()

        connection_id = game.network_manager.add_new_connection(conn)
        if connection_id is None:
            continue

        print("Establishment new connection to: ", addr)

        start_new_thread(threaded_client, (conn, addr, game, connection_id))

        
run()
