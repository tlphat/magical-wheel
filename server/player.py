from config import TURN_PER_PLAYER

class Player:
    def __init__(self, game, username, socket_id):
        self.game = game
        self.username = username
        self.socket_id = socket_id
        self.score = 0
        self.remaining_turn = TURN_PER_PLAYER
        self.is_eliminate = False

    def eliminated(self):
        return self.is_eliminate or self.remaining_turn <= 0

    def eliminate(self):
        self.eliminate = True

    def take_turn(self):
        self.remaining_turn -= 1

    def update_score(self, delta):
        self.score += delta

