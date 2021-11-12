from config import *
from constants import *

class NetworkManager:
    def __init__(self, game):
        self.game = game
        self.connections = {}
        self.accepted_connections = []
        self.connection_counter = 0

    def is_expire_connection(self, connection_id):
        return len(self.accepted_connections) == NUM_PLAYER and connection_id not in self.accepted_connections

    def is_open_for_connection(self):
        return self.game.state == GameState.WAITING and len(self.connections) < MAX_PENDING_CONNECTION and len(self.accepted_connections) < NUM_PLAYER

    def add_new_connection(self, channel):
        if not self.is_open_for_connection():
            return None

        self.connection_counter += 1
        self.connections[self.connection_counter] = channel
        return self.connection_counter

    def accept_connection(self, connection_id):
        self.accepted_connections.append(connection_id)

    
    def remove_connection(self, connection_id):
        self.connections.pop(connection_id, None)

    def send(self, response):
        print("Sending resonse: ", response)
        send_type = response.send_type

        response_data = response.data
        content = response_data.content
        connection_id = response_data.connection_id

        if isinstance(content, list):
            content = list(map(str, content))
            content = '\n'.join(content)

        if send_type == PUBLIC_RESPONSE:
            for connection_id in self.accepted_connections:
                self.send_to(connection_id, content)
        else:
            self.send_to(connection_id, content)

    def send_to(self, connection_id, content):
        if not connection_id in self.connections:
            print("Missing connection to: " + str(connection_id))
            return

        conn = self.connections[connection_id]
        conn.send(content.encode())
