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
        self.turn = 0
        self.round = 1
        self.keyword = None
        self.origin_keyword = None
        self.hint = None
        self.guested_keyword = None
        self.is_pending_next_turn = False
        self.is_guested_keyword = False
        self.is_pending_response = False

    def run(self):
        while not self.state == GameState.END:
            self.event_manager.process_request_queue()
            self.event_manager.process_response_queue()

            if self.state == GameState.WAITING:
                if self.player_manager.full_player():
                    self.start_game()
            else:
                self.update_logic()

            self.event_manager.post_process_response_queue()

            time.sleep(0.25) 


    def update_logic(self):
        if self.state == GameState.PRE_START:
            if self.get_elapsed_time() >= WAITING_TIME_TO_START:
                self.state = GameState.START
                self.next_turn()
            return


        
        elapsed_time = self.get_elapsed_time()

        if elapsed_time >= TIME_PER_TURN or self.is_pending_next_turn:
            if self.is_pending_response:
                self.is_pending_response = False
                self.send_dummy_response()
                return

            if self.is_end_game():
                self.end_game()
            else:
                self.next_turn()
            return


    def next_turn(self):
        def create_response():
            response_content = [EventType["START_TURN"], next_player.username, self.round]
            return ResponseData(response_content, next_player.socket_id)
        
        next_player = self.player_manager.get_next_player()

        if (self.turn == NUM_PLAYER):
            self.round += 1
            self.turn = 1
        else:
            self.turn += 1

        print("Round {} Turn: {}".format(self.round, self.turn))
        print("Keyword: ", self.origin_keyword)
        print("Guested: ", self.guested_keyword)
        print("Player: ", next_player.username)

        self.publish_response(PUBLIC_RESPONSE, create_response())

        self.is_pending_next_turn = False
        self.is_pending_response = True
        self.start_turn_time = time.time()

    def start_game(self):
        def create_response():
            response_content = [EventType["START_GAME"], len(self.keyword), self.hint, TIME_PER_TURN - 0.75, NUM_ROUND]
            self.player_manager.wrap_to_response(response_content)
            
            return ResponseData(response_content, None)
            
        self.keyword, self.hint = load_keyword_and_hint()
        self.keyword = self.keyword.upper()
        self.origin_keyword = self.keyword
        self.guested_keyword = "*" * len(self.keyword)
        print("Game Start")
        print("Keyword: ", self.keyword)
        print("Guested: ", self.guested_keyword)
        self.state = GameState.PRE_START

        self.publish_response(PUBLIC_RESPONSE, create_response())

        self.start_turn_time = time.time()

    def end_game(self):
        def create_response():
            is_complete_keyword = 1 if self.is_guested_keyword else 0
            correct_guest_username = self.player_manager.cur_player.username if is_complete_keyword else ""
            response_content = [EventType["END_GAME"], is_complete_keyword, correct_guest_username, self.origin_keyword]
            self.player_manager.wrap_score_to_response(response_content)
            return ResponseData(response_content, None)


        self.state = GameState.END

        self.publish_response(PUBLIC_RESPONSE, create_response())

    def join_game_handler(self, event_data):
        def create_success_response():
            response_content = [EventType["JOIN_GAME"], StatusCode["SUCCESS"], NUM_PLAYER]
            self.player_manager.wrap_to_response(response_content)
            
            return ResponseData(response_content, socket_id)
        
        def create_fail_response(code):
            response_content = [EventType["JOIN_GAME"], StatusCode[code]]
            return ResponseData(response_content, socket_id)

        content = event_data.content
        socket_id = event_data.socket_id
        username = content[0]

        err_code = None
        if self.player_manager.full_player():
            err_code = "FULL_CONNECTION"
        elif not is_valid_username(username):
            err_code = "USERNAME_INVALID"
        elif self.player_manager.username_existed(username):
            err_code = "USERNAME_EXISTED"
        elif not self.network_manager.accept_socket(socket_id):
            err_code = "SERVER_ERROR"

        if not err_code is None:
            self.publish_response(PRIVATE_RESPONSE, create_fail_response(err_code))
            return

        self.player_manager.accept_player(username, socket_id)

        self.publish_response(PUBLIC_RESPONSE, create_success_response())

    def receive_guest_handler(self, request_data):
        def create_response():
            response_content = [EventType["PLAYER_GUEST"], current_player.username, guest_char, guest_keyword, self.guested_keyword, current_player.score, is_correct_keyword, 1 if self.is_end_game() else 0]
            return ResponseData(response_content, socket_id)

        content = request_data.content
        socket_id = request_data.socket_id
        guest_char = content[0][0] if content[0] else ""
        guest_keyword = content[1] if len(content) > 1 else ""

        if not self.state == GameState.START:
            return

        if not self.player_manager.is_current_turn_for_socket_id(socket_id):
            print("Incorrect player order")
            return

        current_player = self.player_manager.cur_player
        num_correct_char = 0
        is_correct_keyword = 0

        if guest_char:
            guest_char = guest_char.upper()
            num_correct_char = self.keyword.count(guest_char)
            if num_correct_char > 0:
                current_player.update_score(1)
                self.player_manager.set_next_player(current_player)
                self.round += 1
                self.turn = 0

                self.keyword = self.keyword.replace(guest_char, '')
                for i in range(len(self.origin_keyword)):
                    if self.origin_keyword[i] == guest_char:
                        self.guested_keyword = self.guested_keyword[:i] + guest_char + self.guested_keyword[i + 1:]

        if guest_keyword and (self.round > 1 or self.turn > 1):
            guest_keyword = guest_keyword.upper()
            if guest_keyword == self.origin_keyword:
                current_player.update_score(5)
                is_correct_keyword = 1
                self.is_guested_keyword = True

            current_player.eliminate()

        self.is_pending_next_turn = True
        self.is_pending_response = False

        return self.publish_response(PUBLIC_RESPONSE, create_response())

        
    def send_dummy_response(self):
        def create_response():
            current_player = self.player_manager.cur_player
            response_content = [EventType["PLAYER_GUEST"], current_player.username, '', '', self.guested_keyword, current_player.score, 0, 1 if self.is_end_game() else 0]
            return ResponseData(response_content, None)
            
        return self.publish_response(PUBLIC_RESPONSE, create_response())

    def publish_response(self, send_type, response_data):
        self.event_manager.push_response(Response(send_type, response_data))

    def get_elapsed_time(self):
        return time.time() - self.start_turn_time

    def is_end_game(self):
        return self.is_guested_keyword or self.round > NUM_ROUND or (self.turn == NUM_PLAYER and self.round + 1 > NUM_ROUND) or not self.player_manager.has_next_player()
    
    def ended(self):
        return self.state == GameState.END
