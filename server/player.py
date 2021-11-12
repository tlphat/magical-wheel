from config import TURN_PER_PLAYER

class Player:
    def __init__(self, game, username, connection_id):
        self.game = game
        self.username = username
        self.connection_id = connection_id
        self.score = 0
        self.turn = TURN_PER_PLAYER
