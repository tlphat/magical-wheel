from constants import *
import threading 

class EventManager:
    def __init__(self, game, network_manager):
        self.game = game
        self.network_manager = network_manager
        self.request_queue = []
        self.response_queue = []
        self.request_handler={
                1: self.game.join_game_handler,
                4: self.game.receive_guest_handler
                }
        
        self.lock = threading.Lock()

    def push_request(self, raw_request_data):
        request = self.extract_raw_request(raw_request_data)
        if request:
            self.lock.acquire()
            self.request_queue.append(request)
            self.lock.release()
        else:
            print("Invalid event data: ", raw_request_data)

    def push_response(self, response):
        self.response_queue.append(response)

    def process_request_queue(self):
        if len(self.request_queue) == 0:
            return

        self.lock.acquire()
        for request in self.request_queue:
            print("Handling request: ", request)
            handler = self.request_handler.get(request.type, lambda : 'Not register handler')
            handler(request.data)

        self.request_queue.clear()
        self.lock.release()

    def process_response_queue(self):
        if len(self.response_queue) == 0:
            return

        for response in self.response_queue:
            self.network_manager.send(response)

        self.response_queue.clear()

    def post_process_response_queue(self):
        self.process_response_queue()

    def extract_raw_request(self, raw_request_data):
        try:
            contents = raw_request_data.raw_content.strip().split("\n")
            event_type = int(contents[0])
            return Request(event_type, RequestData(contents[1:], self.network_manager.get_socket_id(raw_request_data.sock)))
        except Exception as e:
            print("Exception in extract_raw_request: ", e)
            return None


