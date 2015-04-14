#!/usr/bin/python

import SimpleHTTPServer
import SocketServer
import urlparse

import my_logging


logger = my_logging.get_logger('food', _logger_file='food_http_server.log')
PORT = 8000


class MyHttpHandler(SimpleHTTPServer.SimpleHTTPRequestHandler):
    def do_GET(self):
        logger.debug('receive a GET request')
        logger.debug('basic info >>>>>>>')
        client_info = self.get_client_basic_info()
        logger.debug(client_info)
        logger.debug('basic info <<<<<<<')
        self.send_response(200)
        self.end_headers()
        self.wfile.write('''{"psid": 23974, "cnname": "\u6768\u5f6a", "name": "Salt Yang", "enname": "Salt Yang", "depcode": "CHT.5621E"}''')


    def do_POST(self):
        logger.debug('receive a POST request')
        logger.debug('basic info >>>>>>>')
        client_info = self.get_client_basic_info()
        logger.debug(client_info)
        logger.debug('basic info <<<<<<<')
        self.send_response(200)
        self.end_headers()

    def get_client_basic_info(self):
        o = urlparse.urlparse(self.path)
        print 'o: ', o
        print 'params: ', urlparse.parse_qs(o.query)
        return {'client addr': self.client_address,
                'request path': self.path,
                'command': self.command,
                'client content': self.rfile.read(int(self.headers.getheader('content-length', 0)))}



SocketServer.TCPServer(("", PORT), MyHttpHandler).serve_forever()

