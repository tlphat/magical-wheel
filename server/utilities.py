import re
import random

def is_valid_username(username):
    pattern = re.compile("^[a-zA-Z0-9_]{1,10}$")
    return pattern.match(username)

def load_keyword_and_hint():
    with open("database.txt") as f:
        n = int(f.readline())
        db = []
        for i in range(n):
            keyword = f.readline().strip()
            hint = f.readline().strip()
            db.append((keyword, hint))

        return random.choice(db)
