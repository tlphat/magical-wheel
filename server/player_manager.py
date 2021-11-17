from config import *
from player import Player

class PlayerManager:
    def __init__(self, game):
        self.game = game
        self.cur_player_idx = -1
        self.cur_player = None
        self.next_player = None
        self.players = []
        self.num_player = 0

    def has_next_player(self):
        posible_next_player_idx = self.get_player_idx(self.cur_player) if self.cur_player else 0

        count = 0
        while count < self.num_player:
            if not self.players[posible_next_player_idx].eliminated():
                return True

            posible_next_player_idx += 1
            posible_next_player_idx %= self.num_player
            count += 1

        return False

    def set_next_player(self, player):
        self.next_player = player

    def get_player_idx(self, player):
        for idx, p in enumerate(self.players):
            if p == player:
                return idx

        return None

    def get_next_player(self):
        if self.next_player and not self.next_player.eliminated():
            self.cur_player = self.next_player
            self.next_player = None
            return self.cur_player
        else:
            self.next_player = None

        posible_next_player_idx = self.get_player_idx(self.cur_player) if self.cur_player else 0

        count = 0
        while count < self.num_player:
            if not self.players[posible_next_player_idx].eliminated():
                self.cur_player = self.players[posible_next_player_idx]
                return self.cur_player

            posible_next_player_idx += 1
            posible_next_player_idx %= self.num_player
            count += 1

        return None

    def accept_player(self, username, socket_id):
        player = Player(self, username, socket_id)
        self.players.append(player)
        self.num_player += 1

    def wrap_to_response(self, response_content):
        response_content.append(self.num_player)
        for player in self.players:
            response_content.append(player.username)

    def wrap_score_to_response(self, response_content):
        response_content.append(self.num_player)
        for player in self.players:
            response_content.append(player.score)

    def full_player(self):
        return len(self.players) >= NUM_PLAYER

    def username_existed(self, username):
        for player in self.players:
            if player.username == username:
                return True

        return False

    def get_num_player(self):
        return self.num_player
    
    def is_current_turn_for_socket_id(self, socket_id):
        return self.cur_player and self.cur_player.socket_id == socket_id
