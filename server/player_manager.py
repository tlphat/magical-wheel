from config import *
from player import Player

class PlayerManager:
    def __init__(self, game):
        self.game = game
        self.cur_player_idx = -1
        self.cur_player = None
        self.players = []
        self.num_player = 0

    def get_next_player(self):
        posible_next_player_idx = (self.cur_player_idx + 1) % (self.num_player)

        count = 0
        while count < self.num_player:
            if not self.players[posible_next_player_idx].eliminated():
                self.cur_player_idx = posible_next_player_idx
                self.cur_player = self.players[self.cur_player_idx]
                return self.players[self.cur_player_idx]

            posible_next_player_idx += 1
            posible_next_player_idx %= self.num_player

        return None

    def accept_player(self, username, connection_id):
        player = Player(self, username, connection_id)
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
