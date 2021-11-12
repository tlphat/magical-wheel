from collections import namedtuple
from enum import Enum

Request = namedtuple('Request', 'type data')
RawRequestData = namedtuple('RawRequestData', 'raw_content connection_id')
RequestData = namedtuple('RequestData', 'content connection_id')

Response = namedtuple('Response', 'send_type data')
ResponseData = namedtuple('ResponseData', 'content connection_id')

PRIVATE_RESPONSE = 'private'
PUBLIC_RESPONSE = 'public'

EventType = {
        'JOIN_GAME': 1
        }
StatusCode = {
        'SUCCESS': 0,
        'USERNAME_EXISTED': 1,
        'USERNAME_INVALID': 2,
        }
