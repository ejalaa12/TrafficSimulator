import re
from datetime import timedelta


regex = re.compile(
    r'PT((?P<hours>\d+?)H)?((?P<minutes>\d+?)M)?((?P<seconds>\d+\.*\d*?)S)?')


def parse_duration(time_str):
    parts = regex.match(time_str)
    if not parts:
        return
    parts = parts.groupdict()
    time_params = {}
    for (name, param) in parts.iteritems():
        if param:
            time_params[name] = float(param)
    return timedelta(**time_params)


if __name__ == '__main__':
    print parse_duration('PT12H3M1.2S')
    print parse_duration('PT51.024000001S')
    print parse_duration(' PT6S')
