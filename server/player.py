class Player:
    def __init__(self, game, username, socket_id):
        self.game = game
        self.username = username
        self.socket_id = socket_id
        self.score = 0
        self.is_eliminate = False

    def eliminated(self):
        return self.is_eliminate

    def eliminate(self):
        self.is_eliminate = True

    def update_score(self, delta):
        self.score += delta

