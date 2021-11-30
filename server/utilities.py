import re
import random
import os

def is_valid_username(username):
    pattern = re.compile("^[a-zA-Z0-9_]{1,10}$")
    return pattern.match(username)

def load_keyword_and_hint():
    abs_path = os.path.dirname(os.path.abspath(__file__))
    file_path = abs_path + '/database.txt'
    with open(file_path) as f:
        n = int(f.readline())
        db = []
        for _ in range(n):
            keyword = f.readline().strip()
            hint = f.readline().strip()
            db.append((keyword, hint))

        return random.choice(db)
