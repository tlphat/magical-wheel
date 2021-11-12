import re

def is_valid_username(username):
    pattern = re.compile("^[a-zA-Z0-9_]{1,10}$")
    return pattern.match(username)
