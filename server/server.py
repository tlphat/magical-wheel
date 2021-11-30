import socket, select
from _thread import start_new_thread
import time
from game import Game
from config import *
from constants import *

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setblocking(False)


while True:
    try:
        server.bind((HOST, PORT))
        server.listen()
        print("Server Started! Waiting for connection...")
        break
    except socket.error as err:
        print(err)
        time.sleep(1)

def run():
    game = Game()
    start_new_thread(game.run, ())

    network = game.network_manager
    network.inputs.append(server)

    while not game.ended() or len(network.outputs) > 0:
        readable, writable, exceptional = select.select(network.inputs, network.outputs, network.inputs, TIME_OUT)

        for s in readable:
            if s is server:
                conn, addr = s.accept()
                conn.setblocking(0)

                socket_id = network.add_new_socket(conn)
                if socket_id is None:
                    continue

                print("Establishment new connection to: ", addr)

                network.inputs.append(conn)
            else:
                if network.is_expire_socket(s):
                    network.close_socket(s)
                    continue
                
                try:
                    data = s.recv(4096).decode()
                except ConnectionResetError:
                    network.close_socket(s)
                    continue

                if not data:
                    network.close_socket(s)
                    continue

                game.event_manager.push_request(RawRequestData(data, s))

        for s in writable:
            next_msg = game.network_manager.get_message(s)
            if not next_msg is None:
                s.send(next_msg)

        for s in exceptional:
            network.close_socket(s)

num_game = 1
while True:
    print("Creating Game: ", num_game)
    run()
    num_game += 1
