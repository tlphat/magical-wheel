from config import *
from constants import *
import queue

class NetworkManager:
    def __init__(self, game):
        self.game = game
        self.socket_to_id = {}
        self.accepted_sockets = []
        self.alive_sockets = []
        self.socket_counter = 0
        self.inputs = []
        self.outputs = []
        self.message_queues = {}

    def is_expire_socket(self, sock):
        return len(self.accepted_sockets) == NUM_PLAYER and sock not in self.accepted_sockets

    def is_open_for_connection(self):
        return self.game.state == GameState.WAITING and len(self.socket_to_id) < MAX_PENDING_CONNECTION and len(self.accepted_sockets) < NUM_PLAYER

    def add_new_socket(self, sock):
        if not self.is_open_for_connection():
            return None

        self.socket_counter += 1
        self.socket_to_id[sock] = self.socket_counter
        self.alive_sockets.append(sock)
        self.message_queues[sock] = queue.Queue()
        return self.socket_counter

    def accept_socket(self, sock):
        if isinstance(sock, int):
            sock = self.get_socket(sock)

        if sock is None:
            return False

        self.accepted_sockets.append(sock)

        return True

    def full_connection(self):
        return len(self.accepted_sockets) == NUM_PLAYER

    def close_socket(self, sock):
        if sock in self.outputs:
            self.outputs.remove(sock)
        if sock in self.alive_sockets:
            self.alive_sockets.remove(sock)


        self.inputs.remove(sock)
        self.message_queues.pop(sock, None)
        if sock in self.accepted_sockets:
            dis_player = self.get_player_by_sock(sock)
            dis_player.eliminate()
            response_content = [EventType["PLAYER_LEAVE_GAME"], dis_player.username]
            self.game.event_manager.push_response(Response(PUBLIC_RESPONSE, ResponseData(response_content, None)))
            self.accepted_sockets.remove(sock)
            
        sock.close()

    def get_player_by_sock(self, sock):
        return self.game.player_manager.get_player_by_socket_id(self.get_socket_id(sock))

    def get_socket_id(self, sock):
        return self.socket_to_id[sock]

    def get_socket(self, socket_id):
        for s, id in self.socket_to_id.items():
            if id == socket_id:
                return s

        return None

    def get_message(self, s):
        try:
            next_msg = self.message_queues[s].get_nowait()
        except Exception as e:
            self.outputs.remove(s)
            print(e)
            return None
        else:
            return next_msg

    def send(self, response):
        print("Sending resonse: ", response)
        send_type = response.send_type

        response_data = response.data
        content = response_data.content
        sock_id = response_data.socket_id

        if isinstance(content, list):
            content = list(map(str, content))
            content = '\n'.join(content)

        if send_type == PUBLIC_RESPONSE:
            for sock in self.accepted_sockets:
                self.send_to(sock, content)
        else:
            self.send_to(sock_id, content)

    def send_to(self, sock, content):
        if isinstance(sock, int):
            sock_id = sock
            sock = self.get_socket(sock_id)
            if sock is None:
                print("Missing connection to: ", sock_id)
                return

        if not sock in self.alive_sockets:
            print("Missing connection to: " + str(self.socket_to_id[sock]))
            return
        if sock not in self.outputs:
            self.outputs.append(sock)

        self.message_queues[sock].put(content.encode())
