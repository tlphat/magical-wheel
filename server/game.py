import time
from event_manager import EventManager
from network_manager import NetworkManager
from player_manager import PlayerManager
from player import Player
from constants import *
from config import *
from utilities import *


class Game:
    def __init__(self):
        self.network_manager = NetworkManager(self)
        self.event_manager = EventManager(self, self.network_manager)
        self.player_manager = PlayerManager(self)
        self.state = GameState.WAITING
        self.start_turn_time = None
        self.turn = -1
        self.keyword = None
        self.hint = None
        self.guested_keyword = None

    def run(self):
        while not self.state == GameState.END:
            self.event_manager.run()

            if self.state == GameState.WAITING:
                if self.player_manager.full_player():
                    self.start_game()
            else:
                self.update_logic()

            time.sleep(0.5) 

    def update_logic(self):
        if self.state == GameState.PRE_START:
            if self.getElapsedTime() >= WAITING_TIME_TO_START:
                self.state = GameState.START
                self.next_turn()
                return


        elapsedTime = self.getElapsedTime()

        if elapsedTime >= TIME_PER_TURN:
            self.next_turn()
            return

    def next_turn(self):
        def create_response():
            response_content = [EventType["START_TURN"], next_player.username, next_player.remaining_turn]
            return ResponseData(response_content, next_player.connection_id)
        
        self.turn += 1

        next_player = self.player_manager.get_next_player()
        next_player = None
        if next_player is None:
            self.end_game()
            return
        
        next_player.take_turn()

        self.publish_response(PUBLIC_RESPONSE, create_response())

        self.start_turn_time = time.time()

    def start_game(self):
        def create_response():
            response_content = [EventType["START_GAME"], len(self.keyword), self.hint]
            self.player_manager.wrap_to_response(response_content)
            
            return ResponseData(response_content, None)
            
        self.keyword, self.hint = load_keyword_and_hint()
        self.guested_keyword = "*" * len(self.keyword)
        print("Game Start")
        print("Keyword: ", self.keyword)
        print("Guested: ", self.guested_keyword)
        self.state = GameState.PRE_START

        self.publish_response(PUBLIC_RESPONSE, create_response())

        self.start_turn_time = time.time()

    def end_game(self):
        def create_response():
            is_complete_keyword = self.keyword == self.guested_keyword
            correct_guest_username = self.player_manager.cur_player.username if is_complete_keyword else ""
            response_content = [EventType["END_GAME"], is_complete_keyword, correct_guest_username]
            self.player_manager.wrap_score_to_response(response_content)
            return ResponseData(response_content, None)

        self.state = GameState.END

        print("End Game")
        
        self.publish_response(PUBLIC_RESPONSE, create_response())

    def join_game_handler(self, event_data):
        def create_success_response():
            response_content = [EventType["JOIN_GAME"], StatusCode["SUCCESS"], NUM_PLAYER]
            self.player_manager.wrap_to_response(response_content)
            
            return ResponseData(response_content, connection_id)
        
        def create_fail_response(code):
            response_content = [EventType["JOIN_GAME"], StatusCode[code]]
            return ResponseData(response_content, connection_id)

        content = event_data.content
        connection_id = event_data.connection_id
        username = content[0]

        err_code = None
        if self.player_manager.full_player():
            erro_code = "FULL_CONNECTION"
        elif not is_valid_username(username):
            err_code = "USERNAME_INVALID"
        elif self.player_manager.username_existed(username):
            err_code = "USERNAME_EXISTED"

        if not err_code is None:
            response_data = create_fail_response(err_code)
            self.publish_response(PRIVATE_RESPONSE, response_data)
            return

        self.network_manager.accept_connection(connection_id)
        self.player_manager.accept_player(username, connection_id)
        response_data = create_success_response()

        self.publish_response(PUBLIC_RESPONSE, response_data)
        
    def publish_response(self, send_type, response_data):
        self.event_manager.push_response(Response(send_type, response_data))

    def getElapsedTime(self):
        return time.time() - self.start_turn_time
