#!/usr/bin/python

import logging


def get_logger(_logger_name, _log_level=logging.DEBUG, _logger_file='', _logger_console=True,
               _format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'):
    logger = logging.getLogger(_logger_name)
    logger.setLevel(_log_level)

    formatter = logging.Formatter(_format)
    if _logger_file != '':
        fh = logging.FileHandler(_logger_file)
        fh.setFormatter(formatter)
        logger.addHandler(fh)

    if _logger_console is True:
        ch = logging.StreamHandler()
        ch.setFormatter(formatter)
        logger.addHandler(ch)

    return logger




