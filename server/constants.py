from collections import namedtuple
from enum import Enum

Request = namedtuple('Request', 'type data')
RawRequestData = namedtuple('RawRequestData', 'raw_content sock')
RequestData = namedtuple('RequestData', 'content socket_id')

Response = namedtuple('Response', 'send_type data')
ResponseData = namedtuple('ResponseData', 'content socket_id')

PRIVATE_RESPONSE = 'private'
PUBLIC_RESPONSE = 'public'

EventType = {
        'JOIN_GAME': 1,
        'START_GAME': 2,
        'START_TURN': 3,
        'PLAYER_GUEST': 4,
        'END_GAME': 5,
        }
StatusCode = {
        'SUCCESS': 0,
        'USERNAME_EXISTED': 1,
        'USERNAME_INVALID': 2,
        'SERVER_ERROR': 4,
        }

class GameState(Enum):
    WAITING = 1,
    PRE_START = 2,
    START = 3,
    END = 4
