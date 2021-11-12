from config import TURN_PER_PLAYER

class Player:
    def __init__(self, game, username, connection_id):
        self.game = game
        self.username = username
        self.connection_id = connection_id
        self.score = 0
        self.remaining_turn = TURN_PER_PLAYER
        self.eliminate = False

    def eliminated(self):
        return self.eliminate or self.remaining_turn <= 0

    def take_turn(self):
        self.remaining_turn -= 1
