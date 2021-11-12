import time
from event_manager import EventManager
from network_manager import NetworkManager
from player import Player
from constants import *
from config import *
from utilities import *


class Game:
    def __init__(self):
        self.network_manager = NetworkManager(self)
        self.event_manager = EventManager(self, self.network_manager)
        self.players = []
        self.start = False

    def run(self):
        while True:
            self.event_manager.run()

            time.sleep(0.5) 

    
    def started(self):
        return self.start

    def full_player(self):
        return len(self.players) >= NUM_PLAYER

    def username_existed(self, username):
        for player in self.players:
            if player.username == username:
                return True

        return False

    def join_game_handler(self, event_data):
        def create_success_response():
            response_content = [EventType["JOIN_GAME"], StatusCode["SUCCESS"], NUM_PLAYER]
            response_content.append(len(self.players))
            for player in self.players:
                response_content.append(player.username)
            
            return ResponseData(response_content, connection_id)
        
        def create_fail_response(code):
            response_content = [EventType["JOIN_GAME"], StatusCode[code]]
            return ResponseData(response_content, connection_id)

        content = event_data.content
        connection_id = event_data.connection_id
        username = content[0]

        err_code = None
        if self.full_player():
            erro_code = "FULL_CONNECTION"
        elif not is_valid_username(username):
            err_code = "USERNAME_INVALID"
        elif self.username_existed(username):
            err_code = "USERNAME_EXISTED"

        if not err_code is None:
            response_data = create_fail_response(err_code)
            self.event_manager.push_response(Response(PRIVATE_RESPONSE, response_data))
            return

        self.network_manager.accept_connection(connection_id)
        player = Player(self, username, connection_id)
        self.players.append(player)
        response_data = create_success_response()
        self.event_manager.push_response(Response(PUBLIC_RESPONSE, response_data))
        
